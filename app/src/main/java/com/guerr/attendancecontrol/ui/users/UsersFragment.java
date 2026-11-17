package com.guerr.attendancecontrol.ui.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.guerr.attendancecontrol.api.ApiClient;
import com.guerr.attendancecontrol.api.ApiService;
import com.guerr.attendancecontrol.data.model.UserCreateDTO;
import com.guerr.attendancecontrol.data.model.UserManagementDTO;
import com.guerr.attendancecontrol.databinding.DialogAddUserBinding;
import com.guerr.attendancecontrol.databinding.FragmentUsersBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersFragment extends Fragment {

    private FragmentUsersBinding binding;
    private UsersAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new UsersAdapter(this::confirmDeactivateUser);
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvUsers.setAdapter(adapter);

        binding.btnRefreshUsers.setOnClickListener(v -> loadUsers());
        binding.fabAddUser.setOnClickListener(v -> showAddUserDialog());

        loadUsers();
    }

    private void loadUsers() {
        if (binding == null) return;
        binding.progressBarUsers.setVisibility(View.VISIBLE);
        binding.tvEmptyUsers.setVisibility(View.GONE);

        ApiService api = ApiClient.getService(requireContext());
        api.getManagementUsers().enqueue(new Callback<List<UserManagementDTO>>() {
            @Override
            public void onResponse(Call<List<UserManagementDTO>> call, Response<List<UserManagementDTO>> response) {
                if (!isAdded() || binding == null) return;
                binding.progressBarUsers.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<UserManagementDTO> users = response.body();
                    adapter.setItems(users);
                    binding.tvEmptyUsers.setVisibility(users.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    binding.tvEmptyUsers.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<UserManagementDTO>> call, Throwable t) {
                if (!isAdded() || binding == null) return;
                binding.progressBarUsers.setVisibility(View.GONE);
                binding.tvEmptyUsers.setVisibility(View.VISIBLE);
            }
        });
    }

    private void confirmDeactivateUser(UserManagementDTO user) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar desactivación")
                .setMessage("¿Deseas dar de baja a " + user.getFullName() + "?")
                .setPositiveButton("Desactivar", (dialog, which) -> deactivateUser(user))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deactivateUser(UserManagementDTO user) {
        ApiService api = ApiClient.getService(requireContext());
        api.deleteUser(user.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Usuario " + user.getFullName() + " desactivado correctamente", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    Toast.makeText(requireContext(), "Error al desactivar (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de red: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddUserDialog() {
        DialogAddUserBinding dialogBinding = DialogAddUserBinding.inflate(getLayoutInflater());

        new AlertDialog.Builder(requireContext())
                .setTitle("Registrar Nuevo Usuario")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Crear", (dialog, which) -> {
                    String rut = dialogBinding.etRut.getText() != null ? dialogBinding.etRut.getText().toString().trim() : "";
                    String name = dialogBinding.etName.getText() != null ? dialogBinding.etName.getText().toString().trim() : "";
                    String lastName = dialogBinding.etLastName.getText() != null ? dialogBinding.etLastName.getText().toString().trim() : "";
                    String email = dialogBinding.etEmail.getText() != null ? dialogBinding.etEmail.getText().toString().trim() : "";
                    String phone = dialogBinding.etPhone.getText() != null ? dialogBinding.etPhone.getText().toString().trim() : "";
                    String address = dialogBinding.etAddress.getText() != null ? dialogBinding.etAddress.getText().toString().trim() : "";
                    String password = dialogBinding.etPassword.getText() != null ? dialogBinding.etPassword.getText().toString().trim() : "";

                    if (rut.isEmpty() || name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(requireContext(), "Por favor completa los campos obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    UserCreateDTO newUser = new UserCreateDTO(rut, name, lastName, phone, address, email, password);
                    createUser(newUser);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void createUser(UserCreateDTO newUser) {
        ApiService api = ApiClient.getService(requireContext());
        api.createUser(newUser).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    Toast.makeText(requireContext(), "Error al crear usuario (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de red: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
