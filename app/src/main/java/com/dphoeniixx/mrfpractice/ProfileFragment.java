package com.dphoeniixx.mrfpractice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dphoeniixx.mrfpractice.data.SessionManager;
import com.dphoeniixx.mrfpractice.http.RESTClient;
import com.dphoeniixx.mrfpractice.http.resposnes.ProfileResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    private static final RESTClient API = MRFApp.restClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView helloText = getView().findViewById(R.id.helloText);
        TextView emailText = getView().findViewById(R.id.emailText);
        Button logoutButton = getView().findViewById(R.id.logoutButton);
        Button updateButton = getView().findViewById(R.id.updateButton);

        if(SessionManager.getToken().isEmpty()){
            showFragment(new LoginFragment());
        }

        logoutButton.setOnClickListener(view1 -> {
            SessionManager.setToken("");
            showFragment(new LoginFragment());
            ((Button)getActivity().findViewById(R.id.profileBtn)).setText("Login");
        });

        updateButton.setOnClickListener(v -> API.changeEmail(emailText.getText().toString()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MRFApp.getCurrentActivity().runOnUiThread(() -> Toast.makeText(MRFApp.getContext(), "Unknown error on updating email.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                MRFApp.getCurrentActivity().runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(MRFApp.getContext(), "Email Updated.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MRFApp.getContext(), "Unknown error on updating email.", Toast.LENGTH_SHORT).show();
                    }
                    response.close();
                });
            }
        }));

        API.getProfile().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if(response.isSuccessful()){
                    ProfileResponse profileResponse = new Gson().fromJson(response.body().string(), ProfileResponse.class);
                    MRFApp.setText(helloText, String.format("Hello, %s\nYour Email: %s", profileResponse.getUser().getName(), profileResponse.getUser().getEmail()) );
                } else {
                    ProfileResponse.Error error = new Gson().fromJson(response.body().string(), ProfileResponse.Error.class);
                    MRFApp.setText(helloText, error.getMessage());
                }
                response.close();
            }
        });
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}