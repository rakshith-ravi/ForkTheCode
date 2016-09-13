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
        } else {
            // open sign up activity
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
