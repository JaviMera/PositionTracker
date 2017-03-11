package com.javier.positiontracker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.javier.positiontracker.broadcastreceivers.BroadcastLocation;
import com.javier.positiontracker.broadcastreceivers.BroadcastNotification;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.dialogs.DateRangeListener;
import com.javier.positiontracker.dialogs.DialogDateRange;
import com.javier.positiontracker.dialogs.DialogNotification;
import com.javier.positiontracker.dialogs.DialogViewNotification;
import com.javier.positiontracker.model.UserLocation;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
    implements
    OnMapReadyCallback,
    DateRangeListener,
    DialogNotification.OnNotificationCallback, DialogViewNotification.OnViewNotification {

    public static final int FINE_LOCATION_CODE = 100;

    private final static float ZOOM_LEVEL_STREET = 15.0f;

    private GoogleMap mMap;
    private Map<UserLocation, Marker> mMarkers;
    private TrackerService mService;
    private boolean mBound;
    private boolean mNotificationActive;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TrackerService.ServiceBinder binder = (TrackerService.ServiceBinder) iBinder;
            mService = binder.getService();
            mService.startTracking();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            mBound = false;
        }
    };

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mRootLayout;

    @BindView(R.id.toolbar)
    Toolbar mBar;

    @BindView(R.id.locationFab)
    FloatingActionButton mLocationFab;

    private Marker mCurrentMarker;

    private BroadcastReceiver mNewLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            UserLocation location = intent.getParcelableExtra(BroadcastLocation.KEY);

            // Check if the incoming intent contains a valid new location
            if(location != null) {

                // Check if a marker is showing on the map
                if(mCurrentMarker != null) {

                    mCurrentMarker.setVisible(false);
                    mCurrentMarker.remove();
                }

                MarkerOptions options = new MarkerOptions();
                options.position(location.getPosition());
                options.title("Current Location");

                mCurrentMarker = mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentMarker.getPosition()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL_STREET), 2000, null);
            }
        }
    };

    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Set notification active back to false when the notification has been launched
            mNotificationActive = false;

            // Re-draw the menu icons when the notification has been launched
            invalidateOptionsMenu();
        }
    };
    private int mTimeLimit;

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);

        if(mNotificationActive) {

            // Display the notification active icon when the user sets a time limit notification
            // on the same location
            menu.findItem(R.id.action_notification_active).setVisible(true);
            menu.findItem(R.id.action_notification_none).setVisible(false);
        }
        else {

            // Display the notification none icon if the notification was successfully launched
            // indicating that a new time limit notification can be set again
            menu.findItem(R.id.action_notification_active).setVisible(false);
            menu.findItem(R.id.action_notification_none).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        DialogFragment dialog;

        switch(item.getItemId()) {

            case R.id.action_date_range:

                dialog = new DialogDateRange();
                dialog.show(getSupportFragmentManager(), "dialog_date_range");
                break;

            case R.id.action_notification_none:

                dialog = new DialogNotification();
                dialog.show(getSupportFragmentManager(), "dialog_notification");
                break;

            case R.id.action_notification_active:

                dialog = DialogViewNotification.newInstance(mTimeLimit);
                dialog.show(getSupportFragmentManager(), "dialog_view_notification");
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMarkers = new LinkedHashMap<>();

        if(savedInstanceState != null && savedInstanceState.containsKey("time")) {

            mTimeLimit = savedInstanceState.getInt("time");
            mNotificationActive = true;
            invalidateOptionsMenu();
        }
        else {

            SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName() + ".PREFERENCES_KEY", MODE_PRIVATE);
            if(prefs.contains("time")) {

                mTimeLimit = prefs.getInt("time", 0);
                mNotificationActive = mTimeLimit != 0;
                invalidateOptionsMenu();
            }
        }

        ButterKnife.bind(this);

        setSupportActionBar(mBar);

        SupportMapFragment fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_CODE);
        }
    }

    @Override
    protected void onStart() {

        super.onStart();

        Intent intent = new Intent(this, TrackerService.class);

        // Bind to TrackerService to store the location of the device periodically
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        // Register a receiver to listen to location updates from the service
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                mNewLocationReceiver,
                new IntentFilter(BroadcastLocation.ACTION)
            );

        // Register a receiver to listen to notifications from the service
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                mNotificationReceiver,
                new IntentFilter(BroadcastNotification.ACTION)
            );
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNewLocationReceiver);

        if(mBound) {

            mBound = false;
            unbindService(mConnection);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(isFinishing()) {

            SharedPreferences.Editor editor =
                getSharedPreferences(
                    MainActivity.class.getSimpleName() + ".PREFERENCES_KEY",
                    MODE_PRIVATE
                )
                .edit();

            editor.putInt("time", mTimeLimit);
            editor.apply();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Check if the user has set up a location based time limit
        // Zero means there hasn't been one set yet
        if(mTimeLimit > 0) {

            outState.putInt("time", mTimeLimit);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case FINE_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    Intent intent = new Intent(MainActivity.this, TrackerService.class);
                    startService(intent);
                    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                }
                break;
        }
    }

    public void showLocations(Date minDate, Date maxDate) {

        PositionTrackerDataSource source = new PositionTrackerDataSource(this);
        final List<UserLocation> locations = source.readLocationsWithRange(
            minDate.getTime(),
            maxDate.getTime()
        );

        List<UserLocation> locationsToRemove = new ArrayList<>();

        // Loop through the current locations to see which ones do not match the current filter
        for(Map.Entry<UserLocation, Marker> entry : mMarkers.entrySet()) {

            if(!locations.contains(entry.getKey())) {

                locationsToRemove.add(entry.getKey());
            }
        }

        for(UserLocation location : locationsToRemove) {

            Marker marker = mMarkers.get(location);
            marker.remove();
            mMarkers.remove(location);
        }

        // Loop through the current locations to see which ones match the current filter
        for (UserLocation location : locations) {

            MarkerOptions options = new MarkerOptions();
            options.position(location.getPosition());
            options.title(location.toString());

            Marker marker = mMap.addMarker(options);
            mMarkers.put(location, marker);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
    }

    @Override
    public void onDateRangeSelected(Date startDate, Date endDate) {

        showLocations(startDate, endDate);

        if(!mMarkers.isEmpty()) {

            Marker firstMarker = mMarkers.entrySet()
                .iterator()
                .next()
                .getValue();

            mMap.moveCamera(CameraUpdateFactory.newLatLng(firstMarker.getPosition()));
        }
    }

    @Override
    public void onSetNotification(int time) {

        mTimeLimit = time;
        mService.trackTime(mTimeLimit);

        mNotificationActive = true;
        invalidateOptionsMenu();

        Snackbar
            .make(
                mRootLayout,
                "Notification Created!",
                Snackbar.LENGTH_SHORT)
            .show();
    }

    @OnClick(R.id.locationFab)
    public void onLocationFabClick(View view) {

        // Before moving and zooming in the current marker, check current marker is a valid marker
        if(mCurrentMarker != null) {

            // Center the camera to the current marker position, which is the current device's
            // location in the map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentMarker.getPosition()));

            // Zoom in the current marker with stree level, an animation delay of 2 seconds, and
            // without registering a cancel callback
            mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL_STREET), 2000, null);
        }
    }

    @Override
    public void onNotificationDelete() {

        mTimeLimit = 0;
        mNotificationActive = false;
        mService.trackTime(mTimeLimit);
        invalidateOptionsMenu();
    }
}
