package com.example.sharadsingh.setalarmtostarteverymorning;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import static com.example.sharadsingh.setalarmtostarteverymorning.MyApplication.mContext;


public class LocationService extends Service {
    private final Context context;
    MyTrackingDeviceService myTrackingDeviceService ;

    public LocationService(Context context ,MyTrackingDeviceService myTrackingDeviceServices ) {
        this.context = context;
        this.myTrackingDeviceService = myTrackingDeviceServices;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Location getLocation() {
        LocationManager locationManager;
        Location location = null;
        long time = 0;
        try {
            if (context != null) {
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (!isGPSEnabled && !isNetworkEnabled) {
                    return null;
                } else {
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        //if (location == null) {

                       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return null;
                        }
*/
                        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{
                                    android.Manifest.permission.ACCESS_FINE_LOCATION
                            },
                             10);
                        }

                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                0,
                                0f, new android.location.LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location clocation) {

                                    }

                                    @Override
                                    public void onStatusChanged(String s, int i, Bundle bundle) {

                                    }

                                    @Override
                                    public void onProviderEnabled(String s) {

                                    }

                                    @Override
                                    public void onProviderDisabled(String s) {

                                    }
                                });
                        Log.d("GPS", "GPS Enabled");

                        //}
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            return location;

                        }
                    }
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                time,
                                0, new android.location.LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location clocation) {
//                                     if(clocation!=null)
//                                         location=clocation;
                                    }

                                    @Override
                                    public void onStatusChanged(String s, int i, Bundle bundle) {
                                    }

                                    @Override
                                    public void onProviderEnabled(String s) {
                                    }
                                    @Override
                                    public void onProviderDisabled(String s) {

                                    }
                                });
                        Log.d("Network", "Network Enabled");
                        //if (locationManager != null && location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        return location;
                        //}
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
