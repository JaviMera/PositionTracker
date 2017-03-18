package com.javier.positiontracker;

import android.location.Address;

import com.javier.positiontracker.model.LocationAddress;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by javie on 3/16/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class LocationAddressTest {

    private LocationAddress mTarget;
    private String mExpectedStreet = "404 harambe, Zoo Park";
    private String mExpectedArea = "HEAVEN";
    private int mExpectedHour = 2;
    private int mExpectedMinute = 20;

    @Before
    public void setUp() throws Exception {

        Address address = Mockito.mock(Address.class);
        when(address.getAddressLine(0)).thenReturn(mExpectedStreet);
        when(address.getAdminArea()).thenReturn(mExpectedArea);

        mTarget = new LocationAddress(
            address,
            mExpectedHour,
            mExpectedMinute
        );
    }

    @Test
    public void getStreet() throws Exception {

        // Assert
        Assert.assertEquals(mExpectedStreet, mTarget.getStreet());
    }

    @Test
    public void getArea() throws Exception {

        // Assert
        Assert.assertEquals(mExpectedArea, mTarget.getArea());
    }

    @Test
    public void getHour() throws Exception {

        // Assert
        Assert.assertEquals(mExpectedHour, mTarget.getHour());
    }

    @Test
    public void getMinute() throws Exception {

        // Assert
        Assert.assertEquals(mExpectedMinute, mTarget.getMinute());
    }
}