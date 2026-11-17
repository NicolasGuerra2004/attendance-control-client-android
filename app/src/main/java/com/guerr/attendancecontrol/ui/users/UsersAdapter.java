package com.guerr.attendancecontrol.ui.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.guerr.attendancecontrol.R;
import com.guerr.attendancecontrol.data.model.UserManagementDTO;
import com.guerr.attendancecontrol.databinding.ItemUserBinding;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    public interface OnUserActionListener {
        void onDeactivateUser(UserManagementDTO user);
        void onEditUser(UserManagementDTO user);
    }

    private final List<UserManagementDTO> items = new ArrayList<>();
    private final OnUserActionListener listener;

    public UsersAdapter(OnUserActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<UserManagementDTO> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserBinding binding;

        ViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(UserManagementDTO user, OnUserActionListener listener) {
            Context context = binding.getRoot().getContext();
            binding.tvUserName.setText(user.getFullName() != null ? user.getFullName() : "Sin nombre");
            binding.tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "--");

            boolean isActive = user.isActive();
            if (isActive) {
                binding.tvUserStatusBadge.setText("Activo");
                binding.tvUserStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_present_text));

                binding.btnToggleStatus.setVisibility(View.VISIBLE);
                binding.btnToggleStatus.setText("Eliminar");
                binding.btnToggleStatus.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeactivateUser(user);
                    }
                });
            } else {
                binding.tvUserStatusBadge.setText("Inactivo");
                binding.tvUserStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));

                binding.btnToggleStatus.setVisibility(View.GONE);
            }

            binding.btnEditUser.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditUser(user);
                }
            });
        }
    }
}
