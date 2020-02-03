package com.saveetha.busmate2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class LocationUpdater extends Service implements LocationListener {

    final String TAG = "LocationUpdaterService";
    final String CHANNEL_ID = "ForegroundServiceChannelLocationUpdater";
    LocationManager locationManager;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;


    public LocationUpdater() {
    }

    public void startLocationUpdate() {
        Log.d(TAG, "Location Updater - startLocationUpdate Alarm Called");
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, LocationUpdater.class);
//        boolean flag = (PendingIntent.getBroadcast(this, 0,
//                intent, PendingIntent.FLAG_NO_CREATE) == null);
        /*Register alarm if not registered already*/
        // if (flag) {
        PendingIntent alarmIntent = PendingIntent.getService(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Calendar obj called calendar
        Calendar calendar = Calendar.getInstance();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 5);
        cal.set(Calendar.MINUTE, 40);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (cal.compareTo(calendar) < 0) {
            cal.add(Calendar.DATE, 1);
        }


        Log.i("MainActivity", cal.toString());
        /* Setting alarm for every time interval from the current time.*/
        int intervalTimeMillis = 1000 * 24; // time interval for updating location
        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,                cal.getTimeInMillis(), ,                alarmIntent);
        alarmMgr.set(AlarmManager.RTC, cal.getTimeInMillis(), alarmIntent);
    }

    public void stopLocationUpdate() {
        Log.i(TAG, "stopLocationUpdate Alarm called");
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, LocationUpdater.class);
        intent.setAction("STOPME");
//        boolean flag = (PendingIntent.getBroadcast(this, 0,
//                intent, PendingIntent.FLAG_NO_CREATE) == null);
        /*Register alarm if not registered already*/
        // if (flag) {
        PendingIntent alarmIntent = PendingIntent.getService(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Calendar obj called calendar
        Calendar calendar = Calendar.getInstance();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 8);
        cal.set(Calendar.MINUTE, 15);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (calendar.compareTo(cal) > 0) {
            cal.add(Calendar.DATE, 1);
        }

        Log.i("MainActivity", cal.toString());
        /* Setting alarm for every time interval from the current time.*/
        int intervalTimeMillis = 1000 * 24; // time interval for updating location
        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,                cal.getTimeInMillis(), ,                alarmIntent);
        alarmMgr.set(AlarmManager.RTC, cal.getTimeInMillis(), alarmIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Log.i("Service", "Started");

        startForegroundApi();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 5);
        cal.set(Calendar.MINUTE, 40);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.MILLISECOND, 0);
        Long fiveforty = cal.getTimeInMillis();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR, 8);
        cal2.set(Calendar.MINUTE, 15);
        cal2.set(Calendar.AM_PM, Calendar.AM);
        cal2.set(Calendar.MILLISECOND, 0);
        Long eightoclck = cal2.getTimeInMillis();
        Long timenow = Calendar.getInstance().getTimeInMillis();


        if (!(timenow.compareTo(eightoclck) < 0 && timenow.compareTo(fiveforty) > 0)) {
            Log.i(TAG, "Service stopped due to irregular time");
            startLocationUpdate();
            stopSelf();
        }
    }

    @TargetApi(26)
    void startForegroundApi() {
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel AidYou", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager mgr = getSystemService(NotificationManager.class);
        mgr.createNotificationChannel(serviceChannel);

        Intent notificationIntent = new Intent(this, this.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this, CHANNEL_ID).setContentTitle("BusMate").setContentText("We are helping your friends").setTicker("Hello!").build();
        startForeground(10023, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() == "STOPME") {
            Log.i(TAG, "Stopping Intent Received");
            stopSelf();
        }
        stopLocationUpdate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
        Intent intent = new Intent(this, BusmateNetClient.class);
        intent.putExtra("LAT", Double.toString(location.getLatitude()));
        intent.putExtra("LONG", Double.toString(location.getLongitude()));
        startService(intent);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 5);
        cal.set(Calendar.MINUTE, 40);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.MILLISECOND, 0);
        Long fiveforty = cal.getTimeInMillis();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR, 8);
        cal2.set(Calendar.MINUTE, 15);
        cal2.set(Calendar.AM_PM, Calendar.AM);
        cal2.set(Calendar.MILLISECOND, 0);
        Long eightoclck = cal2.getTimeInMillis();
        Long timenow = Calendar.getInstance().getTimeInMillis();


        if (!(timenow.compareTo(eightoclck) < 0 && timenow.compareTo(fiveforty) > 0)) {
            Log.i(TAG, "Service stopped due to irregular time");
            startLocationUpdate();
            stopSelf();
        }

        //Log.i("Speed data",location.getSpeed()+"");
        //Log.i("onLocationChanged",location.getSpeed()+""+location.getLatitude()+","+location.getLongitude()+","+location.getAccuracy());
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

    @Override
    public void onDestroy() {
        Log.i(TAG, "Location Updater Stopped");
        startLocationUpdate();
        super.onDestroy();
    }


}
