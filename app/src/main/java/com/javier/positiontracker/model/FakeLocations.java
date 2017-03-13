package com.javier.positiontracker.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by javie on 2/26/2017.
 */

public class FakeLocations {

    public static List<UserLocation> getLocations() {

        final UserLocation location1 = new UserLocation(
            new LatLng(36.1519752, -96.1581974),
            getDate(2017, Calendar.JANUARY, 15).getTime()
        );

        final UserLocation location2 = new UserLocation(
            new LatLng(35.4813524, -98.0396437),
            getDate(2017, Calendar.FEBRUARY, 27).getTime()
        );

        return new ArrayList<UserLocation>(){
            {
                add(location1);
                add(location2);
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
