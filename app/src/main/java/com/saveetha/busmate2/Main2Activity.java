package com.saveetha.busmate2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class Main2Activity extends AppCompatActivity implements LocationListener {

    ImageView loadingGif;
    LocationManager locationManager;
    Location currentLocation;
    TextView responseTV;
    Button responseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        loadingGif = findViewById(R.id.loadingGIF);
        loadingGif.setVisibility(View.INVISIBLE);
        Glide.with(this).load(R.drawable.giphy).into(loadingGif);
        responseBtn = findViewById(R.id.responseButton);
        responseBtn.setVisibility(View.INVISIBLE);
        responseTV = findViewById(R.id.responseTextView);
        responseTV.setText("Bus Number - 33 has accepted your request! Please wait in your stop");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //request location permission
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            responseBtn.setVisibility(View.VISIBLE);
            responseBtn.setText("Okay!");
            finish();
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        //TODO get location from the server

        //TODO match it with the current location

        //TODO if not matched or great distance, display error!

        //TODO Else wait until the request is accepted by someone, Show exit button
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
