package com.saveetha.busmate2;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    String TAG = "MAPActivity";
    Marker mk;
    LatLng location;
    Handler handler;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "getLocation Called");
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                final String url = "http://192.168.0.200:8080/busmate/getbuslocation.php";

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                try {
                                    JSONObject jb = new JSONObject(response);
                                    String latt = jb.getString("lat");
                                    String longg = jb.getString("long");
                                    location = new LatLng(Double.parseDouble(latt), Double.parseDouble(longg));
                                    //mk = mMap.addMarker(new MarkerOptions().position(location).title("BUS").icon(BitmapDescriptorFactory.fromResource(R.drawable.busimage)));

                                    Log.i(TAG,location.toString());

                                    animateMarker(mk, location, false);
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
                        return params;
                    }
                };

// Add the request to the RequestQueue.
                queue.add(stringRequest);
                handler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    public void animateMarker(final Marker marker, final LatLng toPosition, final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        location = new LatLng(37.166003333333336, -121.85197666666666);
        mk = mMap.addMarker(new MarkerOptions().position(location).title("BUS").icon(BitmapDescriptorFactory.fromResource(R.drawable.busmarkerpng)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
    }

}
