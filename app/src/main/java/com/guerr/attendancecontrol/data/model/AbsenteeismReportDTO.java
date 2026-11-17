package com.guerr.attendancecontrol.data.model;

import com.google.gson.annotations.SerializedName;

public class AbsenteeismReportDTO {
    @SerializedName("userId")
    private Long userId;

    @SerializedName("rut")
    private String rut;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("date")
    private String date;

    public AbsenteeismReportDTO() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
