package com.example.sharadsingh.setalarmtostarteverymorning;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.example.sharadsingh.setalarmtostarteverymorning.receiver.MyService;

/**
 * Created by sharadsingh on 09/10/17.
 */

public class GpsLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            ContentResolver contentResolver = context.getContentResolver();
            int mode = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            if (mode != Settings.Secure.LOCATION_MODE_OFF) {
               Intent pushIntent = new Intent(context, MyService.class);
               context.startService(pushIntent);
            }else {

           }

        }
    }

}