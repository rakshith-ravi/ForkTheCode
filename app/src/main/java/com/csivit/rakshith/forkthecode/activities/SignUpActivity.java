package com.csivit.rakshith.forkthecode.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.csivit.rakshith.forkthecode.R;
import com.csivit.rakshith.forkthecode.model.Data;
import com.csivit.rakshith.forkthecode.model.RetroAPI;
import com.google.gson.JsonObject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SignUpActivity extends AppCompatActivity {

    private EditText email;
    private EditText username;
    private EditText password;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email = (EditText) findViewById(R.id.email_address);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
    }

    public void onSignUp(View view) {
        final AlertDialog progressDialog = new ProgressDialog.Builder(this)
                .setMessage("Signing you up. Please wait")
                .setTitle("Signing up")
                .setCancelable(false)
                .show();
        subscriptions.add(RetroAPI.NetworkCalls.signUp(email.getText().toString(), username.getText().toString(), password.getText().toString())
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
                        progressDialog.dismiss();
                        boolean success = jsonObject.get("success").getAsBoolean();
                        if(success) {
                            Data.setLoggedIn(true);
                            // TODO set user data and open home page
                        } else {
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("Unable to Sign Up :(")
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
                }));
    }

    public void onLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
    }
}
