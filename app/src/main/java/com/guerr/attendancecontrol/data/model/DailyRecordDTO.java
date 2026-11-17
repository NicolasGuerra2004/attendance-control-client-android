package com.guerr.attendancecontrol.data.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public class DailyRecordDTO {
    @SerializedName("userId")
    private Long userId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("entryTime")
    private JsonElement entryTime;

    @SerializedName("departureTime")
    private JsonElement departureTime;

    @SerializedName("status")
    private String status;

    public DailyRecordDTO() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormattedEntryTime() {
        return parseTime(entryTime);
    }

    public String getFormattedDepartureTime() {
        return parseTime(departureTime);
    }

    private String parseTime(JsonElement element) {
        if (element == null || element.isJsonNull()) return null;
        try {
            if (element.isJsonPrimitive()) {
                String str = element.getAsString();
                if (str.length() >= 5) {
                    return str.substring(0, 5);
                }
                return str;
            }
            if (element.isJsonArray()) {
                JsonArray arr = element.getAsJsonArray();
                if (arr.size() >= 2) {
                    return String.format(Locale.US, "%02d:%02d", arr.get(0).getAsInt(), arr.get(1).getAsInt());
                }
            }
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                int hour = obj.has("hour") ? obj.get("hour").getAsInt() : 0;
                int minute = obj.has("minute") ? obj.get("minute").getAsInt() : 0;
                return String.format(Locale.US, "%02d:%02d", hour, minute);
            }
        } catch (Exception ignored) {}
        return null;
    }
}
