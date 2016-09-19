package com.csivit.rakshith.forkthecode.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
                googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your clue is waiting here :P"));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
            }
        });
        locationService = new LocationService(this);
        Subscription subscription = Observable.interval(Constants.LOCATION_INTERVAL, TimeUnit.MILLISECONDS)
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
                        Location currentLocation = locationService.getLocation();
                        if(currentLocation.distanceTo(location) < 10) {
                            TextView textView = new TextView(MapActivity.this);
                            textView.setText(getIntent().getStringExtra("char"));
                            textView.setTextSize(48);
                            setContentView(textView);
                            Subscription timerSubscription = Observable.timer(3, TimeUnit.SECONDS)
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
                                            Data.setMapActivity(false);
                                            Data.save();
                                            finish();
                                        }
                                    });
                            subscriptions.add(timerSubscription);
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
}
