package com.guerr.attendancecontrol.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.guerr.attendancecontrol.R;
import com.guerr.attendancecontrol.data.model.DailyRecordDTO;
import com.guerr.attendancecontrol.databinding.ItemTodayRecordBinding;
import com.guerr.attendancecontrol.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class TodayRecordAdapter extends RecyclerView.Adapter<TodayRecordAdapter.ViewHolder> {

    private final List<DailyRecordDTO> items = new ArrayList<>();

    public void setItems(List<DailyRecordDTO> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTodayRecordBinding binding = ItemTodayRecordBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTodayRecordBinding binding;

        ViewHolder(ItemTodayRecordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(DailyRecordDTO item) {
            Context context = binding.getRoot().getContext();
            binding.tvUserFullName.setText(item.getFullName() != null ? item.getFullName() : "Sin nombre");
            String entry = item.getFormattedEntryTime();
            String departure = item.getFormattedDepartureTime();
            binding.tvEntry.setText("Entrada: " + (entry != null ? entry : "--:--"));
            binding.tvDeparture.setText("Salida: " + (departure != null ? departure : "--:--"));

            String status = item.getStatus() != null ? item.getStatus().trim().toUpperCase() : "AUSENTE";
            switch (status) {
                case "PRESENTE":
                    binding.tvStatusBadge.setText("Presente");
                    binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.status_present_bg));
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_present_text));
                    break;
                case "ATRASADO":
                    binding.tvStatusBadge.setText("Atrasado");
                    binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.status_late_bg));
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_late_text));
                    break;
                case "SALIDA_ANTICIPADA":
                    binding.tvStatusBadge.setText("Salida Anticipada");
                    binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.status_early_bg));
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_early_text));
                    break;
                case "AUSENTE":
                case "INASISTENTE":
                default:
                    binding.tvStatusBadge.setText("Ausente");
                    binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.status_absent_bg));
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_absent_text));
                    break;
            }
        }
    }
}
