package com.example.sharadsingh.setalarmtostarteverymorning;

import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by mansha on 30/10/15.
 */
public class MyApplication extends MultiDexApplication {
    public static final String TAG = MyApplication.class.getSimpleName();
    private static MyApplication mInstance;
    private RequestQueue mRequestQueue;
    public static Context mContext;
    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this;
       /* RealmConfiguration realmConfiguration = new RealmConfiguration
                .Builder(mContext)
                .schemaVersion(2)
                .name("new")
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);*/

        RealmConfiguration encryptedConfig = new RealmConfiguration.Builder(mContext).name("name")
                .schemaVersion(6)
                .build();
        Realm.removeDefaultConfiguration();
        Realm.setDefaultConfiguration(encryptedConfig);


        if (BuildConfig.DEBUG) {
            Constants.IS_LIVE = false;
        } else {
            Constants.IS_LIVE = true;
        }
    }
    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
