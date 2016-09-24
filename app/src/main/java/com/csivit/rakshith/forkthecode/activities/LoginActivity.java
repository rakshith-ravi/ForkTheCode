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

import com.csivit.rakshith.forkthecode.R;
import com.csivit.rakshith.forkthecode.model.Constants;
import com.csivit.rakshith.forkthecode.model.Data;
import com.csivit.rakshith.forkthecode.model.RetroAPI;
import com.google.gson.JsonObject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LoginActivity extends AppCompatActivity {

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_login);
    }

    public void onLogin(View view) {
        /*
        {
            success: true
            authtoken: "asdfasdfjasdhgfaw"
            questionid: wjbefawadf
        }
         */
        EditText username = (EditText) findViewById(R.id.registration_number);
        EditText password = (EditText) findViewById(R.id.password);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging you in");
        progressDialog.setTitle("Logging in");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        progressDialog.show();
        RetroAPI.NetworkCalls.login(username.getText().toString(), password.getText().toString())
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
                            Data.AuthToken = jsonObject.get("authtoken").toString();
                            Data.setQuestion(jsonObject.get("questionid").toString(), jsonObject.get("question").toString());
                            Data.setLoggedIn(true);
                            Data.setJoinedTeam(true);
                            Data.save();
                            if(Data.isJoinedTeam()) {
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setMessage(jsonObject.get("message").toString())
                                    .setTitle("Error")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .setCancelable(false)
                                    .create()
                                    .show();
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Data.save();
    }
}
