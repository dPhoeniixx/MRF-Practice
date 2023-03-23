package com.dphoeniixx.mrfpractice.http.resposnes;

import com.dphoeniixx.mrfpractice.data.model.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("data")
    @Expose
    private LoginData data = new LoginData();

    public String getStatus() {
        return status;
    }

    public LoginData getData() {
        return data;
    }

    public class LoginData {

        @SerializedName("token")
        @Expose
        private String token;
        @SerializedName("user")
        @Expose
        private User user;

        public String getToken() {
            return token;
        }

        public User getUser() {
            return user;
        }

    }

    public class Error {
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("message")
        @Expose
        private String message;

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

    }
}