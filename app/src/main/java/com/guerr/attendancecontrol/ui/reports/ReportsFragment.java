package com.guerr.attendancecontrol.ui.reports;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.guerr.attendancecontrol.R;
import com.guerr.attendancecontrol.api.ApiClient;
import com.guerr.attendancecontrol.api.ApiService;
import com.guerr.attendancecontrol.data.model.AbsenteeismReportDTO;
import com.guerr.attendancecontrol.data.model.EarlyDepartureReportDTO;
import com.guerr.attendancecontrol.data.model.LateArrivalReportDTO;
import com.guerr.attendancecontrol.databinding.FragmentReportsBinding;
import com.guerr.attendancecontrol.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsFragment extends Fragment {

    private enum ReportCategory {
        LATE, EARLY, ABSENT
    }

    private FragmentReportsBinding binding;
    private ReportsAdapter adapter;
    private Calendar selectedCalendar;
    private ReportCategory currentCategory = ReportCategory.LATE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectedCalendar = Calendar.getInstance();

        adapter = new ReportsAdapter();
        binding.rvReports.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvReports.setAdapter(adapter);

        updateDateButtonLabel();

        binding.btnSelectDate.setOnClickListener(v -> showDatePicker());

        binding.tabLate.setOnClickListener(v -> selectTab(ReportCategory.LATE));
        binding.tabEarly.setOnClickListener(v -> selectTab(ReportCategory.EARLY));
        binding.tabAbsent.setOnClickListener(v -> selectTab(ReportCategory.ABSENT));

        selectTab(ReportCategory.LATE);
    }

    private void selectTab(ReportCategory category) {
        currentCategory = category;
        if (binding == null) return;

        int activeBg = ContextCompat.getColor(requireContext(), R.color.white);
        int inactiveBg = ContextCompat.getColor(requireContext(), android.R.color.transparent);
        int activeText = ContextCompat.getColor(requireContext(), R.color.primary);
        int inactiveText = ContextCompat.getColor(requireContext(), R.color.text_dark_label);

        // Reset all tabs
        binding.tabLate.setBackgroundColor(inactiveBg);
        binding.tabLate.setTextColor(inactiveText);
        binding.tabEarly.setBackgroundColor(inactiveBg);
        binding.tabEarly.setTextColor(inactiveText);
        binding.tabAbsent.setBackgroundColor(inactiveBg);
        binding.tabAbsent.setTextColor(inactiveText);

        switch (category) {
            case LATE:
                binding.tabLate.setBackgroundColor(activeBg);
                binding.tabLate.setTextColor(activeText);
                binding.tvReportSectionTitle.setText("Reporte General de Atrasos");
                binding.tvReportSectionSubtitle.setText("Umbral: Entradas después de las 09:30 AM");
                break;
            case EARLY:
                binding.tabEarly.setBackgroundColor(activeBg);
                binding.tabEarly.setTextColor(activeText);
                binding.tvReportSectionTitle.setText("Reporte General de Salidas Anticipadas");
                binding.tvReportSectionSubtitle.setText("Umbral: Salidas antes de las 17:30 PM");
                break;
            case ABSENT:
                binding.tabAbsent.setBackgroundColor(activeBg);
                binding.tabAbsent.setTextColor(activeText);
                binding.tvReportSectionTitle.setText("Reporte General de Inasistencias");
                binding.tvReportSectionSubtitle.setText("Sin justificación registrada");
                break;
        }

        loadCurrentReport();
    }

    private void updateDateButtonLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        binding.btnSelectDate.setText("Fecha: " + sdf.format(selectedCalendar.getTime()));
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, month);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateButtonLabel();
                    loadCurrentReport();
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void loadCurrentReport() {
        if (binding == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateStr = sdf.format(selectedCalendar.getTime());

        binding.progressBarReports.setVisibility(View.VISIBLE);
        binding.tvEmptyReports.setVisibility(View.GONE);

        ApiService api = ApiClient.getService(requireContext());

        if (currentCategory == ReportCategory.EARLY) {
            api.getEarlyDepartures(dateStr).enqueue(new Callback<List<EarlyDepartureReportDTO>>() {
                @Override
                public void onResponse(Call<List<EarlyDepartureReportDTO>> call, Response<List<EarlyDepartureReportDTO>> response) {
                    handleResponse(response.body(), ReportsAdapter.ReportType.EARLY);
                }

                @Override
                public void onFailure(Call<List<EarlyDepartureReportDTO>> call, Throwable t) {
                    handleFailure();
                }
            });
        } else if (currentCategory == ReportCategory.ABSENT) {
            api.getAbsenteeism(dateStr).enqueue(new Callback<List<AbsenteeismReportDTO>>() {
                @Override
                public void onResponse(Call<List<AbsenteeismReportDTO>> call, Response<List<AbsenteeismReportDTO>> response) {
                    handleResponse(response.body(), ReportsAdapter.ReportType.ABSENT);
                }

                @Override
                public void onFailure(Call<List<AbsenteeismReportDTO>> call, Throwable t) {
                    handleFailure();
                }
            });
        } else {
            api.getLateArrivals(dateStr).enqueue(new Callback<List<LateArrivalReportDTO>>() {
                @Override
                public void onResponse(Call<List<LateArrivalReportDTO>> call, Response<List<LateArrivalReportDTO>> response) {
                    handleResponse(response.body(), ReportsAdapter.ReportType.LATE);
                }

                @Override
                public void onFailure(Call<List<LateArrivalReportDTO>> call, Throwable t) {
                    handleFailure();
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void handleResponse(List<T> list, ReportsAdapter.ReportType type) {
        if (!isAdded() || binding == null) return;
        binding.progressBarReports.setVisibility(View.GONE);

        List<ReportsAdapter.ReportItem> items = new ArrayList<>();

        if (list != null) {
            for (T obj : list) {
                if (obj instanceof LateArrivalReportDTO) {
                    LateArrivalReportDTO r = (LateArrivalReportDTO) obj;
                    items.add(new ReportsAdapter.ReportItem(
                            r.getFullName(),
                            r.getRut(),
                            "Hora Entrada: " + DateUtils.formatTime(r.getEntry()),
                            r.getDelay() + " minutos",
                            type
                    ));
                } else if (obj instanceof EarlyDepartureReportDTO) {
                    EarlyDepartureReportDTO r = (EarlyDepartureReportDTO) obj;
                    items.add(new ReportsAdapter.ReportItem(
                            r.getFullName(),
                            r.getRut(),
                            "Hora Salida: " + DateUtils.formatTime(r.getDeparture()),
                            r.getDelay() + " minutos",
                            type
                    ));
                } else if (obj instanceof AbsenteeismReportDTO) {
                    AbsenteeismReportDTO r = (AbsenteeismReportDTO) obj;
                    items.add(new ReportsAdapter.ReportItem(
                            r.getFullName(),
                            r.getRut(),
                            "Fecha: " + DateUtils.formatDate(r.getDate()),
                            "Inasistente",
                            type
                    ));
                }
            }
        }

        adapter.setItems(items);
        binding.tvEmptyReports.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void handleFailure() {
        if (!isAdded() || binding == null) return;
        binding.progressBarReports.setVisibility(View.GONE);
        binding.tvEmptyReports.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
