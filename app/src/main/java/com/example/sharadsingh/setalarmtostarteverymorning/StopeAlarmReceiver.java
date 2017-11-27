package com.example.sharadsingh.setalarmtostarteverymorning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.sharadsingh.setalarmtostarteverymorning.receiver.MyService;

/**
 * Created by sharadsingh on 16/11/17.
 */

public class StopeAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "I'm Stoping", Toast.LENGTH_SHORT).show();
        context.stopService(new Intent(context, MyService.class));
    }
}