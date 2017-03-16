package com.javier.positiontracker;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.broadcastreceivers.BroadcastBase;
import com.javier.positiontracker.broadcastreceivers.BroadcastNotification;
import com.javier.positiontracker.clients.GoogleClient;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.broadcastreceivers.BroadcastLocation;
import com.javier.positiontracker.model.LocationCounter;
import com.javier.positiontracker.model.LocationNotification;
import com.javier.positiontracker.model.LocationThreshold;
import com.javier.positiontracker.model.TimeLimit;
import com.javier.positiontracker.model.UserLocation;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by javie on 2/24/2017.
 */

public class TrackerService extends Service
    implements com.javier.positiontracker.clients.LocationUpdate {

    public static final float SMALLEST_DISTANCE = 20.0f;
    private GoogleClient mClient;
    private UserLocation mLastLocation;
    private IBinder mBinder;
    private BroadcastBase mBroadcastLocation;
    private BroadcastBase mBroadcastNotification;

    private LocationThreshold mLocationThreshold;
    private LocationCounter mLocationCounter;
    private LocationNotification mLocationNotification;
    private Location mLastGoogleLocation;

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

    private long getCurrentDateInMilliseconds(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime().getTime();
    }

    private UserLocation createUserLocation(Location location) {

        return new UserLocation(
            new LatLng(location.getLatitude(), location.getLongitude()),
            getCurrentDateInMilliseconds(new Date())
        );
    }

    @Override
    public void onNewLocation(Location location) {

        float distance = -1.0f;

        if(mLastGoogleLocation != null) {

            distance = mLastGoogleLocation.distanceTo(location);
        }

        mLastGoogleLocation = location;

        UserLocation newLocation = createUserLocation(location);

        // Notify the activity, if any is listening, about a location change
        mBroadcastLocation.send(newLocation);

        // Check if the new location is considered a new location based on the distance between
        // last location and new location
        if(distance != -1.0f && distance >= SMALLEST_DISTANCE) {

            PositionTrackerDataSource source = new PositionTrackerDataSource(this);

            // Check if the location already exists in the database
            if (!source.hasLocation(newLocation)) {

                source.insertUserLocation(newLocation);
            }

            // For every new location, reset the counter as the user has clearly started moving
            mLocationCounter.reset();

            // Save the new location as our last known location
            mLastLocation = newLocation;
        }

        else {

            // If it's not a new location, then check if there is a time limit setup
            // A valid threshold will be a value > 0
            if(mLocationThreshold.hasValidThreshold()) {

                // Check if the counter is greater or equal than the threshold
                if(mLocationCounter.getCounter() >= mLocationThreshold.getThreshold()) {

                    // Send a notification to the user when they've reached their time limit
                    // at the same location
                    mLocationNotification.send(
                            getString(R.string.notification_title),
                            String.format(getString(R.string.notification_content), mLocationThreshold.getThreshold() / 1000 / 60 ),
                            R.mipmap.ic_launcher);

                    // Send a broadcast message to the activity about the notification being launched
                    // Pass in null as we don't care what gets passed to the receiver
                    mBroadcastNotification.send(null);

                    // Remove the time limit from the database once it has been launched
                    removeTimeLimit();

                    // Reset both threshold and counter since the user has reached the time limit
                    mLocationThreshold.reset();
                    mLocationCounter.reset();
                }
                else {

                    // If the counter is still less than the threshold, then increment it by the
                    // location update time
                    mLocationCounter.increment(mClient.getTimeInterval());
                }
            }
        }
    }

    public void trackTime(long time, long createdAt) {

        // Store the time notifications in milliseconds
        mLocationThreshold.setThreshold(time * 60 * 1000);

        // Reset the counter everytime there is a new time threshold set by the user
        mLocationCounter.reset();

        PositionTrackerDataSource source = new PositionTrackerDataSource(this);

        // Delete any existing notification
        source.deleteTimeLimit();

        // Create the new notification
        source.insertTimeLimit(
            time,
            createdAt
        );
    }

    public class ServiceBinder extends Binder {

        public TrackerService getService() {

            return TrackerService.this;
        }
    }
}
