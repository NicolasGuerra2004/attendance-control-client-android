package com.guerr.attendancecontrol.ui.auth;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.guerr.attendancecontrol.MainActivity;
import com.guerr.attendancecontrol.api.ApiClient;
import com.guerr.attendancecontrol.api.ApiService;
import com.guerr.attendancecontrol.data.model.LoginRequestDTO;
import com.guerr.attendancecontrol.data.model.LoginResponseDTO;
import com.guerr.attendancecontrol.data.session.SessionManager;
import com.guerr.attendancecontrol.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.scrollLogin, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomPadding = Math.max(systemBars.bottom, ime.bottom) + 30;
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);
            return insets;
        });

        sessionManager = SessionManager.getInstance(this);

        if (sessionManager.isLoggedIn()) {
            navigateNext();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        setupAutoScrollOnFocus();
    }

    private void setupAutoScrollOnFocus() {
        View[] fields = { binding.etEmail, binding.etPassword };

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

    private void navigateNext() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        binding.btnLogin.setEnabled(!isLoading);
    }

    private void showError(String message) {
        binding.tvError.setText(message);
        binding.tvError.setVisibility(View.VISIBLE);
        binding.tvSuccess.setVisibility(View.GONE);
    }

    private void hideMessages() {
        binding.tvError.setVisibility(View.GONE);
        binding.tvSuccess.setVisibility(View.GONE);
    }
}
