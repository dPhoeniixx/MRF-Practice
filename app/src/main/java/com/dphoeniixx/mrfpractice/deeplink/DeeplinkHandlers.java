package com.dphoeniixx.mrfpractice.deeplink;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.dphoeniixx.mrfpractice.BlogpostActivity;
import com.dphoeniixx.mrfpractice.MRFApp;
import com.dphoeniixx.mrfpractice.MainActivity;
import com.dphoeniixx.mrfpractice.http.RESTClient;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DeeplinkHandlers {
    private static final RESTClient API = MRFApp.restClient;

    public static void handleHome(Uri uri) {
        Intent intent = new Intent(MRFApp.getContext(), MainActivity.class);
        MRFApp.getCurrentActivity().startActivity(intent);
    }

    public static void handleBlog(Uri uri) {
        Intent intent = new Intent(MRFApp.getContext(), BlogpostActivity.class);
        intent.putExtra(BlogpostActivity.BLOGPOST_ID, uri.getPathSegments().get(1));
        MRFApp.getCurrentActivity().startActivity(intent);
    }

    public static void handleRedeem(Uri uri) {
        String redeemCode = uri.getQueryParameter("code");
        API.redeem(redeemCode).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MRFApp.getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MRFApp.getContext(), "Unknown error on redeeming.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                    MRFApp.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(response.isSuccessful()){
                                Toast.makeText(MRFApp.getContext(),"Redeemed.", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(MRFApp.getContext(), "Unknown error on redeeming.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });
    }
}
