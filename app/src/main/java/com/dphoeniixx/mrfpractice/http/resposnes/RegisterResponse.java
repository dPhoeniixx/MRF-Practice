package com.dphoeniixx.mrfpractice.http.resposnes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RegisterResponse {

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

    public class Error {
        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("errors")
        @Expose
        private ArrayList<ValidationError> errors;

        @SerializedName("message")
        @Expose
        private String message;

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public ArrayList<ValidationError> getErrors() {
            return errors;
        }
    }

    public static class ValidationError {
        @SerializedName("value")
        @Expose
        private String value;

        @SerializedName("msg")
        @Expose
        private String msg;

        @SerializedName("param")
        @Expose
        private String param;

        @SerializedName("location")
        @Expose
        private String location;

        public String getMsg() {
            return msg;
        }
    }
}