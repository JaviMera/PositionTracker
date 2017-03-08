package com.javier.positiontracker;

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
import com.javier.positiontracker.location.LocationBroadcast;
import com.javier.positiontracker.location.LocationCounter;
import com.javier.positiontracker.location.LocationNotification;
import com.javier.positiontracker.location.LocationThreshold;
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

                    // Send a notification to the user when they've reached their time limit
                    // at the same location
                    mLocationNotification.send(
                        getString(R.string.notification_title),
                        String.format(getString(R.string.notification_content), mLocationThreshold.getThreshold() / 1000 / 60 ),
                        R.mipmap.ic_launcher);

                    // Reset both threshold and counter since the user has reached the time limit
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

        // Reset the counter everytime there is a new time threshold set by the user
        mLocationCounter.reset();
    }

    public class ServiceBinder extends Binder {

        TrackerService getService() {

            return TrackerService.this;
        }
    }
}
