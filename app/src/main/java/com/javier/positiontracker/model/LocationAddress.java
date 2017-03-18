package com.javier.positiontracker.model;

import java.util.Objects;

/**
 * Created by javie on 3/16/2017.
 */

public class LocationAddress {

    private String mStreet;
    private String mArea;
    private String mPostal;
    private int mHour;
    private int mMinute;

    public LocationAddress(String street, String area, String postal) {

        mStreet = street;
        mArea = area;
        mPostal = postal;
    }

    @Override
    public int hashCode() {

        int result = 17;
        result = 31 * result + Objects.hashCode(mStreet);
        result = 31 * result + Objects.hashCode(mArea);
        result = 31 * result + Objects.hashCode(mPostal);

        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if(null == obj) {
            return false;
        }

        if(!(obj instanceof LocationAddress)) {
            return false;
        }

        LocationAddress otherAddress = (LocationAddress)obj;

        return
            mStreet.equals(otherAddress.getStreet())
            && mArea.equals(otherAddress.getArea())
            && mPostal.equals(otherAddress.getPostal());
    }

    public String getStreet() {
        return mStreet;
    }

    public String getArea() {
        return mArea;
    }

    public String getPostal() {
        return mPostal;
    }

    @Override
    public String toString() {

        return mStreet + ", " + mArea + ", " + mPostal + " at  " + mHour + ":" + mMinute;
    }

    public void setHour(int hour) {

        mHour = hour;
    }

    public void setMinute(int minute) {
        
        mMinute = minute;
    }
}
