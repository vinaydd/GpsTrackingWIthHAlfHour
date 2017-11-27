package com.example.sharadsingh.setalarmtostarteverymorning.receiver;

import java.util.Objects;

import io.realm.RealmObject;

/**
 * Created by sharadsingh on 17/11/17.
 */

public class


VimayModel extends RealmObject {

    private String status;
    private double latitude;

    public double getLatitude() {
        return latitude;
    }

    public VimayModel setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public VimayModel setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    private double longitude;
    private String date;
    private String deviceId;
    public String getStatus() {
        return status;
    }
    public VimayModel setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getDate() {
        return date;
    }
    public VimayModel setDate(String date) {
        this.date = date;
        return this;
    }
    public String getDeviceId() {
        return deviceId;
    }

    public VimayModel setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

}
