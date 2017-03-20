package com.javier.positiontracker.model;

import android.location.Address;

import java.util.Locale;
import java.util.Objects;

/**
 * Created by javie on 3/16/2017.
 */

public class LocationAddress {

    private String mStreet;
    private String mArea;
    private int mHour;
    private int mMinute;

    public LocationAddress(String street, String area) {

        mStreet = street;
        mArea = area;
    }

    @Override
    public int hashCode() {

        int result = 17;
        result = 31 * result + Objects.hashCode(mStreet);
        result = 31 * result + Objects.hashCode(mArea);
        result = 31 * result + Objects.hashCode(mHour);
        result = 31 * result + Objects.hashCode(mMinute);

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
            getStreet().equals(otherAddress.getStreet())
            && getArea().equals(otherAddress.getArea());
    }

    public String getStreet() {

        return mStreet;
    }

    public String getArea() {

        return mArea;
    }

    public void setHour(int hour) {

        mHour = hour;
    }

    public void setMinute(int minute) {

        mMinute = minute;
    }

    public String getHour() {

        return mHour < 10 ? "0" + mHour : mHour + "";
    }

    public String getMinute() {

        return mMinute < 10 ? "0" + mMinute : mMinute + "";
    }

    public String getFullAddress() {

        return String.format(Locale.ENGLISH,
            "%30s, %-30s %s:%s\r\n\r\n", getStreet(), getArea(), getHour(), getMinute()
        );
    }
}
