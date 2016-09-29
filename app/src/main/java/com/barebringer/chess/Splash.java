package com.barebringer.chess;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this, MainActivity.class));
                finish();
            }
        }, 1500);
        SharedPreferences sharedPreferences = getSharedPreferences("Chess", MODE_PRIVATE);
        if (sharedPreferences.getString("BoardState", "").equals("")) {
            Toast.makeText(getApplicationContext(), "No save file", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Save file detected", Toast.LENGTH_SHORT).show();
        }
    }
}
