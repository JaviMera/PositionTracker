package com.javier.positiontracker;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.javier.positiontracker.model.UserLocation;

/**
 * Created by javie on 3/8/2017.
 */

public class LocationBroadcast {

    private LocalBroadcastManager mManager;

    public static final String LOCATION_CHANGE = LocationBroadcast.class.getSimpleName() + ".NEW_LOCATION";
    public static final String LOCATION_CHANGE_KEY = "new_location";

    public LocationBroadcast(LocalBroadcastManager manager) {

        mManager = manager;
    }

    public void send(UserLocation newLocation) {

        // Create intent with new location information
        Intent intent = new Intent(LOCATION_CHANGE);
        intent.putExtra(LOCATION_CHANGE_KEY, newLocation);

        // Broadcast the intent to main activity
        mManager.sendBroadcast(intent);
    }
}
