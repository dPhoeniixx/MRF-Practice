package com.dphoeniixx.mrfpractice.http.resposnes;

import com.dphoeniixx.mrfpractice.data.model.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileResponse {
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("data")
    @Expose
    private User data;

    public String getStatus() {
        return status;
    }

    public User getUser() {
        return data;
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
