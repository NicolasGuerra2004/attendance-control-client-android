package com.guerr.attendancecontrol.api;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // URL base por defecto hacia el backend en Azure (puerto 3040)
    public static final String DEFAULT_BASE_URL = "http://68.211.88.224:3040/";

    private static Retrofit retrofit = null;
    private static ApiService apiService = null;
    private static String currentBaseUrl = DEFAULT_BASE_URL;

    public static synchronized ApiService getService(Context context) {
        if (apiService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context))
                    .addInterceptor(logging)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(currentBaseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    public static synchronized void setBaseUrl(String newBaseUrl, Context context) {
        if (!newBaseUrl.endsWith("/")) {
            newBaseUrl += "/";
        }
        currentBaseUrl = newBaseUrl;
        apiService = null;
        getService(context);
    }

    public static String getBaseUrl() {
        return currentBaseUrl;
    }
}
