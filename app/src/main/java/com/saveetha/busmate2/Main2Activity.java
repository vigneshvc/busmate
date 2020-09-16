package com.saveetha.busmate2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class Main2Activity extends AppCompatActivity implements LocationListener {

    ImageView loadingGif;
    LocationManager locationManager;
    Location currentLocation;
    TextView responseTV;
    Button responseBtn;
    String uname;
    Handler handler;
    final String TAG = "BUS MISSED ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        loadingGif = findViewById(R.id.loadingGIF);
        //loadingGif.setVisibility(View.INVISIBLE);
        Glide.with(this).load(R.drawable.giphy).into(loadingGif);
        responseBtn = findViewById(R.id.responseButton);
        responseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main2Activity.this.finish();
            }
        });
        responseBtn.setVisibility(View.INVISIBLE);
        responseTV = findViewById(R.id.responseTextView);
        //responseTV.setText("Bus Number - 33 has accepted your request! Please wait in your stop");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //request location permission
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            responseBtn.setVisibility(View.VISIBLE);
            responseTV.setText("Please provide Location permission and Try again!");
            loadingGif.setVisibility(View.INVISIBLE);
            responseBtn.setText("Okay!");
        }else {
            Log.i(TAG, "Location requested");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        uname = new PasswordManager(this).getUserName();
        final String url = getString(R.string.server_address)+"/busmissed.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG,"RESPONSE:"+response);
                        try {
                            JSONObject jb = new JSONObject(response);
                            String status = jb.getString("status");
                            if(status.equals("success")){
                                Log.i(TAG,"Status - Success");
                                //wait for admin to accept
                                responseTV.setText("Please Wait for Bus Admin Response!");
                                responseBtn.setVisibility(View.INVISIBLE);
                                loadingGif.setVisibility(View.VISIBLE);

                                handler = new Handler();
                                handler.postDelayed(new Runnable(){
                                                        @Override
                                                        public void run() {
                                                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                                            final String url = getString(R.string.server_address)+"/busmissed.php";
                                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                                                    new Response.Listener<String>() {
                                                                        @SuppressLint("SetTextI18n")
                                                                        @Override
                                                                        public void onResponse(String response) {
                                                                            Log.i(TAG,"RESPONSE:"+response);
                                                                            try {
                                                                                JSONObject jb = new JSONObject(response);
                                                                                String found =jb.getString("found");
                                                                                if(found.equals("true")){
                                                                                    String busno = jb.getString("busno");
                                                                                    responseTV.setText("Bus Number - "+busno+" has accepted your request! Please wait in your stop");
                                                                                    responseBtn.setVisibility(View.VISIBLE);
                                                                                    loadingGif.setVisibility(View.INVISIBLE);
                                                                                    handler.removeCallbacksAndMessages(null);
                                                                                }else if(found.equals("alldenied")){
                                                                                    responseTV.setText("No Bus is Available!");
                                                                                    responseBtn.setVisibility(View.VISIBLE);
                                                                                    loadingGif.setVisibility(View.INVISIBLE);
                                                                                    handler.removeCallbacksAndMessages(null);
                                                                                }
                                                                            } catch (Exception e) {
                                                                                Log.e(TAG, e.toString());
                                                                            }
                                                                            //result.setText(url);
                                                                        }
                                                                    }, new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    Log.e(TAG,error.toString());
                                                                }
                                                            }) {
                                                                @Override
                                                                protected Map<String, String> getParams() {
                                                                    Map<String, String> params = new HashMap<String, String>();
                                                                    params.put("uname",uname);
                                                                    return params;
                                                                }
                                                            };
// Add the request to the RequestQueue.
                                                            queue.add(stringRequest);
                                                            handler.postDelayed(this, 10000);
                                                        }
                                                    },
                                        10000);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"error-"+error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username", uname);
                if(currentLocation!=null){
                    params.put("clat",currentLocation.getLatitude()+"");
                    params.put("clong",currentLocation.getLongitude()+"");
                }
                return params;
            }
        };


        try{sleep(2000);}catch (InterruptedException e){Log.e(TAG, e.toString());}
        Log.i(TAG,"Initial bus missed request posted");
        queue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        Log.i(TAG,"location Updated - "+currentLocation);
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
