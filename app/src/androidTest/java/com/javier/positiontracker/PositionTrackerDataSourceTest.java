package com.javier.positiontracker;

import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.databases.PositionTrackerSQLiteHelper;
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
        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        UserLocation location = new UserLocation(
            latLng,
            date.getTime(),
            hour,
            minute);

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
        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Act
        mTarget.insertUserLocation(new UserLocation(new LatLng(14, 100), date1.getTime(), hour, minute));
        mTarget.insertUserLocation(new UserLocation(new LatLng(56, -140), date2.getTime(), hour, minute));
        mTarget.insertUserLocation(new UserLocation(new LatLng(24, 84), date3.getTime(), hour, minute));

        List<UserLocation> locations = mTarget.readAllLocations();

        // Assert
        Assert.assertNotNull(locations);
        Assert.assertEquals(3, locations.size());
        Assert.assertEquals(locations.get(0).getHour(), hour);
        Assert.assertEquals(locations.get(0).getMinute(), minute);
    }

    @Test
    public void dbShouldReadLocationsWithRange() throws Exception {

        // Arrange
        Date date1 = getDate(2017, Calendar.JANUARY, 1);
        Date date2 = getDate(2017, Calendar.JANUARY, 25);
        Date date3 = getDate(2017, Calendar.FEBRUARY, 15);
        int expectedSize = 2;

        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Act
        mTarget.insertUserLocation(new UserLocation(new LatLng(14, 100), date1.getTime(), hour, minute));
        mTarget.insertUserLocation(new UserLocation(new LatLng(56, -140), date2.getTime(), hour, minute));
        mTarget.insertUserLocation(new UserLocation(new LatLng(24, 84), date3.getTime(), hour, minute));

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

        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Act
        mTarget.insertUserLocation(new UserLocation(new LatLng(14, 100), date1.getTime(), hour, minute));
        mTarget.insertUserLocation(new UserLocation(new LatLng(56, -140), date2.getTime(), hour, minute));
        mTarget.insertUserLocation(new UserLocation(new LatLng(24, 84), date3.getTime(), hour, minute));

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

        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Act
        mTarget.insertUserLocation(new UserLocation(new LatLng(14, 100), date1.getTime(), hour, minute));
        mTarget.insertUserLocation(new UserLocation(new LatLng(56, -140), date2.getTime(), hour, minute));
        mTarget.insertUserLocation(new UserLocation(new LatLng(24, 84), date3.getTime(), hour, minute));
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

    @Test
    public void dbShouldInsertLastLocation() throws Exception {

        // Arrange
        String provider = "harambeGPS";
        double latitude = 80.909090;
        double longitude = -100.219021;
        Location location = new Location(provider);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        // Act
        long rowId = mTarget.insertLastLocation(PositionTrackerSQLiteHelper.LAST_LOCATION_ID_VALUE, location);

        // Assert
        Assert.assertTrue(rowId > -1);
    }

    @Test
    public void dbShouldReadLastLocation() throws Exception {

        // Arrange
        String provider = "harambeGPS";
        double latitude = 80.909090;
        double longitude = -100.219021;
        Location expectedLocation = new Location(provider);
        expectedLocation.setLatitude(latitude);
        expectedLocation.setLongitude(longitude);

        // Act
        mTarget.insertLastLocation(PositionTrackerSQLiteHelper.LAST_LOCATION_ID_VALUE, expectedLocation);
        Location actualLocation = mTarget.readLastLocation();

        // Assert
        Assert.assertNotNull(actualLocation);
    }

    @Test
    public void dbShouldNotReadLastLocation() throws Exception {

        // Act
        Location actualLocation = mTarget.readLastLocation();

        // Assert
        Assert.assertNull(actualLocation);
    }

    @Test
    public void dbShouldNotContainLastLocation() throws Exception {

        // Arrange
        String provider = "harambeGPS";
        double latitude = 80.909090;
        double longitude = -100.219021;
        Location location1 = new Location(provider);
        location1.setLatitude(latitude);
        location1.setLongitude(longitude);

        latitude = 80.929292;
        longitude = -100.424343;
        Location location2 = new Location(provider);
        location2.setLatitude(latitude);
        location2.setLongitude(longitude);

        // Act
        mTarget.insertLastLocation(PositionTrackerSQLiteHelper.LAST_LOCATION_ID_VALUE, location1);
        boolean containsLocation = mTarget.containsLocation(location2);

        // Assert
        Assert.assertFalse(containsLocation);
    }

    @Test
    public void dbShouldContainLastLocation() throws Exception {

        // Arrange
        String provider = "harambeGPS";
        double latitude = 80.909090;
        double longitude = -100.219021;
        Location location1 = new Location(provider);
        location1.setLatitude(latitude);
        location1.setLongitude(longitude);

        Location location2 = new Location(provider);
        location2.setLatitude(latitude);
        location2.setLongitude(longitude);

        // Act
        mTarget.insertLastLocation(PositionTrackerSQLiteHelper.LAST_LOCATION_ID_VALUE, location1);
        boolean containsLocation = mTarget.containsLocation(location2);

        // Assert
        Assert.assertTrue(containsLocation);
    }

    @Test
    public void dbShouldUpdateLastLocation() throws Exception {

        // Arrange
        String provider = "harambeGPS";
        double latitude = 80.909090;
        double longitude = -100.219021;
        Location location1 = new Location(provider);
        location1.setLatitude(latitude);
        location1.setLongitude(longitude);

        latitude = 80.929292;
        longitude = -100.424343;
        Location location2 = new Location(provider);
        location2.setLatitude(latitude);
        location2.setLongitude(longitude);

        // Act
        mTarget.insertLastLocation(PositionTrackerSQLiteHelper.LAST_LOCATION_ID_VALUE, location1);
        long affectedRow = mTarget.updateLastLocation(PositionTrackerSQLiteHelper.LAST_LOCATION_ID_VALUE, location2);
        Location actualLocation = mTarget.readLastLocation();
        // Assert
        Assert.assertTrue(affectedRow == 1);
        Assert.assertEquals(location2.getLatitude(), actualLocation.getLatitude());
        Assert.assertEquals(location2.getLongitude(), actualLocation.getLongitude());
        Assert.assertEquals(location2.getProvider(), actualLocation.getProvider());
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
