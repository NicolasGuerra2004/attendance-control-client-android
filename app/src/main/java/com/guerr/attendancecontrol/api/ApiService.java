package com.guerr.attendancecontrol.api;

import com.guerr.attendancecontrol.data.model.AbsenteeismReportDTO;
import com.guerr.attendancecontrol.data.model.AttendanceRecordDTO;
import com.guerr.attendancecontrol.data.model.DailyRecordDTO;
import com.guerr.attendancecontrol.data.model.DashboardMetricsDTO;
import com.guerr.attendancecontrol.data.model.EarlyDepartureReportDTO;
import com.guerr.attendancecontrol.data.model.LateArrivalReportDTO;
import com.guerr.attendancecontrol.data.model.LoginRequestDTO;
import com.guerr.attendancecontrol.data.model.LoginResponseDTO;
import com.guerr.attendancecontrol.data.model.RegisterRequestDTO;
import com.guerr.attendancecontrol.data.model.UserCreateDTO;
import com.guerr.attendancecontrol.data.model.UserManagementDTO;
import com.guerr.attendancecontrol.data.model.WeeklyHistoryDTO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // === Autenticación ===
    @POST("api/auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO request);

    @POST("api/auth/register")
    Call<ResponseBody> register(@Body RegisterRequestDTO request);

    @POST("api/auth/logout")
    Call<Void> logout();

    // === Asistencia Usuario (Usamos ResponseBody para evitar error de recursión de JPA en el JSON) ===
    @POST("api/attendance/check-in")
    Call<ResponseBody> checkIn();

    @POST("api/attendance/check-out")
    Call<ResponseBody> checkOut();

    @GET("api/attendance/my-weekly-history")
    Call<List<WeeklyHistoryDTO>> getMyWeeklyHistory();

    @GET("api/attendance/has-checked-in-today")
    Call<Boolean> hasCheckedInToday();

    // === Dashboard (Admin) ===
    @GET("api/attendance/dashboard")
    Call<DashboardMetricsDTO> getDashboardMetrics();

    @GET("api/attendance/today")
    Call<List<DailyRecordDTO>> getTodayRecords();

    // === Reportes (Admin) ===
    @GET("api/attendance/reports/late-arrivals")
    Call<List<LateArrivalReportDTO>> getLateArrivals(@Query("date") String date);

    @GET("api/attendance/reports/early-departures")
    Call<List<EarlyDepartureReportDTO>> getEarlyDepartures(@Query("date") String date);

    @GET("api/attendance/reports/absenteeism")
    Call<List<AbsenteeismReportDTO>> getAbsenteeism(@Query("date") String date);

    // === Gestión de Usuarios (Admin) ===
    @GET("api/users/management")
    Call<List<UserManagementDTO>> getManagementUsers();

    @POST("api/users")
    Call<Void> createUser(@Body UserCreateDTO request);

    @DELETE("api/users/{id}")
    Call<Void> deleteUser(@Path("id") Long id);
}
