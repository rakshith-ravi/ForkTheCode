package com.csivit.rakshith.forkthecode.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.csivit.rakshith.forkthecode.R;
import com.csivit.rakshith.forkthecode.model.Constants;
import com.csivit.rakshith.forkthecode.model.Data;
import com.csivit.rakshith.forkthecode.model.LocationService;
import com.csivit.rakshith.forkthecode.model.RetroAPI;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class HomeActivity extends AppCompatActivity {

    private EditText answerText;
    private TextView questionText;
    private TextView clueText;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        answerText = (EditText) findViewById(R.id.answer);
        questionText = (TextView) findViewById(R.id.question);
        clueText = (TextView) findViewById(R.id.inventory);
        clueText.setText("Inventory: " + Arrays.toString(Data.getClue().toCharArray()));
        questionText.setText(Data.getQuestion());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Data.save();
    }

    public void onSubmitAnswer(View view) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking your answer");
        progressDialog.setTitle("Checking");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        String answer = answerText.getText().toString().toLowerCase().replaceAll("\\.", "").replaceAll("\\-", "").replaceAll("\"", "");
        String token = "Token " + Data.AuthToken;
        RetroAPI.NetworkCalls.answer(token.replaceAll("\"", ""), Data.getQuestionID(), answer)
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
                        boolean success = jsonObject.get("success").getAsBoolean();
                        if(success) {
                            Log.e(Constants.LOG_TAG, Data.AuthToken);
                            Data.setQuestion(jsonObject.get("questionid").getAsString(), jsonObject.get("question").getAsString());
                            questionText.setText(Data.getQuestion());
                            clueText.setText(Data.getClue());
                            progressDialog.dismiss();
                            Data.setMapActivity(true);
                            Location location = new Location("");
                            location.setLatitude(jsonObject.get("lat").getAsDouble());
                            location.setLongitude(jsonObject.get("long").getAsDouble());
                            Data.setLocation(location);
                            Data.setClue(Data.getClue() + jsonObject.get("letter").toString().replaceAll("\"", ""));
                            Data.save();
                        } else {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(HomeActivity.this)
                                    .setTitle("Invalid answer")
                                    .setMessage(jsonObject.get("message").toString())
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    }
                });
    }

    public void onLeaderboard(View view) {
        final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage("Getting list of teams");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Leaderboard");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.e(Constants.LOG_TAG, Data.AuthToken);
        Subscription subscription = RetroAPI.NetworkCalls.getTeams("Token " + Data.AuthToken.replaceAll("\"", ""))
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
                        ArrayAdapter<String> teamsAdapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.select_dialog_item);
                        for(int j = 0; j < jsonElements.size(); j++) {
                            teamsAdapter.add(jsonElements.get(j).getAsJsonObject().get("name").toString().replaceAll("\"", "") + " - " + jsonElements.get(j).getAsJsonObject().get("question_attempted").toString().replaceAll("\"", ""));
                        }
                        new android.support.v7.app.AlertDialog.Builder(HomeActivity.this)
                                .setTitle("Leaderboard")
                                .setAdapter(teamsAdapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialogInterface, final int i) {

                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setCancelable(true)
                                .create()
                                .show();
                    }
                });
        subscriptions.add(subscription);
    }
}
