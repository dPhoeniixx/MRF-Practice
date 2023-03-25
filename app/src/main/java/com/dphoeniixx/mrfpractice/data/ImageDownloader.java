package com.dphoeniixx.mrfpractice.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.dphoeniixx.mrfpractice.data.Utils;

import com.dphoeniixx.mrfpractice.MRFApp;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ImageDownloader {

    private static String   downloadUri;
    private static File     destDir;
    private static String   filename;
    private static File     cacheFile;

    private static final String CACHE_PATH = MRFApp.getContext().getApplicationContext().getCacheDir() + "/image-cache/";

    public ImageDownloader(Uri uri) {
        downloadUri = uri.toString();
        destDir     = new File(CACHE_PATH, Utils.md5(downloadUri.getBytes(StandardCharsets.UTF_8)));
        filename    = downloadUri.substring(downloadUri.lastIndexOf('/') + 1);
        cacheFile   = new File(destDir, filename);
        destDir.mkdirs();
    }

    public Bitmap download() {
        if(isCached()){
            return BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
        }
        try {
            MRFApp.downloadFile(downloadUri, destDir);
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
