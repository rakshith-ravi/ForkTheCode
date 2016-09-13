package com.csivit.rakshith.forkthecode.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.csivit.rakshith.forkthecode.R;
import com.csivit.rakshith.forkthecode.model.Data;

public class TeamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        ((TextView)findViewById(R.id.username)).setText(Data.getUsername());
    }

    public void onCreateTeam(View view) {

    }

    public void onJoinTeam(View view) {

    }
}
