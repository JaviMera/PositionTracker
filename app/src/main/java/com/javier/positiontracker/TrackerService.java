package com.javier.positiontracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.clients.GoogleClient;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.model.UserLocation;

/**
 * Created by javie on 2/24/2017.
 */

public class TrackerService extends Service
    implements com.javier.positiontracker.clients.LocationUpdate {

    private GoogleClient mClient;
    private UserLocation mLastLocation;
    private IBinder mBinder;
    private LocationBroadcast mLocationBroadcast;
    private LocationThreshold mLocationThreshold;
    private LocationCounter mLocationCounter;
    private LocationNotification mLocationNotification;

    @Override
    public void onCreate() {

        super.onCreate();
        mBinder = new ServiceBinder();
        mClient = new GoogleClient(this, this);
        mLocationBroadcast = new LocationBroadcast(LocalBroadcastManager.getInstance(this));

        mLocationNotification = new LocationNotification(
            this,
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)
        );

        mLocationThreshold = new LocationThreshold();
        mLocationCounter = new LocationCounter();
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
        if(mLastLocation == null || !mLastLocation.equals(newLocation)) {

            mLastLocation = newLocation;
            mLocationCounter.reset();

            PositionTrackerDataSource source = new PositionTrackerDataSource(this);

            // Check if the location already exists in the database
            if(!source.hasLocation(mLastLocation)) {

                source.insertUserLocation(mLastLocation);
            }

            // Notify the activity about a location change
            mLocationBroadcast.send(mLastLocation);
        }
        // TODO: implement time accumulation in same location
        else {

            if(mLocationThreshold.hasValidThreshold()) {

                if(mLocationCounter.getCounter() >= mLocationThreshold.getThreshold()) {

                    mLocationNotification.send(
                        "Position Tracker",
                        "It's been " + mLocationThreshold.getThreshold() / 1000 / 60 + " minute(s).",
                        R.mipmap.ic_launcher);

                    mLocationThreshold.reset();
                    mLocationCounter.reset();
                }
                else {

                    mLocationCounter.increment(mClient.getTimeInterval());
                }
            }
        }
    }

    public void trackTime(int time) {

        // Store the time notifications in milliseconds
        mLocationThreshold.setThreshold(time * 60 * 1000);
    }

    public class ServiceBinder extends Binder {

        TrackerService getService() {

            return TrackerService.this;
        }
    }
}
