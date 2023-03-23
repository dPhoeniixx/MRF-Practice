package com.dphoeniixx.mrfpractice.http.resposnes;

import com.dphoeniixx.mrfpractice.data.model.Blog;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlogResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private Blog data;

    public String getStatus() {
        return status;
    }

    public Blog getData() {
        return data;
    }

}
