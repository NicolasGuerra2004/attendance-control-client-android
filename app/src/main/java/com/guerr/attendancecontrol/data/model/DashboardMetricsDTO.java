package com.guerr.attendancecontrol.data.model;

import com.google.gson.annotations.SerializedName;

public class DashboardMetricsDTO {
    @SerializedName("presentToday")
    private int presentToday;

    @SerializedName("lateToday")
    private int lateToday;

    @SerializedName("absentToday")
    private int absentToday;

    public DashboardMetricsDTO() {}

    public int getPresentToday() {
        return presentToday;
    }

    public void setPresentToday(int presentToday) {
        this.presentToday = presentToday;
    }

    public int getLateToday() {
        return lateToday;
    }

    public void setLateToday(int lateToday) {
        this.lateToday = lateToday;
    }

    public int getAbsentToday() {
        return absentToday;
    }

    public void setAbsentToday(int absentToday) {
        this.absentToday = absentToday;
    }
}
