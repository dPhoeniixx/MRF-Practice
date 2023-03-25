package com.dphoeniixx.mrfpractice;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dphoeniixx.mrfpractice.data.SessionManager;
import com.dphoeniixx.mrfpractice.deeplink.DeeplinkHandler;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showFragment(new BlogFragment());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button blogButton = findViewById(R.id.blogBtn);
        Button profileButton = findViewById(R.id.profileBtn);

        if(SessionManager.getToken().isEmpty()) {
            profileButton.setText("Login");
        } else {
            profileButton.setText("Profile");
        }

        blogButton.setOnClickListener(view -> showFragment(new BlogFragment()));

        profileButton.setOnClickListener(view -> {
            if(SessionManager.getToken() == ""){
                showFragment(new LoginFragment());
            }else {
                showFragment(new ProfileFragment());
            }
        });

        if (getIntent().getData() != null){
            DeeplinkHandler deeplinkHandler = new DeeplinkHandler(getIntent().getData());
            deeplinkHandler.handle();
        }


    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}