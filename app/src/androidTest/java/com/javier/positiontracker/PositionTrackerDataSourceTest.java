package com.javier.positiontracker;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.model.UserLocation;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by javie on 2/17/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PositionTrackerDataSourceTest {

    private PositionTrackerDataSource mTarget;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void name() throws Exception {

        mTarget = new PositionTrackerDataSource(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void tearDown() throws Exception {

        mTarget.clean();
    }

    @Test
    public void dbShouldCreateLocation() throws Exception {

        // Arrange
        LatLng latLng = new LatLng(65.89, -110.0000);
        long date = new Date().getTime();
        UserLocation location = new UserLocation(latLng, date);

        // Act
        long rowId = mTarget.insertUserLocation(location);

        // Assert
        Assert.assertTrue(rowId > -1);
        Assert.assertTrue(mTarget.isClosed());
    }

    @Test
    public void dbShouldCreateLocationTime() throws Exception {

        // Arrange
        LatLng latLng = new LatLng(35.00, -15.3345);
        long date = new Date().getTime();
        UserLocation location = new UserLocation(latLng, date);
        mTarget.insertUserLocation(location);

        // Act
        long rowId = mTarget.insertUserLocationTime(
            location.getPosition().latitude,
            location.getPosition().longitude
        );

        // Assert
        Assert.assertTrue(rowId > -1);
        Assert.assertTrue(mTarget.isClosed());
    }

    @Test
    public void dbShouldReadLocationsWithRange() throws Exception {

        // Arrange
        Date date1 = getDate(2017, Calendar.JANUARY, 1);
        Date date2 = getDate(2017, Calendar.JANUARY, 25);
        Date date3 = getDate(2017, Calendar.FEBRUARY, 15);
        int expectedSize = 2;

        // Act
        mTarget.insertUserLocation(new UserLocation(new LatLng(14, 100), date1.getTime()));
        mTarget.insertUserLocation(new UserLocation(new LatLng(56, -140), date2.getTime()));
        mTarget.insertUserLocation(new UserLocation(new LatLng(24, 84), date3.getTime()));

        Date minDate = getDate(2017, Calendar.JANUARY, 24);
        Date maxDate = getDate(2017, Calendar.FEBRUARY, 24);

        List<UserLocation> locations = mTarget.readLocationsWithRange(minDate.getTime(), maxDate.getTime());

        // Assert
        Assert.assertNotNull(locations);
        Assert.assertEquals(expectedSize, locations.size());
    }

    @Test
    public void dbShouldNotReadLocationsWithRange() throws Exception {

        // Arrange
        Date date1 = getDate(2017, Calendar.JANUARY, 1);
        Date date2 = getDate(2017, Calendar.JANUARY, 25);
        Date date3 = getDate(2017, Calendar.FEBRUARY, 15);
        int expectedSize = 0;

        // Act
        mTarget.insertUserLocation(new UserLocation(new LatLng(14, 100), date1.getTime()));
        mTarget.insertUserLocation(new UserLocation(new LatLng(56, -140), date2.getTime()));
        mTarget.insertUserLocation(new UserLocation(new LatLng(24, 84), date3.getTime()));

        Date minDate = getDate(2017, Calendar.JANUARY, 26);
        Date maxDate = getDate(2017, Calendar.FEBRUARY, 14);

        List<UserLocation> locations = mTarget.readLocationsWithRange(minDate.getTime(), maxDate.getTime());

        // Assert
        Assert.assertNotNull(locations);
        Assert.assertEquals(expectedSize, locations.size());
    }

    @Test
    public void existingLocationReturnsTrue() throws Exception{

        // Arrange
        Date date1 = getDate(2017, Calendar.JANUARY, 1);
        UserLocation location1 = new UserLocation(new LatLng(14,100), date1.getTime());

        // Act
        mTarget.insertUserLocation(location1);
        boolean hasLocation = mTarget.hasLocation(location1);

        // Assert
        Assert.assertTrue(hasLocation);
    }

    @Test
    public void nonExistingLocationReturnsFalse() throws Exception{

        // Arrange
        Date date1 = getDate(2017, Calendar.JANUARY, 1);
        UserLocation location1 = new UserLocation(new LatLng(14,100), date1.getTime());

        // Act
        boolean hasLocation = mTarget.hasLocation(location1);

        // Assert
        Assert.assertFalse(hasLocation);
    }

    private Date getDate(int year, int month, int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }
}
