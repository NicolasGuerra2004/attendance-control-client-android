package com.guerr.attendancecontrol.ui.auth;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.guerr.attendancecontrol.MainActivity;
import com.guerr.attendancecontrol.R;
import com.guerr.attendancecontrol.api.ApiClient;
import com.guerr.attendancecontrol.api.ApiService;
import com.guerr.attendancecontrol.data.model.LoginRequestDTO;
import com.guerr.attendancecontrol.data.model.LoginResponseDTO;
import com.guerr.attendancecontrol.data.model.RegisterRequestDTO;
import com.guerr.attendancecontrol.data.session.SessionManager;
import com.guerr.attendancecontrol.databinding.ActivityLoginBinding;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager sessionManager;
    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Manejo de insets para barra de estado y teclado (evita que el teclado tape los inputs)
        ViewCompat.setOnApplyWindowInsetsListener(binding.scrollLogin, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomPadding = Math.max(systemBars.bottom, ime.bottom) + 120;
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);
            return insets;
        });

        sessionManager = SessionManager.getInstance(this);

        if (sessionManager.isLoggedIn()) {
            navigateNext();
            return;
        }

        binding.tabLogin.setOnClickListener(v -> setMode(true));
        binding.tabRegister.setOnClickListener(v -> setMode(false));

        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        binding.btnRegister.setOnClickListener(v -> attemptRegister());
        binding.btnConfigServer.setOnClickListener(v -> showServerConfigDialog());

        setupAutoScrollOnFocus();
    }

    private void setupAutoScrollOnFocus() {
        View[] fields = {
                binding.etEmail, binding.etPassword,
                binding.etRegRut, binding.etRegName, binding.etRegLastName,
                binding.etRegEmail, binding.etRegPhone, binding.etRegAddress, binding.etRegPassword
        };

        for (View field : fields) {
            field.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    binding.scrollLogin.postDelayed(() -> {
                        if (binding == null) return;
                        Rect rect = new Rect();
                        v.getDrawingRect(rect);
                        try {
                            binding.scrollLogin.offsetDescendantRectToMyCoords(v, rect);
                            binding.scrollLogin.smoothScrollTo(0, Math.max(0, rect.top - 120));
                        } catch (Exception ignored) {}
                    }, 250);
                }
            });
        }
    }

    private void setMode(boolean loginMode) {
        this.isLoginMode = loginMode;
        hideMessages();

        if (loginMode) {
            binding.formLogin.setVisibility(View.VISIBLE);
            binding.formRegister.setVisibility(View.GONE);

            binding.tabLogin.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            binding.tabLogin.setTextColor(ContextCompat.getColor(this, R.color.text_primary));

            binding.tabRegister.setBackgroundTintList(ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
            binding.tabRegister.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            binding.formLogin.setVisibility(View.GONE);
            binding.formRegister.setVisibility(View.VISIBLE);

            binding.tabRegister.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            binding.tabRegister.setTextColor(ContextCompat.getColor(this, R.color.text_primary));

            binding.tabLogin.setBackgroundTintList(ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
            binding.tabLogin.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        }
    }

    private void attemptLogin() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        if (email.isEmpty()) {
            showError("Por favor ingresa tu correo electrónico.");
            return;
        }
        if (password.isEmpty()) {
            showError("Por favor ingresa tu contraseña.");
            return;
        }

        setLoading(true);
        hideMessages();

        ApiService apiService = ApiClient.getService(this);
        LoginRequestDTO request = new LoginRequestDTO(email, password);

        apiService.login(request).enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponseDTO session = response.body();
                    sessionManager.saveSession(session);
                    Toast.makeText(LoginActivity.this, "¡Bienvenido, " + session.getFullName() + "!", Toast.LENGTH_SHORT).show();
                    navigateNext();
                } else if (response.code() == 401 || response.code() == 403) {
                    showError("Credenciales inválidas. Verifica tu correo y contraseña.");
                } else {
                    showError("Error en el servidor (" + response.code() + "). Intenta más tarde.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                setLoading(false);
                showError("No se pudo conectar con el servidor: " + t.getLocalizedMessage());
            }
        });
    }

    private void attemptRegister() {
        String rut = binding.etRegRut.getText() != null ? binding.etRegRut.getText().toString().trim() : "";
        String name = binding.etRegName.getText() != null ? binding.etRegName.getText().toString().trim() : "";
        String lastName = binding.etRegLastName.getText() != null ? binding.etRegLastName.getText().toString().trim() : "";
        String email = binding.etRegEmail.getText() != null ? binding.etRegEmail.getText().toString().trim() : "";
        String phone = binding.etRegPhone.getText() != null ? binding.etRegPhone.getText().toString().trim() : "";
        String address = binding.etRegAddress.getText() != null ? binding.etRegAddress.getText().toString().trim() : "";
        String password = binding.etRegPassword.getText() != null ? binding.etRegPassword.getText().toString().trim() : "";

        if (rut.isEmpty() || name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Por favor completa todos los campos obligatorios.");
            return;
        }

        setLoading(true);
        hideMessages();

        RegisterRequestDTO req = new RegisterRequestDTO(rut, name, lastName, phone, address, email, password);
        ApiService apiService = ApiClient.getService(this);

        apiService.register(req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    setMode(true);
                    binding.etEmail.setText(email);
                    binding.etPassword.setText("");
                    showSuccess("¡Cuenta registrada exitosamente! Ahora puedes iniciar sesión.");
                } else {
                    showError("No fue posible registrar la cuenta. Verifica que el RUT y correo no estén duplicados.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                setLoading(false);
                showError("Error de conexión: " + t.getLocalizedMessage());
            }
        });
    }

    private void navigateNext() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showServerConfigDialog() {
        EditText input = new EditText(this);
        input.setText(ApiClient.getBaseUrl());
        input.setSingleLine(true);
        input.setPadding(40, 20, 40, 20);

        new AlertDialog.Builder(this)
                .setTitle("Configuración de Servidor")
                .setMessage("Ingresa la URL base del backend:")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String url = input.getText().toString().trim();
                    if (!url.isEmpty()) {
                        ApiClient.setBaseUrl(url, this);
                        Toast.makeText(this, "Servidor actualizado a: " + ApiClient.getBaseUrl(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!isLoading);
        binding.btnRegister.setEnabled(!isLoading);
    }

    private void showError(String message) {
        binding.tvError.setText(message);
        binding.tvError.setVisibility(View.VISIBLE);
        binding.tvSuccess.setVisibility(View.GONE);
    }

    private void showSuccess(String message) {
        binding.tvSuccess.setText(message);
        binding.tvSuccess.setVisibility(View.VISIBLE);
        binding.tvError.setVisibility(View.GONE);
    }

    private void hideMessages() {
        binding.tvError.setVisibility(View.GONE);
        binding.tvSuccess.setVisibility(View.GONE);
    }
}
