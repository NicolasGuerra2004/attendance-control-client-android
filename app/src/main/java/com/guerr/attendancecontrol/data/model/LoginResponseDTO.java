package com.guerr.attendancecontrol.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponseDTO {
    @SerializedName("token")
    private String token;

    @SerializedName("email")
    private String email;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("rol")
    private String rol;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, String email, String fullName, String rol) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isAdmin() {
        if (rol == null) return false;
        String normalized = rol.trim().toUpperCase().replace("ROLE_", "");
        return "ADMIN".equals(normalized);
    }
}
