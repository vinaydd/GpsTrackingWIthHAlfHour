package com.example.sharadsingh.setalarmtostarteverymorning;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import com.example.sharadsingh.setalarmtostarteverymorning.receiver.MyService;

import java.util.Calendar;

/**
 * Created by sharadsingh on 17/06/17.
 */

public class BootUpReceiver extends BroadcastReceiver {
    AlarmManager manager,managerone;
    PendingIntent pendingIntent,pendingIntentStope;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmIntent = new Intent(context, StartAlarmReceiver.class);
        pendingIntent= PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent alarmIntentStope = new Intent(context, StopeAlarmReceiver.class);
        pendingIntentStope= PendingIntent.getBroadcast(context, 0, alarmIntentStope, PendingIntent.FLAG_UPDATE_CURRENT);
        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        managerone = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
           startAt10( );
           stopAt22();
           Intent myIntent = new Intent(context, MyService.class);
           context.startService(myIntent);
    }


    public void startAt10() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE,17);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
    public void stopAt22() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 00);
        managerone.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentStope);
    }


}
