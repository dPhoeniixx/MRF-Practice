package com.dphoeniixx.mrfpractice;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dphoeniixx.mrfpractice.data.BlogAdapter;
import com.dphoeniixx.mrfpractice.http.resposnes.BlogsResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BlogFragment extends Fragment {
    private static final String TAG = MainActivity.class.getSimpleName();

    BlogAdapter blogAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MRFApp.restClient.getBlogs().enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ListView listView = getView().findViewById(R.id.blogsListView);
                BlogsResponse blogsResponse = new Gson().fromJson(response.body().string(), BlogsResponse.class);
                MRFApp.getCurrentActivity().runOnUiThread(() -> {
                    blogAdapter = new BlogAdapter(getContext(), blogsResponse.getData());
                    listView.setAdapter(blogAdapter);
                });
                response.close();
            }

            @Override
            public void onFailure(Call call, IOException t) {
                Log.d(TAG, t.getMessage() + " ::: " + call.toString());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false);
    }
}