package com.guerr.attendancecontrol.data.model;

import com.google.gson.annotations.SerializedName;

public class EarlyDepartureReportDTO {
    @SerializedName("userId")
    private Long userId;

    @SerializedName("rut")
    private String rut;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("departure")
    private String departure;

    @SerializedName("delay")
    private long delay;

    public EarlyDepartureReportDTO() {}

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

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}
