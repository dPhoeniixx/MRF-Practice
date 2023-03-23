package com.dphoeniixx.mrfpractice.http.resposnes;

import com.dphoeniixx.mrfpractice.data.model.Blog;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BlogsResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private ArrayList<Blog> data;

    public String getStatus() {
        return status;
    }

    public ArrayList<Blog> getData() {
        return data;
    }

}
