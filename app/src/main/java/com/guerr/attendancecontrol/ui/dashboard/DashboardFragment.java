package com.guerr.attendancecontrol.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.guerr.attendancecontrol.api.ApiClient;
import com.guerr.attendancecontrol.api.ApiService;
import com.guerr.attendancecontrol.data.model.DailyRecordDTO;
import com.guerr.attendancecontrol.data.model.DashboardMetricsDTO;
import com.guerr.attendancecontrol.databinding.FragmentDashboardBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private TodayRecordAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new TodayRecordAdapter();
        binding.rvTodayRecords.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTodayRecords.setAdapter(adapter);

        binding.btnRefreshDashboard.setOnClickListener(v -> loadData());

        loadData();
    }

    private void loadData() {
        if (binding == null) return;
        binding.progressBarDashboard.setVisibility(View.VISIBLE);

        ApiService api = ApiClient.getService(requireContext());

        // 1. Cargar métricas
        api.getDashboardMetrics().enqueue(new Callback<DashboardMetricsDTO>() {
            @Override
            public void onResponse(Call<DashboardMetricsDTO> call, Response<DashboardMetricsDTO> response) {
                if (!isAdded() || binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    DashboardMetricsDTO m = response.body();
                    binding.tvPresentCount.setText(String.valueOf(m.getPresentToday()));
                    binding.tvLateCount.setText(String.valueOf(m.getLateToday()));
                    binding.tvAbsentCount.setText(String.valueOf(m.getAbsentToday()));
                }
            }

            @Override
            public void onFailure(Call<DashboardMetricsDTO> call, Throwable t) {
                // Silencioso o mantener valores
            }
        });

        // 2. Cargar registros del día
        api.getTodayRecords().enqueue(new Callback<List<DailyRecordDTO>>() {
            @Override
            public void onResponse(Call<List<DailyRecordDTO>> call, Response<List<DailyRecordDTO>> response) {
                if (!isAdded() || binding == null) return;
                binding.progressBarDashboard.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<DailyRecordDTO> list = response.body();
                    adapter.setItems(list);
                    binding.tvEmptyToday.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    binding.tvEmptyToday.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<DailyRecordDTO>> call, Throwable t) {
                if (!isAdded() || binding == null) return;
                binding.progressBarDashboard.setVisibility(View.GONE);
                binding.tvEmptyToday.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
