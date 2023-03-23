package com.dphoeniixx.mrfpractice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.dphoeniixx.mrfpractice.data.ImageDownloader;
import com.dphoeniixx.mrfpractice.data.model.Blog;
import com.dphoeniixx.mrfpractice.http.RESTClient;
import com.dphoeniixx.mrfpractice.http.resposnes.BlogResponse;
import com.dphoeniixx.mrfpractice.http.resposnes.BlogsResponse;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;

import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogpostActivity extends FragmentActivity {


    private static RESTClient API = MRFApp.restClient;

    public static final String BLOGPOST_ID = "blogID";

    private String blogID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blog_post);

        Intent intent = getIntent();

        blogID = intent.getStringExtra(BLOGPOST_ID);

        TextView blogTitle = findViewById(R.id.blogTitle);
        TextView blogText = findViewById(R.id.blogText);
        ShapeableImageView blogImage = findViewById(R.id.blogImage);



        API.getBlogById(blogID).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                BlogResponse blogResponse = new Gson().fromJson(response.body().string(), BlogResponse.class);
                Blog blog = blogResponse.getData();

                MRFApp.setText(blogTitle, blog.getTitle());
                MRFApp.setText(blogText, blog.getDescription());

                MRFApp.getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageDownloader imageDownloader = new ImageDownloader(Uri.parse(blog.getImage()));
                        blogImage.setImageBitmap(imageDownloader.download());
                    }
                });
                response.close();
            }
        });

    }

}