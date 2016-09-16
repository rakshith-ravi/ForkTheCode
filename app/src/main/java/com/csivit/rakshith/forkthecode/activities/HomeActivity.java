package com.csivit.rakshith.forkthecode.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.csivit.rakshith.forkthecode.R;
import com.csivit.rakshith.forkthecode.model.Data;
import com.csivit.rakshith.forkthecode.model.LocationService;
import com.csivit.rakshith.forkthecode.model.RetroAPI;
import com.google.gson.JsonObject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {

    //private GoogleMap googleMap;
    //private MapView mapView;
    private EditText answerText;
    private TextView questionText;
    private TextView clueText;
    private LocationService locationService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        answerText = (EditText) findViewById(R.id.answer);
        questionText = (TextView) findViewById(R.id.question);
        clueText = (TextView) findViewById(R.id.clue);
        locationService = new LocationService(this);
        /*
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                HomeActivity.this.googleMap = googleMap;
            }
        });
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mapView.onDestroy();
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

                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        boolean success = jsonObject.get("success").getAsBoolean();
                        if(success) {
                            // TODO Get new question and stuff
                            RetroAPI.NetworkCalls.getQuestion(jsonObject.get("nextquestionid").getAsString())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<JsonObject>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onNext(JsonObject jsonObject) {
                                            Data.setQuestion(jsonObject.get("questionid").getAsString(), jsonObject.get("question").getAsString());
                                            Data.setClue(jsonObject.get("clue").getAsString());
                                            questionText.setText(Data.getQuestion());
                                            progressDialog.dismiss();
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
