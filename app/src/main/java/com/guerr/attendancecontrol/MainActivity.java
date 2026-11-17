package com.guerr.attendancecontrol;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.guerr.attendancecontrol.data.session.SessionManager;
import com.guerr.attendancecontrol.databinding.ActivityMainBinding;
import com.guerr.attendancecontrol.ui.assist.AssistFragment;
import com.guerr.attendancecontrol.ui.auth.LoginActivity;
import com.guerr.attendancecontrol.ui.dashboard.DashboardFragment;
import com.guerr.attendancecontrol.ui.reports.ReportsFragment;
import com.guerr.attendancecontrol.ui.users.UsersFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Manejo limpio de insets: barra de estado arriba y barra de navegación abajo
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            binding.bottomNavigation.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        sessionManager = SessionManager.getInstance(this);

        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setupNavigation();
    }

    private void setupNavigation() {
        boolean isAdmin = sessionManager.isAdmin();

        if (isAdmin) {
            binding.bottomNavigation.setVisibility(View.VISIBLE);
            binding.bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_dashboard) {
                    replaceFragment(new DashboardFragment());
                    return true;
                } else if (itemId == R.id.nav_assist) {
                    replaceFragment(new AssistFragment());
                    return true;
                } else if (itemId == R.id.nav_reports) {
                    replaceFragment(new ReportsFragment());
                    return true;
                } else if (itemId == R.id.nav_users) {
                    replaceFragment(new UsersFragment());
                    return true;
                }
                return false;
            });

            // Los administradores inician por defecto en el Dashboard
            binding.bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        } else {
            // Empleados regulares van directamente a la pantalla de Asistencia
            binding.bottomNavigation.setVisibility(View.GONE);
            replaceFragment(new AssistFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
