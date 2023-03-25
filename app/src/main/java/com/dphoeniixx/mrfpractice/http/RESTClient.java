package com.dphoeniixx.mrfpractice.http;

import android.content.Context;
import android.util.Log;

import com.dphoeniixx.mrfpractice.MRFApp;
import com.dphoeniixx.mrfpractice.R;
import com.dphoeniixx.mrfpractice.data.SessionManager;
import com.dphoeniixx.mrfpractice.data.Utils;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.CertificatePinner;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class RESTClient {
    private static final String BASE_URL              = "https://52.139.154.230:8443/api/v1";

    private static final String REGISTER_ENDPOINT     = "/user/register";
    private static final String UPDATE_ENDPOINT       = "/user/update";
    private static final String PROFILE_ENDPOINT      = "/user/profile";
    private static final String GET_BLOG_ENDPOINT     = "/blogs/";
    private static final String GET_BLOGS_ENDPOINT    = "/blogs";
    private static final String REDEEM_ENDPOINT       = "/user/redeem/";

    private static OkHttpClient     client;

    private static final Interceptor interceptor = chain -> {
        Request request = chain.request();
        Log.d("ENTR", request.url().toString());
        if(SessionManager.getToken() != ""){
            request = request.newBuilder()
                    .header("Authorization", "Bearer " + SessionManager.getToken())
                    .build();
        }
        return chain.proceed(request);
    };

    private static X509TrustManager trustManagerForCertificates(InputStream in)
            throws GeneralSecurityException, IOException {

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Certificate certificate = certificateFactory.generateCertificate(in);

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("0", certificate);

        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, null);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }


        final X509TrustManager finalTm = (X509TrustManager) trustManagers[0];
        X509TrustManager customTm = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return finalTm.getAcceptedIssuers();
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
                if (chain[0].equals(certificate)) {
                    finalTm.checkServerTrusted(chain, authType);
                }
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
                finalTm.checkClientTrusted(chain, authType);
            }
        };


        return customTm;
    }


    public static SSLSocketFactory getSslContext(X509TrustManager trustManager) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { trustManager }, null);
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        return sslSocketFactory;
    }

    public RESTClient() {
        try {
            InputStream stream = MRFApp.getContext().getResources().openRawResource(R.raw.certificate);
            X509TrustManager trustManager = trustManagerForCertificates(stream);
            client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addNetworkInterceptor(interceptor)
                    .sslSocketFactory(getSslContext(trustManager), trustManager)
                    .certificatePinner(new CertificatePinner.Builder()
                            .add("52.139.154.230", "sha256/G34i6LjdY5oBYD33LSpgsg8MIVsOQeilOmlFcngZP7M=")
                            .build())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Call login(String email, String password) {
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        String LOGIN_ENDPOINT = "/user/login";
        Request request = new Request.Builder()
                .url(BASE_URL + LOGIN_ENDPOINT)
                .post(requestBody)
                .build();
        return client.newCall(request);
    }

    public Call register(String name, String email, String password) {
        RequestBody requestBody = new FormBody.Builder()
                .add("name", name)
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + REGISTER_ENDPOINT)
                .post(requestBody)
                .build();
        return client.newCall(request);
    }

    public Call redeem(String code) {
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {

            }
        };

        Request request = new Request.Builder()
                .url(BASE_URL + REDEEM_ENDPOINT + code)
                .post(requestBody)
                .build();
        return client.newCall(request);
    }

    public Call changeEmail(String email) {
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + UPDATE_ENDPOINT)
                .post(requestBody)
                .build();

        return client.newCall(request);
    }

    public Call getProfile() {
        Request request = new Request.Builder()
                .url(BASE_URL + PROFILE_ENDPOINT)
                .build();
        return client.newCall(request);
    }

    public Call getBlogById(String blogId) {
        Request request = new Request.Builder()
                .url(BASE_URL + GET_BLOG_ENDPOINT + blogId)
                .build();
        return client.newCall(request);
    }

    public Call getBlogs() {
        Request request = new Request.Builder()
                .url(BASE_URL + GET_BLOGS_ENDPOINT)
                .build();
        return client.newCall(request);
    }
}
