package com.javier.positiontracker;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.clients.GoogleClient;
import com.javier.positiontracker.clients.LocationUpdate;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.model.UserLocation;

/**
 * Created by javie on 2/24/2017.
 */

public class TrackerService extends Service
    implements LocationUpdate {

    public static final String LOCATION_CHANGE = TrackerService.class.getSimpleName() + ".NEW_LOCATION";
    public static final String LOCATION_CHANGE_KEY = "new_location";

    private GoogleClient mClient;
    private UserLocation mLastLocation;
    private IBinder mBinder;
    private LocalBroadcastManager mBroadcast;

    @Override
    public void onCreate() {

        super.onCreate();
        mBinder = new ServiceBinder();
        mClient = new GoogleClient(this, this);
        mBroadcast = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onDestroy() {

        mClient.disconnect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    public void trackPosition() {

        mClient.connect();
    }

    @Override
    public void onNewLocation(Location location) {

        UserLocation newLocation = new UserLocation(
            new LatLng(location.getLatitude(), location.getLongitude()),
            location.getTime());

        // If it's a new location, proceed to storing it in the database
        if(mLastLocation != newLocation) {

            mLastLocation = newLocation;

            PositionTrackerDataSource source = new PositionTrackerDataSource(this);

            // Check if the location already exists in the database
            if(!source.hasLocation(mLastLocation)) {

                source.insertUserLocation(mLastLocation);
            }

            // Notify the activity about a location change
            Intent intent = new Intent(LOCATION_CHANGE);
            intent.putExtra(LOCATION_CHANGE_KEY, mLastLocation);

            mBroadcast.sendBroadcast(intent);
        }
        // TODO: implement time accumulation in same location
        else {

        }
    }

    public void trackTime(int time) {

    }

    public class ServiceBinder extends Binder {

        TrackerService getService() {

            return TrackerService.this;
        }
    }
}
