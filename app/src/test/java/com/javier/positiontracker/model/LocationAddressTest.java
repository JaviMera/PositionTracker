package com.javier.positiontracker.model;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by javie on 3/16/2017.
 */
public class LocationAddressTest {

    private LocationAddress mTarget;
    private String mExpectedStreet = "harambe 404";
    private String mExpectedArea = "HEAVEN";
    private String mExpectedPostal = "666";

    @Before
    public void setUp() throws Exception {

        mTarget = new LocationAddress(
            mExpectedStreet,
            mExpectedArea,
            mExpectedPostal
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
    public void getPostal() throws Exception {

        // Assert
        Assert.assertEquals(mExpectedPostal, mTarget.getPostal());
    }
}