package com.javier.positiontracker.model;

import android.location.Address;

import java.util.Locale;
import java.util.Objects;

/**
 * Created by javie on 3/16/2017.
 */

public class LocationAddress {

    private Address mAddress;
    private int mHour;
    private int mMinute;

    public LocationAddress(Address address, int hour, int minute) {

        mAddress = address;
        mHour = hour;
        mMinute = minute;
    }

    @Override
    public int hashCode() {

        int result = 17;
        result = 31 * result + Objects.hashCode(mAddress.getAddressLine(0));
        result = 31 * result + Objects.hashCode(mAddress.getAdminArea());

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

        return mAddress.getAddressLine(0).split(",")[0];
    }

    public String getArea() {

        return mAddress.getAdminArea();
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
