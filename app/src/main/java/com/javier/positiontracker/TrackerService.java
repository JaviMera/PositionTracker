package com.javier.positiontracker;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
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
    public static final int NOTIFICATION_REQUEST_CODE = 11;

    private GoogleClient mClient;
    private UserLocation mLastLocation;
    private IBinder mBinder;
    private LocalBroadcastManager mBroadcast;
    private long mTimeNotification;
    private long mTimeCounter;
    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {

        super.onCreate();
        mBinder = new ServiceBinder();
        mClient = new GoogleClient(this, this);
        mBroadcast = LocalBroadcastManager.getInstance(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        setTimeNotification(0L);
        setTimeCounter(0L);
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

            // Reset bot time notificaion and counter if the location changes
            // since we wan't to start tracking time again for a new location
            setTimeNotification(0L);
            setTimeCounter(0L);

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

            if(mTimeNotification > 0) {

                if(mTimeCounter >= mTimeNotification) {

                    Notification notification = createNotification();
                    mNotificationManager.notify(NOTIFICATION_REQUEST_CODE, notification);

                    setTimeNotification(0L);
                    setTimeCounter(0L);
                }
                else {

                    mTimeCounter += mClient.getTimeInterval();
                }
            }
        }
    }

    private Notification createNotification() {

        // Begin creating a notification
        Notification.Builder notificationBuilder = new Notification.Builder(this);

        // Set the icon that will be displayed on the left of the notification
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

        // Set the title of the message
        notificationBuilder.setContentTitle("Position Tracker");

        // Set the body of the message
        notificationBuilder.setContentText(
            "It's been " +
            mTimeNotification / 1000 / 60 +
            " minute(s).");

        // Set the notification to vibrate
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

        // PRIORITY_HIGH will make notification show as a heads-up Notification
        // instead of just displaying the icon at the top of the device.
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);

        return notificationBuilder.build();
    }

    public void trackTime(int time) {

        // Store the time notifications in milliseconds
        setTimeNotification(time * 60 * 1000);

        // Set the time counter to begin counting the seconds at the current location
        setTimeCounter(0L);
    }

    public class ServiceBinder extends Binder {

        TrackerService getService() {

            return TrackerService.this;
        }
    }

    private void setTimeNotification(long time) {

        mTimeNotification = time;
    }

    private void setTimeCounter(long time) {

        mTimeCounter = time;
    }
}
