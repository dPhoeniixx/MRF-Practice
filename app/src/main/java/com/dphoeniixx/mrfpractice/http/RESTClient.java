package com.dphoeniixx.mrfpractice.http;

import android.content.Context;
import android.util.Log;

import com.dphoeniixx.mrfpractice.MRFApp;
import com.dphoeniixx.mrfpractice.R;
import com.dphoeniixx.mrfpractice.data.SessionManager;
import com.dphoeniixx.mrfpractice.http.resposnes.LoginResponse;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.CertificatePinner;
import okhttp3.Connection;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okio.BufferedSink;

public class RESTClient {
    private static String BASE_URL              = "https://52.139.154.230:8443/api/v1";

    private static String LOGIN_ENDPOINT        = "/user/login";
    private static String REGISTER_ENDPOINT     = "/user/register";
    private static String UPDATE_ENDPOINT       = "/user/update";
    private static String PROFILE_ENDPOINT      = "/user/profile";
    private static String GET_BLOG_ENDPOINT     = "/blogs/";
    private static String GET_BLOGS_ENDPOINT    = "/blogs";
    private static String REDEEM_ENDPOINT       = "/user/redeem/";

    private static OkHttpClient client;
    private static Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if(SessionManager.getToken() != ""){
                request = request.newBuilder()
                        .header("Authorization", "Bearer " + SessionManager.getToken())
                        .build();
            }
            return chain.proceed(request);
        }
    };


    TrustManager TRUST_ALL_CERTS = new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[] {};
        }
    };

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
    }

    public static SSLContext getSslContext(Context context) throws Exception {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType()); // "BKS"
        ks.load(null, null);

        InputStream is = context.getResources().openRawResource(R.raw.certificate);
        String certificate = convertStreamToString(is);

        // generate input stream for certificate factory
        InputStream stream = IOUtils.toInputStream(certificate);

        // CertificateFactory
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // certificate
        Certificate ca;
        try {
            ca = cf.generateCertificate(stream);
        } finally {
            is.close();
        }

        ks.setCertificateEntry("my-ca", ca);

        // TrustManagerFactory
        String algorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
        // Create a TrustManager that trusts the CAs in our KeyStore
        tmf.init(ks);

        // Create a SSLContext with the certificate that uses tmf (TrustManager)
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());

        return sslContext;
    }

    public RESTClient() {
        try {
            client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addNetworkInterceptor(interceptor)
                    .sslSocketFactory(getSslContext(MRFApp.getContext()).getSocketFactory())
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

        Request request = new Request.Builder()
                .url(BASE_URL + LOGIN_ENDPOINT)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        return call;
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
        Call call = client.newCall(request);
        return call;
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
        Call call = client.newCall(request);
        return call;
    }

    public Call changeEmail(String email) {
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + UPDATE_ENDPOINT)
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        return call;
    }

    public Call getProfile() {
        Request request = new Request.Builder()
                .url(BASE_URL + PROFILE_ENDPOINT)
                .build();
        Call call = client.newCall(request);
        return call;
    }

    public Call getBlogById(String blogId) {
        Request request = new Request.Builder()
                .url(BASE_URL + GET_BLOG_ENDPOINT + blogId)
                .build();
        Call call = client.newCall(request);
        return call;
    }

    public Call getBlogs() {
        Request request = new Request.Builder()
                .url(BASE_URL + GET_BLOGS_ENDPOINT)
                .build();
        Call call = client.newCall(request);
        return call;
    }
}
