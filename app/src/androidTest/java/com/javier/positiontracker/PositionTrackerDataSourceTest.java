package com.javier.positiontracker;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.model.UserLocation;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

/**
 * Created by javie on 2/17/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PositionTrackerDataSourceTest {

    private PositionTrackerDataSource mTarget;

    @Before
    public void name() throws Exception {

        mTarget = new PositionTrackerDataSource(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void dbShouldCreateLocation() throws Exception {

        // Arrange
        LatLng latLng = new LatLng(2000, -3000);
        long date = new Date().getTime();
        UserLocation location = new UserLocation(latLng, date);

        // Act
        long rowId = mTarget.create(location);

        // Assert
        Assert.assertTrue(rowId > -1);
    }
}
