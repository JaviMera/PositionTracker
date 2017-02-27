package com.javier.positiontracker;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

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

    private GoogleClient mClient;
    private Location mLastLocation;
    private IBinder mBinder;

    @Override
    public void onCreate() {

        super.onCreate();
        mBinder = new ServiceBinder();
        mClient = new GoogleClient(this, this);
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

    public class ServiceBinder extends Binder {

        TrackerService getService() {

            return TrackerService.this;
        }
    }
}
