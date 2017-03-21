package com.javier.positiontracker.model;

import android.location.Address;

import java.util.Locale;

/**
 * Created by javie on 3/20/2017.
 */

public class NullAddress extends Address {

    public NullAddress(Locale locale) {
        super(locale);
    }

    @Override
    public String getAddressLine(int index) {

        return "Unknown";
    }

    @Override
    public String getAdminArea() {

        return "Unknown";
    }
}
