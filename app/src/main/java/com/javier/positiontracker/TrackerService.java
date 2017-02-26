package com.javier.positiontracker;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.clients.GoogleClient;
import com.javier.positiontracker.clients.LocationUpdate;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.model.UserLocation;

/**
 * Created by javie on 2/24/2017.
 */

public class TrackerService extends IntentService
    implements LocationUpdate {

    public static final String TAG = TrackerService.class.getSimpleName();
    public static final String CONNECTION_STATUS = "connection";

    private GoogleClient mClient;
    private Location mLastLocation;

    public TrackerService() {

        super("TrackerService");
    }

    @Override
    public void onCreate() {

        super.onCreate();
        mClient = new GoogleClient(this, this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mClient.connect();

        Intent sendIntent = new Intent();
        sendIntent.putExtra(CONNECTION_STATUS, "CONNECTED");
        sendIntent.setAction(TAG);
        sendBroadcast(sendIntent);
    }

    @Override
    public void onDestroy() {

        mClient.disconnect();
    }

    @Override
    public void onNewLocation(Location location) {

        // If it's a new location, proceed to storing it in the database
        if(mLastLocation != location) {

            mLastLocation = location;

            PositionTrackerDataSource source = new PositionTrackerDataSource(this);

            UserLocation userLocation = new UserLocation(
                new LatLng(location.getLatitude(), location.getLongitude()),
                location.getTime()
            );

            // Check if the location already exists in the database
            if(source.hasLocation(userLocation))
                return;

            source.insertUserLocation(userLocation);
        }
        // TODO: implement time accumulation in same location
        else {

        }
    }
}
