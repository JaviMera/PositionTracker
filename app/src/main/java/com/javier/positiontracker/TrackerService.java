package com.javier.positiontracker;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.broadcastreceivers.BroadcastBase;
import com.javier.positiontracker.broadcastreceivers.BroadcastGps;
import com.javier.positiontracker.broadcastreceivers.BroadcastNotification;
import com.javier.positiontracker.clients.GoogleClient;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.broadcastreceivers.BroadcastLocation;
import com.javier.positiontracker.databases.PositionTrackerSQLiteHelper;
import com.javier.positiontracker.model.LocationAddress;
import com.javier.positiontracker.model.LocationCounter;
import com.javier.positiontracker.model.LocationNotification;
import com.javier.positiontracker.model.LocationProvider;
import com.javier.positiontracker.model.LocationThreshold;
import com.javier.positiontracker.model.NullAddress;
import com.javier.positiontracker.model.TimeLimit;
import com.javier.positiontracker.model.UserLocation;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by javie on 2/24/2017.
 */

public class TrackerService extends Service
    implements com.javier.positiontracker.clients.LocationUpdate {

    private GoogleClient mClient;
    private Location mLastLocation;
    private IBinder mBinder;
    private BroadcastBase mBroadcastLocation;
    private BroadcastBase mBroadcastNotification;
    private BroadcastBase mBroadcastGps;

    private LocationThreshold mLocationThreshold;
    private LocationCounter mLocationCounter;
    private LocationNotification mLocationNotification;
    private Geocoder mGeocoder;
    private float mSmallestDisplacement;

    private BroadcastReceiver mGpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Check whether location has been turned on or off
        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            // Connect back to google client when gps is turned back on
            mClient.connect();
            mBroadcastGps.send(new LocationProvider(true));
        }
        else {

            // Disconnect from google client if gps is turned off
            mClient.disconnect();
            mBroadcastGps.send(new LocationProvider(false));
        }
        }
    };

    @Override
    public void onCreate() {

        mBinder = new ServiceBinder();
        mClient = new GoogleClient(this, this);
        mBroadcastLocation = new BroadcastLocation(LocalBroadcastManager.getInstance(this));
        mBroadcastNotification = new BroadcastNotification(LocalBroadcastManager.getInstance(this));
        mBroadcastGps = new BroadcastGps(LocalBroadcastManager.getInstance(this));
        mLocationNotification = new LocationNotification(
            this,
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)
        );

        mGeocoder = new Geocoder(this, Locale.getDefault());
        mSmallestDisplacement = Float.parseFloat(getString(R.string.smallest_displacement));
        mLocationThreshold = new LocationThreshold();
        mLocationCounter = new LocationCounter();

        registerReceiver(mGpsReceiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));

        TimeLimit timeLimit = getTimeLimit();
        if(timeLimit != null){

            trackTime(timeLimit.getTime(), timeLimit.getCreatedAt());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // By returning START_STICKY we explicitly tell the Android os that the service should be
        // restarted when the last client unbinds from it.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        PositionTrackerDataSource source = new PositionTrackerDataSource(this);
        mLastLocation = source.readLastLocation();

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {

        unregisterReceiver(mGpsReceiver);

        super.onDestroy();
    }

    public void startTracking() {

        if(!mClient.isConnected()) {

            mClient.connect();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        // Disconnect from google client when the user has killed the app by swiping it to
        // the left
        stopTracking();

        // Kill the service when the user has killed the app by swiping it to the left
        stopSelf();
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

    public boolean isConnected() {

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onNewLocation(Location location) {

        float distance = 0;
        boolean hasLastLocation = false;
        PositionTrackerDataSource source = new PositionTrackerDataSource(this);

        if(mLastLocation != null) {

            distance = mLastLocation.distanceTo(location);
            hasLastLocation = source.readLocation(mLastLocation);
        }

        // Check if the new location is considered a new location based on the distance between
        // last location and new location
        if(distance >= mSmallestDisplacement || mLastLocation == null || !hasLastLocation) {

            UserLocation newLocation = createUserLocation(location);
            source.insertUserLocation(newLocation);

            LocationAddress locationAddress = null;
            try {
                locationAddress = createLocationAddress(location);
                source.insertLocationAddress(location.getLatitude(), location.getLongitude(), locationAddress);
            }
            catch (IOException e) {

                e.printStackTrace();
            }

            // Save the new location
            mLastLocation = location;

            // For every new location, reset the counter as the user has clearly started moving
            mLocationCounter.reset();
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

        // Notify the activity, if any is listening, about a location change
        mBroadcastLocation.send(mLastLocation);

        if(source.readLastLocation() == null) {

            source.insertLastLocation(
                PositionTrackerSQLiteHelper.LAST_LOCATION_ID_VALUE,
                mLastLocation);
        }
        else if(!source.containsLocation(mLastLocation)) {

            source.updateLastLocation(
                PositionTrackerSQLiteHelper.LAST_LOCATION_ID_VALUE,
                mLastLocation);
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

    private LocationAddress createLocationAddress(Location location) throws IOException {

        Address geoAddress = getGeoAddress(location);

        return new LocationAddress(
            geoAddress.getAddressLine(0).split(",")[0],
            geoAddress.getAdminArea()
        );
    }

    private Address getGeoAddress(Location location) throws IOException{

        List<Address> geoAddress = mGeocoder.getFromLocation(
            location.getLatitude(),
            location.getLongitude(),
            1
        );

        return geoAddress.size() > 0 ? geoAddress.get(0) : new NullAddress(Locale.ENGLISH);
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

    private int getCurrentHour(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    private int getCurrentMinute(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.MINUTE);
    }

    private UserLocation createUserLocation(Location location) {

        Date date = new Date();
        return new UserLocation(
            new LatLng(location.getLatitude(), location.getLongitude()),
            getCurrentDateInMilliseconds(date),
            getCurrentHour(date),
            getCurrentMinute(date)
        );
    }

}
