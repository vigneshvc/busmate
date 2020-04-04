package com.saveetha.busmate2;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BusmateNetClient extends Service implements Response.Listener, Response.ErrorListener {

    final String TAG = "BusmateNetClient";
    PasswordManager pm;
    String LAT, LONG, USERID;
    int BUSID;

    public BusmateNetClient() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        pm = new PasswordManager(this);
        if(!pm.isLoggedIn())stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences sp = getSharedPreferences("MyPref", MODE_PRIVATE);

        try {
            LAT = intent.getExtras().getString("LAT");
            LONG = intent.getExtras().getString("LONG");
        }
        catch(Exception e){
            LAT = "0";
            LONG = "0";
        }
        //USERID = sp.getString("USERID", "-1");
        //BUSID = sp.getString("BUSID", "-1");
        USERID = pm.getUserName();
        BUSID = pm.getPreferedBusId();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = getResources().getString(R.string.server_address) + "?lat=" + LAT + "&long=" + LONG + "&USERID=" + USERID + "&BUSID=" + BUSID;
        Log.i("URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, this, this) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lat", LAT);
                params.put("long", LONG);
                params.put("userid", USERID);
                params.put("busid", BUSID+"");
                return params;
            }
        };

        Long lastUpdated = sp.getLong("lastupdated", 0);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -5);
        Long timenow = cal.getTimeInMillis();
        if (timenow.compareTo(lastUpdated) > 0) {
            queue.add(stringRequest);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onResponse(Object response) {
        Log.i(TAG, response.toString());
        try {
            stopSelf();
        } catch (Exception e) {
            Log.e(TAG, "Cannot parse JSON Response");
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(TAG, error.toString());
        stopSelf();
    }
}