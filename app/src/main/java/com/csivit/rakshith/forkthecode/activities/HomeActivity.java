package com.csivit.rakshith.forkthecode.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.google.gson.JsonObject;

import java.util.Timer;
import java.util.TimerTask;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {

    private EditText answerText;
    private TextView questionText;
    private TextView clueText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        answerText = (EditText) findViewById(R.id.answer);
        questionText = (TextView) findViewById(R.id.question);
        clueText = (TextView) findViewById(R.id.clue);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Data.save();
    }

    public void onSubmitAnswer(View view) {
        final AlertDialog progressDialog = new ProgressDialog.Builder(this)
                .setMessage("Checking your answer")
                .setTitle("Checking")
                .setCancelable(false)
                .show();
        String answer = answerText.getText().toString();
        RetroAPI.NetworkCalls.answer(Data.getQuestionID(), answer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        boolean success = jsonObject.get("success").getAsBoolean();
                        if(success) {
                            RetroAPI.NetworkCalls.getQuestion(jsonObject.get("nextquestion").getAsString())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<JsonObject>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
                                        }

                                        @Override
                                        public void onNext(JsonObject jsonObject) {
                                            Data.setQuestion(jsonObject.get("questionid").getAsString(), jsonObject.get("question").getAsString());
                                            Data.setClue(jsonObject.get("clue").getAsString());
                                            questionText.setText(Data.getQuestion());
                                            clueText.setText(Data.getClue());
                                            progressDialog.dismiss();
                                            Data.setMapActivity(true);
                                            Intent intent = new Intent(HomeActivity.this, MapActivity.class)
                                                    .putExtra(Constants.LATITUDE_KEY, jsonObject.get("lat").getAsDouble())
                                                    .putExtra(Constants.LONGITUDE_KEY, jsonObject.get("lng").getAsDouble())
                                                    .putExtra("char", jsonObject.get("char").toString());
                                            startActivity(intent);
                                        }
                                    });
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
}
