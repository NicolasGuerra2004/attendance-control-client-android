package com.guerr.attendancecontrol.api;

import android.content.Context;

import com.guerr.attendancecontrol.data.session.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = SessionManager.getInstance(context).getToken();

        if (token != null && !token.trim().isEmpty()) {
            Request authorized = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(authorized);
        }

        return chain.proceed(original);
    }
}
