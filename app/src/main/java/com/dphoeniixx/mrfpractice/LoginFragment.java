package com.dphoeniixx.mrfpractice;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dphoeniixx.mrfpractice.data.SessionManager;
import com.dphoeniixx.mrfpractice.http.RESTClient;
import com.dphoeniixx.mrfpractice.http.resposnes.LoginResponse;
import com.dphoeniixx.mrfpractice.http.resposnes.RegisterResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Callback;


public class LoginFragment extends Fragment {

    private static final RESTClient API = MRFApp.restClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView email = getView().findViewById(R.id.editTextTextEmailAddress);
        TextView password = getView().findViewById(R.id.editTextTextPassword);
        TextView name = getView().findViewById(R.id.editTextName);
        TextView message = getView().findViewById(R.id.message);

        Button loginButton = getView().findViewById(R.id.loginActionBtn);
        Button registerButton = getView().findViewById(R.id.registerActionBtn);

        loginButton.setOnClickListener(view12 -> API.login(email.getText().toString(), password.getText().toString()).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                 Log.d("ttt", e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = new Gson().fromJson(response.body().string(), LoginResponse.class);
                    SessionManager.setToken(loginResponse.getData().getToken());
                    Button profileBtn = getActivity().findViewById(R.id.profileBtn);
                    MRFApp.setText(profileBtn, "Profile");
                    showFragment(new ProfileFragment());
                } else {
                    LoginResponse.Error error = new Gson().fromJson(response.body().string(), LoginResponse.Error.class);
                    MRFApp.setText(message, error.getMessage());
                }
            }
        }));
        registerButton.setOnClickListener(view1 -> API.register(name.getText().toString(), email.getText().toString(), password.getText().toString()).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    RegisterResponse registerResponse = new Gson().fromJson(response.body().string(), RegisterResponse.class);
                    MRFApp.setText(message, registerResponse.getStatus() + "\nTry login now!");
                } else {
                    RegisterResponse.Error error = new Gson().fromJson(response.body().string(), RegisterResponse.Error.class);
                    String errorString = "";
                    if (error.getErrors() != null) {
                        for (RegisterResponse.ValidationError errorVal : error.getErrors()) {
                            errorString += errorVal.getMsg() + "\n";
                        }
                    } else {
                        errorString = error.getMessage();
                    }
                    MRFApp.setText(message, errorString);
                }
                response.close();
            }
        }));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
}