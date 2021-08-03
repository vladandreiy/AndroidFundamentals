package com.example.stockwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.stockwatch.Login.LogInActivity;
import com.example.stockwatch.Login.SignUpActivity;

public class SplashScreen extends AppCompatActivity {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        handler = new Handler();
        handler.postDelayed(() -> {
            SharedPreferences pref;
            pref = getSharedPreferences("preference", MODE_PRIVATE);
            if (pref.getBoolean("firststart", true)) {
                Intent intent = new Intent(SplashScreen.this, SignUpActivity.class);
                startActivity(intent);
                finish();

            } else {
                Intent launchlogin = new Intent(SplashScreen.this, LogInActivity.class);
                startActivity(launchlogin);
                finish();
            }
        }, 2000);
    }
}