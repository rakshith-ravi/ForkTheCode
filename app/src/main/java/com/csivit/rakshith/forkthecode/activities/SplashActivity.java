package com.csivit.rakshith.forkthecode.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.csivit.rakshith.forkthecode.R;
import com.csivit.rakshith.forkthecode.model.Data;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Data.initialize(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Data.isLoggedIn()) {
            // open main activity
            if(Data.isJoinedTeam()) {
                if(Data.isMapActivity()) {
                    Intent intent = new Intent(this, MapActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                Intent intent = new Intent(this, TeamActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            // open sign up activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Data.save();
    }
}
