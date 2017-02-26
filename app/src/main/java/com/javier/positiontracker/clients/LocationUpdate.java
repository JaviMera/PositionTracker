package com.javier.positiontracker.clients;

import android.location.Location;

/**
 * Created by javie on 2/24/2017.
 */
public interface LocationUpdate {

    void onNewLocation(Location location);
}
