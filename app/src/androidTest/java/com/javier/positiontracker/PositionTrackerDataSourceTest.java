package com.javier.positiontracker;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.model.LocationAddress;
import com.javier.positiontracker.model.TimeLimit;
import com.javier.positiontracker.model.UserLocation;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
    public void dbShouldReadAllLocations() throws Exception {

        // Arrange
        Date date1 = getDate(2017, Calendar.JANUARY, 1);
        Date date2 = getDate(2017, Calendar.JANUARY, 25);
        Date date3 = getDate(2017, Calendar.FEBRUARY, 15);
        int expectedSize = 2;

        // Act
        mTarget.insertUserLocation(new UserLocation(new LatLng(14, 100), date1.getTime()));
        mTarget.insertUserLocation(new UserLocation(new LatLng(56, -140), date2.getTime()));
        mTarget.insertUserLocation(new UserLocation(new LatLng(24, 84), date3.getTime()));

        List<UserLocation> locations = mTarget.readAllLocations();

        // Assert
        Assert.assertNotNull(locations);
        Assert.assertEquals(3, locations.size());
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
    public void dbShouldInsertTimeLimit() throws Exception {

        // Arrange
        long minutes = 2 * 60 * 1000;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        long createdAt = c.getTimeInMillis();

        // Act
        long rowsAffected = mTarget.insertTimeLimit(minutes, createdAt);

        // Assert
        Assert.assertTrue(rowsAffected > 0);
    }

    @Test
    public void dbShouldReadNullWithoutTimeLimit() throws Exception {

        // Act
        TimeLimit timeLimit = mTarget.readTimeLimit();

        // Assert
        Assert.assertNull(timeLimit);
    }

    @Test
    public void dbShouldReadTimeLimit() throws Exception {

        // Arrange
        long minutes = 2 * 60 * 1000;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        long createdAt = c.getTimeInMillis();

        // Act
        mTarget.insertTimeLimit(minutes, createdAt);
        TimeLimit timeLimit = mTarget.readTimeLimit();

        // Assert
        Assert.assertNotNull(timeLimit);
        Assert.assertEquals(minutes, timeLimit.getTime());
        Assert.assertEquals(createdAt, timeLimit.getCreatedAt());
    }

    @Test
    public void dbShouldDeleteTimeLimit() throws Exception {

        // Arrange
        long minutes = 2 * 60 * 1000;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        long createdAt = c.getTimeInMillis();

        // Act
        mTarget.insertTimeLimit(minutes, createdAt);
        long affectedRow = mTarget.deleteTimeLimit();

        // Assert
        Assert.assertTrue(affectedRow == 1);
    }

    @Test
    public void dbShouldReadAllDistinctLocationDates() throws Exception {

        // Arrange
        Date date1 = getDate(2017, Calendar.JANUARY, 1);
        Date date2 = getDate(2016, Calendar.JANUARY, 1);
        Date date3 = getDate(2017, Calendar.JANUARY, 1);

        // Act
        long d1 = date1.getTime();
        long d2 = date2.getTime();
        long d3 = date3.getTime();
        mTarget.insertUserLocation(new UserLocation(new LatLng(14, 100), d1));
        mTarget.insertUserLocation(new UserLocation(new LatLng(56, -140), d2));
        mTarget.insertUserLocation(new UserLocation(new LatLng(24, 84), d3));
        List<Long> dates = mTarget.readAllDates();

        // Assert
        Assert.assertNotNull(dates);
        Assert.assertEquals(2, dates.size());
    }

    @Test
    public void dbShouldInsertLocationAddress() throws Exception {

        // Arrange
        double lat = 34.0000;
        double longi = -100.0000;
        String street = "harambe 404";
        String area = "HEAVEN";
        String postal = "666";
        LocationAddress address = new LocationAddress(street, area, postal);

        // Act
        long rowId = mTarget.insertLocationAddress(lat, longi, address);

        // Assert
        Assert.assertTrue(rowId > -1);
    }

    @Test
    public void dbShouldReadLocationAddress() throws Exception {

        // Arrange
        double lat = 34.0000;
        double longi = -100.0000;
        String street = "harambe 404";
        String area = "HEAVEN";
        String postal = "666";
        LocationAddress expectedAddress = new LocationAddress(street, area, postal);

        // Act
        mTarget.insertLocationAddress(lat, longi, expectedAddress);
        LocationAddress actualAddress = mTarget.readLocationAddress(lat, longi);

        // Assert
        Assert.assertTrue(expectedAddress.equals(actualAddress));
    }

    private Date getDate(int year, int month, int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }
}
