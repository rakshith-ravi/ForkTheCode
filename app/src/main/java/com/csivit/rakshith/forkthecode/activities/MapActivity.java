package com.csivit.rakshith.forkthecode.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.csivit.rakshith.forkthecode.R;
import com.csivit.rakshith.forkthecode.model.Constants;
import com.csivit.rakshith.forkthecode.model.Data;
import com.csivit.rakshith.forkthecode.model.LocationService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MapActivity extends AppCompatActivity {

    private GoogleMap googleMap;
    private MapView mapView;
    private Location location;
    private LocationService locationService;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private String letter = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapActivity.this.googleMap = googleMap;
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your clue is waiting here"));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
            }
        });
        locationService = new LocationService(this);
        Subscription subscription = Observable.interval(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(Constants.LOG_TAG, Log.getStackTraceString(e));
                    }

                    @Override
                    public void onNext(Long aLong) {
                        try {
                            Location currentLocation = locationService.getLocation();
                            Log.e(Constants.LOG_TAG, "Getting location " + currentLocation.toString());
                            Log.e(Constants.LOG_TAG, "Go to location " + location.toString());
                            double dx, dy;
                            dx = location.getLatitude() - currentLocation.getLatitude();
                            dy = location.getLongitude() - currentLocation.getLongitude();
                            double meterPerDegree = (2 * Math.PI * 6400000) / (360d * 5);
                            double distance = Math.sqrt((dx * dx) + (dy * dy)) * meterPerDegree;
                            Log.e(Constants.LOG_TAG, distance + "");
                            if (distance < 50) {
                                new AlertDialog.Builder(MapActivity.this)
                                        .setMessage("Your clue: " + getIntent().getStringExtra(Constants.CLUE_KEY))
                                        .setTitle("Congratulations!")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Data.setMapActivity(false);
                                                Data.save();
                                                dialog.dismiss();
                                                Intent intent = new Intent(MapActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .create()
                                        .show();
                            }
                        } catch (Exception ex) {
                            Log.e(Constants.LOG_TAG, Log.getStackTraceString(ex));
                        }
                    }
                });
        subscriptions.add(subscription);
        Intent intent = getIntent();
        if(intent.getDoubleExtra(Constants.LATITUDE_KEY, -1) >= 0d && intent.getDoubleExtra(Constants.LONGITUDE_KEY, -1) >= 0d) {
            Location location = new Location("");
            location.setLatitude(intent.getDoubleExtra(Constants.LATITUDE_KEY, -1));
            location.setLongitude(intent.getDoubleExtra(Constants.LONGITUDE_KEY, -1));
            this.location = location;
        } else {
            Log.e(Constants.LOG_TAG, "Error. Latitude and Longitude are not provided or are invalid!");
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Data.setLocation(location);
        Data.save();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        Data.save();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        subscriptions.unsubscribe();
    }

    public void onPassword(View view) {
        final EditText editText = new EditText(this);
        editText.setHint("Password");
        new AlertDialog.Builder(this)
                .setTitle("Enter the password")
                .setView(editText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(Constants.LOG_TAG, Data.getQuestionID());
                        if(Data.getQuestionID().equals(editText.getText().toString())) {
                            new AlertDialog.Builder(MapActivity.this)
                                    .setMessage("Your clue: " + getIntent().getStringExtra(Constants.CLUE_KEY))
                                    .setTitle("Your clue!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Data.setMapActivity(false);
                                            Data.save();
                                            dialog.dismiss();
                                            Intent intent = new Intent(MapActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}
