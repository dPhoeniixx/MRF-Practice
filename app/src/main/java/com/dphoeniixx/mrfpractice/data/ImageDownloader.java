package com.dphoeniixx.mrfpractice.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.dphoeniixx.mrfpractice.data.Utils;

import com.dphoeniixx.mrfpractice.MRFApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class ImageDownloader {

    private static Uri downloadUri;
    private static File cacheFile;
    private static File destFile;

    private static String CACHE_PATH = MRFApp.getContext().getApplicationContext().getCacheDir() + "/image-cache/";

    public ImageDownloader(Uri uri) {
        downloadUri = uri;
        destFile = new File(CACHE_PATH, Utils.md5(downloadUri.toString()));
        cacheFile = new File(destFile, downloadUri.getLastPathSegment());
        destFile.mkdir();
    }

    public Bitmap download() {
        if(isCached()){
            return BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
        }
        try {
            MRFApp.downloadFile(downloadUri.toString(), destFile);
            return BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isCached(){
        return cacheFile.exists();
    }
}
