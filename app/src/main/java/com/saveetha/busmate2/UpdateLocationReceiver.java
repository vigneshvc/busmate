package com.saveetha.busmate2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateLocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent startIntent = new Intent(context, LocationUpdater.class);
        context.startService(startIntent);
    }
}
