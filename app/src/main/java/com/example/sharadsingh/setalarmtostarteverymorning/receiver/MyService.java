package com.example.sharadsingh.setalarmtostarteverymorning.receiver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.example.sharadsingh.setalarmtostarteverymorning.MyTrackingDeviceService;

import java.util.Timer;
import java.util.TimerTask;
public class MyService extends Service {
    public static final int notify = 3 * 60 *1000;
        private Handler mHandler = new Handler();
        private Timer mTimer = null;
        @Override
        public IBinder onBind(Intent intent) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        @Override
        public void onCreate() {
            if (mTimer != null)
                mTimer.cancel();
            else
                mTimer = new Timer();
                mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            mTimer.cancel();
        }
       class TimeDisplay extends TimerTask {
      @Override
      public void run() {
          mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent myIntent = new Intent(MyService.this, MyTrackingDeviceService.class);
                    startService(myIntent);
                    Toast.makeText(MyService.this, "5 second run", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}


}