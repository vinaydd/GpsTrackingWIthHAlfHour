package com.example.sharadsingh.setalarmtostarteverymorning;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Connection;
import android.widget.Toast;

import com.example.sharadsingh.setalarmtostarteverymorning.receiver.NotificationPublisher;

import java.util.Calendar;
import java.util.LinkedList;

import io.realm.Realm;
public class MainActivity extends AppCompatActivity {
    private PendingIntent pendingIntent ,pendingIntentStope;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         callTimerForStopTraking();
         registerReceiver(broadcastReceiver, new IntentFilter("STOPE_SERVICES"));

        Intent alarmIntent = new Intent(MainActivity.this, StartAlarmReceiver.class);
        pendingIntent= PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent alarmIntentStope = new Intent(MainActivity.this, StopeAlarmReceiver.class);
        pendingIntentStope= PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntentStope, PendingIntent.FLAG_UPDATE_CURRENT);

        startAt10();
        stopAt22();
    }
    public void callTimerForStopTraking() {
         scheduleNotification(getNotification("10 second delay"),3 * 1000);
    }
    private void scheduleNotification(Notification notification, int delay) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        return builder.build();
    }

    public void startAt10() {
         AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
         Calendar calendar = Calendar.getInstance();
         calendar.setTimeInMillis(System.currentTimeMillis());
         calendar.set(Calendar.HOUR_OF_DAY, 13);
         calendar.set(Calendar.MINUTE,20);
         manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
    public void stopAt22() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 00);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentStope);

    }

}
