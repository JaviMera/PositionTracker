package com.javier.positiontracker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by javie on 3/19/2017.
 */

public class LocationProvider implements Parcelable {

    private boolean mEnabled;

    public LocationProvider(boolean enabled) {

        mEnabled =  enabled;
    }

    private LocationProvider(Parcel in) {
        mEnabled = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mEnabled ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocationProvider> CREATOR = new Creator<LocationProvider>() {
        @Override
        public LocationProvider createFromParcel(Parcel in) {
            return new LocationProvider(in);
        }

        @Override
        public LocationProvider[] newArray(int size) {
            return new LocationProvider[size];
        }
    };

    public boolean isEnabled() {

        return mEnabled;
    }
}
