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
import com.javier.positiontracker.broadcastreceivers.BroadcastBase;
import com.javier.positiontracker.broadcastreceivers.BroadcastNotification;
import com.javier.positiontracker.clients.GoogleClient;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.broadcastreceivers.BroadcastLocation;
import com.javier.positiontracker.location.LocationCounter;
import com.javier.positiontracker.location.LocationNotification;
import com.javier.positiontracker.location.LocationThreshold;
import com.javier.positiontracker.model.TimeLimit;
import com.javier.positiontracker.model.UserLocation;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by javie on 2/24/2017.
 */

public class TrackerService extends Service
    implements com.javier.positiontracker.clients.LocationUpdate {

    private GoogleClient mClient;
    private UserLocation mLastLocation;
    private IBinder mBinder;
    private BroadcastBase mBroadcastLocation;
    private BroadcastBase mBroadcastNotification;
    private LocationThreshold mLocationThreshold;
    private LocationCounter mLocationCounter;
    private LocationNotification mLocationNotification;

    @Override
    public void onCreate() {

        mBinder = new ServiceBinder();
        mClient = new GoogleClient(this, this);
        mBroadcastLocation = new BroadcastLocation(LocalBroadcastManager.getInstance(this));
        mBroadcastNotification = new BroadcastNotification(LocalBroadcastManager.getInstance(this));

        mLocationNotification = new LocationNotification(
            this,
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)
        );

        mLocationThreshold = new LocationThreshold();
        mLocationCounter = new LocationCounter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Begin tracking location when the service is first started
        startTracking();

        // By returning START_STICKY we explicitly tell the Android os that the service should be
        // restarted when the last client unbinds from it.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void startTracking() {

        if(!mClient.isConnected()) {

            mClient.connect();
        }
    }

    public void stopTracking() {

        mClient.disconnect();
    }

    public TimeLimit getTimeLimit() {

        PositionTrackerDataSource source = new PositionTrackerDataSource(this);

        return source.readTimeLimit();
    }

    public long removeTimeLimit() {

        PositionTrackerDataSource source = new PositionTrackerDataSource(this);
        return source.deleteTimeLimit();
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
            mBroadcastLocation.send(mLastLocation);
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

                    // Send a broadcast message to the activity about the notification being launched
                    mBroadcastNotification.send(null);

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

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        PositionTrackerDataSource source = new PositionTrackerDataSource(this);

        // Delete any existing notification
        source.deleteTimeLimit();

        // Create the new notification
        source.insertTimeLimit(
            time,
            calendar.getTimeInMillis()
        );
    }

    public class ServiceBinder extends Binder {

        TrackerService getService() {

            return TrackerService.this;
        }
    }
}
