package com.javier.positiontracker.test;

import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.model.UserLocation;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Created by javie on 2/17/2017.
 */
public class UserLocationTest {

    private UserLocation mTarget;
    private LatLng mLatLong;
    private long mDate;

    @Before
    public void setUp() throws Exception {

        mLatLong = new LatLng(1000, -2000);
        mDate = new Date().getTime();

        mTarget = new UserLocation(
            mLatLong,
            mDate
        );
    }

    @Test
    public void getLatLong() throws Exception {

        // Assert
        Assert.assertEquals(mLatLong.latitude, mTarget.getLatLong().latitude);
        Assert.assertEquals(mLatLong.longitude, mTarget.getLatLong().longitude);
    }

    @Test
    public void getDate() throws Exception {

        // Assert
        Assert.assertEquals(mDate, mTarget.getDate());
    }

}