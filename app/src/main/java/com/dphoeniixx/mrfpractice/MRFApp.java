package com.dphoeniixx.mrfpractice;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.dphoeniixx.mrfpractice.http.RESTClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

public class MRFApp extends Application implements ContextProvider {

    public static RESTClient restClient;
    private static Context context;
    private static Activity currentActivity;
    private static String[] requiredLibraries = new String[]{"libcrypto.so", "libssl.so"};

    @Override
    public Context getActivityContext() {
        return currentActivity;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        restClient = new RESTClient();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        File libDir = new File(MRFApp.getContext().getApplicationContext().getApplicationInfo().dataDir, "libs/" + Build.SUPPORTED_ABIS[0]);

        if(!prefs.getBoolean("firstTime", false)) {
            libDir.mkdirs();
            try {
                for (String library: requiredLibraries){
                    downloadFile("http://52.139.154.230:8080/libs/" + Build.SUPPORTED_ABIS[0] + "/" + library + "?ItsNotTheRCE-MiTM-Solution-Not-Accepted.", libDir);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

        for(String library: requiredLibraries){
            System.load(libDir.toString() + "/" + library);
        }

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                MRFApp.this.currentActivity = activity;
            }

            @Override
            public void onActivityStarted(Activity activity) {
                MRFApp.this.currentActivity = activity;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                MRFApp.this.currentActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                MRFApp.this.currentActivity = null;
            }

            @Override
            public void onActivityStopped(Activity activity) {
                // don't clear current activity because activity may get stopped after
                // the new activity is resumed
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                // don't clear current activity because activity may get destroyed after
                // the new activity is resumed
            }
        });

    }

    public static Context getContext(){
        return context;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setText(final TextView text,final String value){
        MRFApp.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

    public static void downloadFile(String url, File dest) throws Exception {
        URL urlDownloader = new URL(url);
        InputStream inputStream = urlDownloader.openStream();
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        FileOutputStream fos = new FileOutputStream(dest.toString() + "/" + Uri.parse(url).getLastPathSegment());
        byte[] data = new byte[1024];
        int count;
        while ((count = bis.read(data, 0, 1024)) != -1) {
            fos.write(data, 0, count);
        }
    }
}
