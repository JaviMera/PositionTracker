package com.javier.positiontracker.model;

import android.location.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by javie on 2/26/2017.
 */

public class FakeLocations {

    public static List<Location> getLocations(String provider) {

        final Location location1 = new Location(provider);
        location1.setLatitude(36.1519752);
        location1.setLongitude(-96.1581974);
        location1.setTime(getDate(2017, Calendar.JANUARY, 15).getTime());

        final Location location2 = new Location(provider);
        location2.setLatitude(35.4813524);
        location2.setLongitude(-98.0396437);
        location2.setTime(getDate(2017, Calendar.FEBRUARY, 27).getTime());

        final Location location3 = new Location(provider);
        location3.setLatitude(35.7305292);
        location3.setLongitude(-95.510653);
        location3.setTime(getDate(2017, Calendar.FEBRUARY, 22).getTime());

        final Location location4 = new Location(provider);
        location4.setLatitude(37.1398522);
        location4.setLongitude(-98.1541764);
        location4.setTime(getDate(2017, Calendar.FEBRUARY, 25).getTime());

        final Location location5 = new Location(provider);
        location5.setLatitude(35.9045244);
        location5.setLongitude(-95.0135555);
        location5.setTime(getDate(2017, Calendar.FEBRUARY, 20).getTime());

        return new ArrayList<Location>(){
            {
                add(location1);
                add(location2);
                add(location3);
                add(location4);
                add(location5);
            }
        };
    }

    private static Date getDate(int year, int month, int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        return calendar.getTime();
    }
}
