package com.javier.positiontracker.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

/**
 * Created by javie on 2/17/2017.
 */
public class UserLocation implements Parcelable{

    private LatLng mLatLong;
    private long mDate;
    private int mMinute;
    private int mHour;

    public UserLocation(LatLng latLng, long date, int hour, int minute) {

        mLatLong = latLng;
        mDate = date;
        mHour = hour;
        mMinute = minute;
    }

    private UserLocation(Parcel in) {
        mLatLong = in.readParcelable(LatLng.class.getClassLoader());
        mDate = in.readLong();
        mHour = in.readInt();
        mMinute = in.readInt();
    }

    public static final Creator<UserLocation> CREATOR = new Creator<UserLocation>() {
        @Override
        public UserLocation createFromParcel(Parcel in) {
            return new UserLocation(in);
        }

        @Override
        public UserLocation[] newArray(int size) {
            return new UserLocation[size];
        }
    };

    public LatLng getPosition() {
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
        return mLatLong.equals(otherLocation.getPosition());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeParcelable(mLatLong, i);
        parcel.writeLong(mDate);
        parcel.writeInt(mHour);
        parcel.writeInt(mMinute);
    }

    public int getHour() {

        return mHour;
    }

    public int getMinute() {

        return mMinute;
    }
}
