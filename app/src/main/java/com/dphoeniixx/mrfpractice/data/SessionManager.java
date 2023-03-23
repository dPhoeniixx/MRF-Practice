package com.dphoeniixx.mrfpractice.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import com.dphoeniixx.mrfpractice.MRFApp;

public class SessionManager {
    private static SharedPreferences sharedPreferences = MRFApp.getContext().getSharedPreferences("user", MODE_PRIVATE);
    private static String TOKEN_NAME = "auth_token";

    public static String getToken() {
        return sharedPreferences.getString(TOKEN_NAME, "");
    }

    public static void setToken(String token){
        sharedPreferences.edit().putString(TOKEN_NAME, token).apply();
    }
}
