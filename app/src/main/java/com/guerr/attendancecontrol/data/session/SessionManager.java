package com.guerr.attendancecontrol.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.guerr.attendancecontrol.data.model.LoginResponseDTO;

public class SessionManager {
    private static final String PREF_NAME = "attendance_auth_pref";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_FULL_NAME = "user_full_name";
    private static final String KEY_ROLE = "user_role";

    private static SessionManager instance;
    private final SharedPreferences preferences;

    private SessionManager(Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void saveSession(LoginResponseDTO response) {
        if (response == null) return;
        preferences.edit()
                .putString(KEY_TOKEN, response.getToken())
                .putString(KEY_EMAIL, response.getEmail())
                .putString(KEY_FULL_NAME, response.getFullName())
                .putString(KEY_ROLE, response.getRol())
                .apply();
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    public String getFullName() {
        return preferences.getString(KEY_FULL_NAME, "");
    }

    public String getRole() {
        return preferences.getString(KEY_ROLE, "");
    }

    public boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.trim().isEmpty();
    }

    public boolean isAdmin() {
        String role = getRole();
        if (role == null) return false;
        String normalized = role.trim().toUpperCase().replace("ROLE_", "");
        return "ADMIN".equals(normalized);
    }

    public void clearSession() {
        preferences.edit().clear().apply();
    }
}
