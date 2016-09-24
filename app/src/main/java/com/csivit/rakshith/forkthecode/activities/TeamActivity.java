package com.csivit.rakshith.forkthecode.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csivit.rakshith.forkthecode.R;
import com.csivit.rakshith.forkthecode.model.Constants;
import com.csivit.rakshith.forkthecode.model.Data;
import com.csivit.rakshith.forkthecode.model.RetroAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class TeamActivity extends AppCompatActivity {

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        ((TextView)findViewById(R.id.username)).setText(Data.getUsername());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Data.save();
    }

    public void onCreateTeam(View view) {
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText teamName = new EditText(this);
        teamName.setHint("Team name");
        final EditText password = new EditText(this);
        password.setHint("Password");
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(teamName, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(password, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        new AlertDialog.Builder(this)
                .setTitle("Create team")
                .setView(linearLayout)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        final android.app.AlertDialog progressDialog = new ProgressDialog.Builder(TeamActivity.this)
                                .setMessage("Creating team")
                                .setTitle("Creating")
                                .setCancelable(false)
                                .show();
                        Subscription subscription = RetroAPI.NetworkCalls.createTeam(teamName.getText().toString(), password.getText().toString())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<JsonObject>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        progressDialog.dismiss();
                                        Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
                                    }

                                    @Override
                                    public void onNext(JsonObject jsonObject) {
                                        progressDialog.dismiss();
                                        if(jsonObject.get("success").getAsBoolean()) {
                                            Data.setJoinedTeam(true);
                                            Data.save();
                                        } else {

                                        }
                                    }
                                });
                        subscriptions.add(subscription);
                    }
                })
                .create()
                .show();
    }

    public void onJoinTeam(View view) {
        final android.app.AlertDialog progressDialog = new ProgressDialog.Builder(TeamActivity.this)
                .setMessage("Getting list of teams")
                .setTitle("Join team")
                .setCancelable(false)
                .show();
        Subscription subscription = RetroAPI.NetworkCalls.getTeams(Data.AuthToken.replaceAll("\"", ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonArray>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
                    }

                    @Override
                    public void onNext(final JsonArray jsonElements) {
                        progressDialog.dismiss();
                        ArrayAdapter<String> teamsAdapter = new ArrayAdapter<>(TeamActivity.this, android.R.layout.select_dialog_singlechoice);
                        final String[] teams = new String[jsonElements.size()];
                        for(int j = 0; j < jsonElements.size(); j++) {
                            teamsAdapter.add(jsonElements.get(j).toString());
                            teams[j] = jsonElements.get(j).toString();
                        }
                        new AlertDialog.Builder(TeamActivity.this)
                                .setTitle("Select the team to join")
                                .setAdapter(teamsAdapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialogInterface, final int i) {
                                        final TextView textView = new TextView(TeamActivity.this);
                                        textView.setHint("Password");
                                        new AlertDialog.Builder(TeamActivity.this)
                                                .setTitle("Enter password")
                                                .setView(textView)
                                                .setCancelable(false)
                                                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(final DialogInterface dialogInterface2, int i2) {
                                                        final android.app.AlertDialog progressDialogInner = new ProgressDialog.Builder(TeamActivity.this)
                                                                .setMessage("Joining")
                                                                .setTitle("Join team")
                                                                .setCancelable(false)
                                                                .show();
                                                        subscriptions.add(RetroAPI.NetworkCalls.joinTeam(teams[i], textView.getText().toString())
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(new Observer<JsonObject>() {
                                                                    @Override
                                                                    public void onCompleted() {

                                                                    }

                                                                    @Override
                                                                    public void onError(Throwable e) {
                                                                        progressDialogInner.dismiss();
                                                                    }

                                                                    @Override
                                                                    public void onNext(JsonObject jsonObject) {
                                                                        progressDialogInner.dismiss();
                                                                        if(jsonObject.get("success").getAsBoolean()) {
                                                                            Data.setJoinedTeam(true);
                                                                            Data.setQuestion(jsonObject.get("questionid").toString(), jsonObject.get("question").toString());
                                                                            Intent intent = new Intent(TeamActivity.this, HomeActivity.class);
                                                                            startActivity(intent);
                                                                            dialogInterface2.dismiss();
                                                                        } else {
                                                                            dialogInterface2.dismiss();
                                                                            new AlertDialog.Builder(TeamActivity.this)
                                                                                    .setTitle("Error")
                                                                                    .setMessage(jsonObject.get("message").toString())
                                                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                                            dialogInterface.dismiss();
                                                                                        }
                                                                                    })
                                                                                    .create()
                                                                                    .show();
                                                                        }
                                                                    }
                                                                }));
                                                    }
                                                })
                                                .create()
                                                .show();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                });
        subscriptions.add(subscription);
    }
}
