package com.guerr.attendancecontrol.ui.assist;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.guerr.attendancecontrol.R;
import com.guerr.attendancecontrol.api.ApiClient;
import com.guerr.attendancecontrol.api.ApiService;
import com.guerr.attendancecontrol.data.model.WeeklyHistoryDTO;
import com.guerr.attendancecontrol.data.session.SessionManager;
import com.guerr.attendancecontrol.databinding.FragmentAssistBinding;
import com.guerr.attendancecontrol.ui.auth.LoginActivity;
import com.guerr.attendancecontrol.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssistFragment extends Fragment {

    private FragmentAssistBinding binding;
    private WeeklyHistoryAdapter historyAdapter;
    private SessionManager sessionManager;
    private Handler clockHandler;
    private Runnable clockRunnable;

    private enum AssistState {
        NOT_STARTED,
        CHECKED_IN,
        COMPLETED
    }

    private AssistState currentState = AssistState.NOT_STARTED;
    private boolean isSubmitting = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAssistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = SessionManager.getInstance(requireContext());

        setupHeader();
        setupRecyclerView();
        startLiveClock();

        binding.btnRefreshHistory.setOnClickListener(v -> loadAttendanceData());
        binding.btnHeaderLogout.setOnClickListener(v -> confirmLogout());

        binding.btnCheckIn.setOnClickListener(v -> handleAttendance(true));
        binding.btnCheckOut.setOnClickListener(v -> handleAttendance(false));

        loadAttendanceData();
    }

    private void setupHeader() {
        String fullName = sessionManager.getFullName().trim();
        String initials = "U";
        if (!fullName.isEmpty()) {
            String[] parts = fullName.split("\\s+");
            if (parts.length >= 2) {
                initials = (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase(Locale.getDefault());
            } else {
                initials = parts[0].substring(0, 1).toUpperCase(Locale.getDefault());
            }
        }
        binding.tvAvatarInitials.setText(initials);
    }

    private void setupRecyclerView() {
        historyAdapter = new WeeklyHistoryAdapter();
        binding.rvWeeklyHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvWeeklyHistory.setAdapter(historyAdapter);
    }

    private void startLiveClock() {
        clockHandler = new Handler(Looper.getMainLooper());
        clockRunnable = new Runnable() {
            @Override
            public void run() {
                if (binding != null) {
                    Date now = new Date();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d 'de' MMMM", new Locale("es", "CL"));

                    binding.tvLiveClock.setText(timeFormat.format(now));
                    String dateFormatted = dateFormat.format(now);
                    if (!dateFormatted.isEmpty()) {
                        dateFormatted = dateFormatted.substring(0, 1).toUpperCase(new Locale("es", "CL")) + dateFormatted.substring(1);
                    }
                    binding.tvCurrentDate.setText(dateFormatted);

                    clockHandler.postDelayed(this, 1000);
                }
            }
        };
        clockHandler.post(clockRunnable);
    }

    private void loadAttendanceData() {
        ApiService api = ApiClient.getService(requireContext());

        api.getMyWeeklyHistory().enqueue(new Callback<List<WeeklyHistoryDTO>>() {
            @Override
            public void onResponse(Call<List<WeeklyHistoryDTO>> call, Response<List<WeeklyHistoryDTO>> response) {
                if (!isAdded() || binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<WeeklyHistoryDTO> history = response.body();
                    historyAdapter.setItems(history);
                    binding.tvEmptyHistory.setVisibility(history.isEmpty() ? View.VISIBLE : View.GONE);

                    String today = DateUtils.getTodayIso();
                    WeeklyHistoryDTO todayRecord = null;
                    for (WeeklyHistoryDTO rec : history) {
                        if (rec.getDate() != null && rec.getDate().startsWith(today)) {
                            todayRecord = rec;
                            break;
                        }
                    }

                    if (todayRecord != null && todayRecord.getFormattedDepartureTime() != null) {
                        updateUiState(AssistState.COMPLETED);
                    } else {
                        checkTodayCheckInStatus();
                    }
                } else {
                    checkTodayCheckInStatus();
                }
            }

            @Override
            public void onFailure(Call<List<WeeklyHistoryDTO>> call, Throwable t) {
                if (!isAdded() || binding == null) return;
                checkTodayCheckInStatus();
            }
        });
    }

    private void checkTodayCheckInStatus() {
        ApiService api = ApiClient.getService(requireContext());
        api.hasCheckedInToday().enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (!isAdded() || binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    boolean hasCheckedIn = response.body();
                    if (currentState != AssistState.COMPLETED) {
                        updateUiState(hasCheckedIn ? AssistState.CHECKED_IN : AssistState.NOT_STARTED);
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                // Mantener estado
            }
        });
    }

    private void handleAttendance(boolean isCheckIn) {
        if (isSubmitting) return;

        isSubmitting = true;
        binding.progressBarAction.setVisibility(View.VISIBLE);
        binding.btnCheckIn.setEnabled(false);
        binding.btnCheckOut.setEnabled(false);
        binding.tvFeedback.setVisibility(View.GONE);

        ApiService api = ApiClient.getService(requireContext());
        Call<ResponseBody> call = isCheckIn ? api.checkIn() : api.checkOut();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                isSubmitting = false;
                if (!isAdded() || binding == null) return;

                binding.progressBarAction.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    if (isCheckIn) {
                        showFeedback("¡Entrada registrada correctamente!", false);
                        updateUiState(AssistState.CHECKED_IN);
                    } else {
                        showFeedback("¡Salida registrada correctamente!", false);
                        updateUiState(AssistState.COMPLETED);
                    }
                    loadAttendanceData();
                } else {
                    showFeedback("No se pudo registrar la marca (Error " + response.code() + ")", true);
                    updateUiState(currentState);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                isSubmitting = false;
                if (!isAdded() || binding == null) return;

                binding.progressBarAction.setVisibility(View.GONE);
                showFeedback("Error de conexión: " + t.getLocalizedMessage(), true);
                updateUiState(currentState);
            }
        });
    }

    private void updateUiState(AssistState state) {
        this.currentState = state;
        if (!isAdded() || binding == null) return;

        int disabledBg = ContextCompat.getColor(requireContext(), R.color.btn_disabled_bg);
        int disabledText = ContextCompat.getColor(requireContext(), R.color.btn_disabled_text);

        switch (state) {
            case NOT_STARTED:
                binding.btnCheckIn.setEnabled(true);
                binding.btnCheckIn.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.btn_checkin)));
                binding.btnCheckIn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

                binding.btnCheckOut.setEnabled(false);
                binding.btnCheckOut.setBackgroundTintList(ColorStateList.valueOf(disabledBg));
                binding.btnCheckOut.setTextColor(disabledText);
                break;

            case CHECKED_IN:
                binding.btnCheckIn.setEnabled(false);
                binding.btnCheckIn.setBackgroundTintList(ColorStateList.valueOf(disabledBg));
                binding.btnCheckIn.setTextColor(disabledText);

                binding.btnCheckOut.setEnabled(true);
                binding.btnCheckOut.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.btn_checkout)));
                binding.btnCheckOut.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                break;

            case COMPLETED:
                binding.btnCheckIn.setEnabled(false);
                binding.btnCheckIn.setBackgroundTintList(ColorStateList.valueOf(disabledBg));
                binding.btnCheckIn.setTextColor(disabledText);

                binding.btnCheckOut.setEnabled(false);
                binding.btnCheckOut.setBackgroundTintList(ColorStateList.valueOf(disabledBg));
                binding.btnCheckOut.setTextColor(disabledText);
                break;
        }
    }

    private void showFeedback(String message, boolean isError) {
        if (binding == null) return;
        binding.tvFeedback.setText(message);
        binding.tvFeedback.setVisibility(View.VISIBLE);
        int bg = isError ? R.color.status_absent_bg : R.color.status_present_bg;
        int text = isError ? R.color.status_absent_text : R.color.status_present_text;
        binding.tvFeedback.setBackgroundColor(ContextCompat.getColor(requireContext(), bg));
        binding.tvFeedback.setTextColor(ContextCompat.getColor(requireContext(), text));
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas salir?")
                .setPositiveButton("Salir", (dialog, which) -> {
                    sessionManager.clearSession();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (clockHandler != null && clockRunnable != null) {
            clockHandler.removeCallbacks(clockRunnable);
        }
        binding = null;
    }
}
