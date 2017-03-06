package com.javier.positiontracker.test;

import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.model.UserLocation;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

/**
 * Created by javie on 2/17/2017.
 */
@RunWith(MockitoJUnitRunner.class)
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
        Assert.assertEquals(mLatLong.latitude, mTarget.getPosition().latitude);
        Assert.assertEquals(mLatLong.longitude, mTarget.getPosition().longitude);
    }

    @Test
    public void getDate() throws Exception {

        // Assert
        Assert.assertEquals(mDate, mTarget.getDate());
    }


    @Test
    public void nullLocationReturnFalse() throws Exception {

        // Act
        boolean sameLocation = mTarget.equals(null);

        // Assert
        Assert.assertFalse(sameLocation);
    }

    @Test
    public void differentTypeReturnFalse() throws Exception {

        // Arrange
        LatLng latLng = new LatLng(2,2);

        // Act
        boolean sameLocation = mTarget.equals(latLng);

        // Assert
        Assert.assertFalse(sameLocation);
    }

    @Test
    public void sameLocationReturnTrue() throws Exception {

        // Arrange
        UserLocation userLocation = new UserLocation(mLatLong, mDate);

        // Act
        boolean sameLocations = mTarget.equals(userLocation);

        // Assert
        Assert.assertTrue(mTarget.hashCode() == userLocation.hashCode());
        Assert.assertTrue(sameLocations);
    }

    @Test
    public void differentLocationReturnFalse() throws Exception {

        // Arrange
        UserLocation location = new UserLocation(new LatLng(100, 120), System.currentTimeMillis());

        // Act
        boolean sameLocation = mTarget.equals(location);

        // Assert
        Assert.assertFalse(sameLocation);
    }
}