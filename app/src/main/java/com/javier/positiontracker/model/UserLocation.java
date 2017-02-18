package com.javier.positiontracker.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by javie on 2/17/2017.
 */
public class UserLocation {

    private LatLng mLatLong;
    private long mDate;

    public UserLocation(LatLng latLng, long date) {

        mLatLong = latLng;
        mDate = date;
    }

    public LatLng getLatLong() {
        return mLatLong;
    }

    public long getDate() {
        return mDate;
    }
}
