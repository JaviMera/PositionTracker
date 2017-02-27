package com.javier.positiontracker.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

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

    @Override
    public int hashCode() {

        int result = 17;
        result = 31 * result + Objects.hashCode(mLatLong.latitude);
        result = 31 * result + Objects.hashCode(mLatLong.longitude);

        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if(null == obj) {
            return false;
        }

        if(!(obj instanceof UserLocation)) {
            return false;
        }

        UserLocation otherLocation = (UserLocation)obj;
        return mLatLong.equals(otherLocation.getLatLong());
    }
}
