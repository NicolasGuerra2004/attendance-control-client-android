package com.guerr.attendancecontrol.ui.reports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.guerr.attendancecontrol.R;
import com.guerr.attendancecontrol.databinding.ItemReportBinding;

import java.util.ArrayList;
import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder> {

    public enum ReportType {
        LATE, EARLY, ABSENT
    }

    public static class ReportItem {
        private final String fullName;
        private final String rut;
        private final String detail;
        private final String badgeText;
        private final ReportType type;

        public ReportItem(String fullName, String rut, String detail, String badgeText, ReportType type) {
            this.fullName = fullName;
            this.rut = rut;
            this.detail = detail;
            this.badgeText = badgeText;
            this.type = type;
        }

        public String getFullName() {
            return fullName;
        }

        public String getRut() {
            return rut;
        }

        public String getDetail() {
            return detail;
        }

        public String getBadgeText() {
            return badgeText;
        }

        public ReportType getType() {
            return type;
        }
    }

    private final List<ReportItem> items = new ArrayList<>();

    public void setItems(List<ReportItem> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReportBinding binding = ItemReportBinding.inflate(
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
        private final ItemReportBinding binding;

        ViewHolder(ItemReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ReportItem item) {
            Context context = binding.getRoot().getContext();
            binding.tvReportName.setText(item.getFullName() != null ? item.getFullName() : "Sin nombre");
            binding.tvReportRut.setText("RUT: " + (item.getRut() != null ? item.getRut() : "--"));
            binding.tvReportDetail.setText(item.getDetail());
            binding.tvReportBadge.setText(item.getBadgeText());

            switch (item.getType()) {
                case LATE:
                    binding.tvReportBadge.setTextColor(ContextCompat.getColor(context, R.color.status_late_text));
                    break;
                case EARLY:
                    binding.tvReportBadge.setTextColor(ContextCompat.getColor(context, R.color.status_early_text));
                    break;
                case ABSENT:
                    binding.tvReportBadge.setTextColor(ContextCompat.getColor(context, R.color.status_absent_text));
                    break;
            }
        }
    }
}
