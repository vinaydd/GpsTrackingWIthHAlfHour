package com.example.sharadsingh.setalarmtostarteverymorning;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {
    private final SharedPreferences appSharedPrefs;
    private final SharedPreferences.Editor prefsEditor;
    private static final String PRE_LOAD = "preLoad";
    private static final String PREFS_NAME = "prefs";
    private static AppPreference instance;

    public AppPreference(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(Constants.APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public String getStringValueForTag(String tagName) {
        return appSharedPrefs.getString(tagName, "");
    }

    public void setStringValueForTag(String tagName, String value) {
        prefsEditor.putString(tagName, value);
        prefsEditor.commit();
    }

    public void setIntValueForTag(String tagName, int value) {
        prefsEditor.putInt(tagName, value);
        prefsEditor.commit();
    }

    public int getIntValueForTag(String tagName) {
        return appSharedPrefs.getInt(tagName, 0);
    }

    public void setBooleanValueForTag(String tagName, boolean value) {
        prefsEditor.putBoolean(tagName, value);
        prefsEditor.commit();
    }

    public boolean getBooleanValueForTag(String tagName) {
        return appSharedPrefs.getBoolean(tagName, false);
    }
    public void clearPreferences() {
        prefsEditor.clear();
        prefsEditor.commit();
    }
    public static AppPreference with(Context context) {

        if (instance == null) {
            instance = new AppPreference(context);
        }
        return instance;
    }
    public void setPreLoad(boolean totalTime) {
        appSharedPrefs
                .edit()
                .putBoolean(PRE_LOAD, totalTime)
                .apply();
    }
    public boolean getPreLoad() {
        return appSharedPrefs.getBoolean(PRE_LOAD, false);
    }
}
