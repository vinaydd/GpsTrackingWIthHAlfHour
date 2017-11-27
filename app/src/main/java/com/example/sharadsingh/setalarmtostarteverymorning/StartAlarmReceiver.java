package com.example.sharadsingh.setalarmtostarteverymorning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.sharadsingh.setalarmtostarteverymorning.receiver.MyService;

/**
 * Created by sharadsingh on 16/11/17.
 */

public class StartAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(context, MyService.class);
        context.startService(myIntent);
    }
}