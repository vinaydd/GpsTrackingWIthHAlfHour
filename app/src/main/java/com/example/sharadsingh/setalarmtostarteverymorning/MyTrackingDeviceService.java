package com.example.sharadsingh.setalarmtostarteverymorning;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sharadsingh.setalarmtostarteverymorning.receiver.VimayModel;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import io.realm.Realm;
import io.realm.RealmResults;
/**
 * Created by sharadsingh on 16/06/17.
 */
public class MyTrackingDeviceService extends Service implements GoogleApiClient.ConnectionCallbacks,
      GoogleApiClient.OnConnectionFailedListener,
      LocationListener {
      public static final int notify =2* 1000;
      private Handler mHandler = new Handler();
      private Timer mTimer = null;
      public LocationRequest mLocationRequest;
      public Location mCurrentLocation;
      public LocationSettingsRequest mLocationSettingsRequest;
      private Realm realm;
      public GoogleApiClient mGoogleApiClient;
      private Timer timer;
      private int counter;
      double latitude;
      double longitude;
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
         realm = Realm.getDefaultInstance();
         this.realm = RealmController.with(this.getApplication()).getRealm();
         RealmController.with(this.getApplication()).refresh();
         buildGoogleApiClient();
         mGoogleApiClient.connect();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //  ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_COARSE_LOCATION);
            } else {
                startLocationUpdate();
                buildLocationSettingsRequest();
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
      mGoogleApiClient.connect();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        checkForValidLocation();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                     Toast.makeText(MyTrackingDeviceService.this, "true", Toast.LENGTH_SHORT).show();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy HH:mm:ss");
                    Date date = new Date();
                    System.out.println(); //2016/11/16 12:08:43
                    saveDataToRealm("pending", latitude, longitude, "vinay123456789", simpleDateFormat.format(date));
                    if (isConnectingToInternet()) {
                        gotoRealmDataWebCall();
                    } else {
                        // Toast.makeText(MyTrackingDeviceService.this, "Network not available", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },notify);
    }
    private void gotoRealmDataWebCall() {
        final RealmResults<VimayModel> booksUser = RealmController.with(getApplication()).getBooksUser();
        if (booksUser != null && booksUser.size() > 0) {
            for (int i = 0; i < booksUser.size(); i++) {
                VimayModel vimayModel = booksUser.get(i);
                if (vimayModel.getStatus().equalsIgnoreCase("pending")) {
                    realm.beginTransaction();
                    booksUser.get(i).setStatus("success");
                    realm.commitTransaction();
                    gotoObjectWebCAll("", vimayModel, i);
                }
            }
        }
        deliteRealmObject(booksUser);
    }
    private void deliteRealmObject(RealmResults<VimayModel> booksUser) {
        try {
            for(int i =0 ; i< booksUser.size();i++){
                VimayModel vimayModel  = booksUser.get(i);
                if(vimayModel.getStatus().equalsIgnoreCase("success")){
                     realm.beginTransaction();
                     VimayModel movie = booksUser.get(i);
                     movie.deleteFromRealm();
                     realm.commitTransaction();
                     Log.d("delitpostion" ,""+ i);
                }
            }
            mGoogleApiClient.disconnect();
           // stopLocationUpdates();
            this.stopService(new Intent(MyTrackingDeviceService.this, MyTrackingDeviceService.class));
            this.stopService(new Intent(MyTrackingDeviceService.this, LocationService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }
    private void gotoObjectWebCAll(String subUrl, VimayModel vinay, final int position) {
        String URL = "http:x//crm.truxapp.com/truxapiv2/transport/addPartnerCapturedLocation";
        final RequestQueue queue = Volley.newRequestQueue(this);
        queue.getCache().remove(URL);
        queue.getCache().clear();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", "123456789");
            jsonObject.put("vehicleLat", vinay.getLatitude());
            jsonObject.put("vehicleLong", vinay.getLongitude());
            jsonObject.put("mobileNumber", "123456789");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Sobject",jsonObject.toString());
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy HH:mm:ss");
            Date convertedDate = simpleDateFormat.parse(vinay.getDate());
            String URL_OF_SOCKET = "http://mobilehost.truxapp.com:5055";
            Uri serverUrl = Uri.parse(URL_OF_SOCKET);
            Uri.Builder builder = serverUrl.buildUpon()
                .appendQueryParameter("id", "7836914331")
                .appendQueryParameter("timestamp", String.valueOf(convertedDate.getTime() / 1000))
                .appendQueryParameter("lat", String.valueOf(vinay.getLatitude()))
                .appendQueryParameter("lon", String.valueOf(vinay.getLongitude()));
                 builder.build().toString();
            RequestManager.sendRequestAsync(builder.build().toString(), new RequestManager.RequestHandler() {
                @Override
                public void onComplete(boolean success) {
                    if (success) {
                     Toast.makeText(MyTrackingDeviceService.this,"Success socket", Toast.LENGTH_SHORT).show();
                    } else {
                       Toast.makeText(MyTrackingDeviceService.this,"false socket", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }

    }
    private void saveDataToRealm(String status, double tal, double longi, String deviceId, String date) {
        final RealmResults<VimayModel> booksUser = RealmController.with(getApplication()).getBooksUser();
        if (booksUser != null && booksUser.size() > 0) {
            Log.d("realmlistsize", String.valueOf(booksUser.size()));
            VimayModel vimayModel = booksUser.get(booksUser.size() - 1);
            boolean flag = getDateDiffirence(vimayModel.getDate(), date);
            if (flag == true) {
                // RealmController.with(this.getApplication()).clearAllUserData();
                VimayModel data = new VimayModel();
                data.setStatus(status);
                data.setLatitude(tal);
                data.setLongitude(longi);
                data.setDeviceId(deviceId);
                data.setDate(date);
                realm.beginTransaction();
                realm.copyToRealm(data);
                realm.commitTransaction();
                AppPreference.with(this).setPreLoad(true);
            }
        } else {
            //RealmController.with(this.getApplication()).clearAllUserData();
            VimayModel data = new VimayModel();
            data.setStatus(status);
            data.setLatitude(tal);
            data.setLongitude(longi);
            data.setDeviceId(deviceId);
            data.setDate(date);
            realm.beginTransaction();
            realm.copyToRealm(data);
            realm.commitTransaction();
            AppPreference.with(this).setPreLoad(true);
        }
    }
    private boolean getDateDiffirence(String previousDate, String currentDate) {
        boolean flag = false;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy HH:mm:ss",Locale.getDefault());
        try {
            Date datecurrent = simpleDateFormat.parse(currentDate);
            Date datepre = simpleDateFormat.parse(previousDate);
            long different = datecurrent.getTime() - datepre.getTime();
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;
            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;
            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;
            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;
            long elapsedSeconds = different / secondsInMilli;
            System.out.printf(
                    "%d days, %d hours, %d minutes, %d seconds%n",
                    elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
            if (elapsedMinutes >= 2 || elapsedDays > 1) {
                flag = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }
    private void startLocationUpdate() {
        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }
    public void startTimer() {
        isLocationUpdated();
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 1000);
    }
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mCurrentLocation == null) {
                counter = counter + 1;
            } else if (isLocationUpdated() && mCurrentLocation != null && mCurrentLocation.getLatitude() != 0.0 && mCurrentLocation.getLongitude() != 0.0) {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                new Runnable() {
                    public void run() {
                        getLatLong(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    }
                };
            } else {
                counter = counter + 1;
            }
            if (counter == 4) {
                if (timer != null) {
                    timer.cancel();
                }
                counter = 0;
            }
        }
    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {

            e.printStackTrace();
        }
        createLocationRequest();
    }
    public void checkForValidLocation() {
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        } else if (!mGoogleApiClient.isConnected()) {
            buildGoogleApiClient();
        } else if (mGoogleApiClient.isConnected() && mCurrentLocation == null) {
            mGoogleApiClient.disconnect();
            buildGoogleApiClient();
        } else {
            getLatLong(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
    }
    public void getLatLong(double lati, double longi) {
        latitude = lati;
        longitude = longi;
    }
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        builder.setAlwaysShow(true);
        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        startTimer();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                       startTimer();
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        break;
                }
            }
        });
    }
    public boolean isLocationUpdated() {
        LocationService locationService = new LocationService(this,MyTrackingDeviceService.this);
        Location location = locationService.getLocation();
        if (location == null)
            return true;
        else {
            if (mCurrentLocation == null) {
                mCurrentLocation = location;
                return true;
            } else {
                int accuracyDelta = (int) (mCurrentLocation.getAccuracy());
                int newLocation = (int) (location.getAccuracy());
                if (accuracyDelta < newLocation)
                    return true;
                else {
                    mCurrentLocation = location;
                    return true;
                }
            }
        }
    }
}