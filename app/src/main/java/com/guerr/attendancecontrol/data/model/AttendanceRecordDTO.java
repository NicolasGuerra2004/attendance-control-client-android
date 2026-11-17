package com.guerr.attendancecontrol.data.model;

import com.google.gson.annotations.SerializedName;

public class AttendanceRecordDTO {
    @SerializedName("id")
    private Long id;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("userFullName")
    private String userFullName;

    @SerializedName("typeAttendance")
    private String typeAttendance;

    @SerializedName("date")
    private String date;

    @SerializedName("ipOrigin")
    private String ipOrigin;

    public AttendanceRecordDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getTypeAttendance() {
        return typeAttendance;
    }

    public void setTypeAttendance(String typeAttendance) {
        this.typeAttendance = typeAttendance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIpOrigin() {
        return ipOrigin;
    }

    public void setIpOrigin(String ipOrigin) {
        this.ipOrigin = ipOrigin;
    }
}
