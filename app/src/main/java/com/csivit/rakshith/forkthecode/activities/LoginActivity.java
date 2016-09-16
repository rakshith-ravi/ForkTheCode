package com.csivit.rakshith.forkthecode.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.csivit.rakshith.forkthecode.R;
import com.csivit.rakshith.forkthecode.model.RetroAPI;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_login);
    }

    public void onLogin(View view) {
        //TODO
        RetroAPI.NetworkCalls.login()
    }
}
