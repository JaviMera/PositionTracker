package com.javier.positiontracker.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.javier.positiontracker.R;
import com.javier.positiontracker.TrackerService;
import com.javier.positiontracker.broadcastreceivers.BroadcastGps;
import com.javier.positiontracker.broadcastreceivers.BroadcastLocation;
import com.javier.positiontracker.broadcastreceivers.BroadcastNotification;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.dialogs.DateRangeListener;
import com.javier.positiontracker.dialogs.DialogDateRange;
import com.javier.positiontracker.dialogs.DialogLocationProvider;
import com.javier.positiontracker.dialogs.DialogNotification;
import com.javier.positiontracker.dialogs.DialogViewNotification;
import com.javier.positiontracker.model.CameraLevel;
import com.javier.positiontracker.model.LocationProvider;
import com.javier.positiontracker.model.TimeLimit;
import com.javier.positiontracker.model.UserLocation;
import com.javier.positiontracker.model.ZoomValues;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrackerActivity extends AppCompatActivity
    implements
    TrackerActivityView,
    OnMapReadyCallback,
    DateRangeListener,
    DialogNotification.OnNotificationCallback,
    DialogViewNotification.OnViewNotification,
    DialogLocationProvider.OnProviderListener {

    public static final int FINE_LOCATION_CODE = 100;
    public static final int EXTERNAL_STORAGE_CODE = 1000;
    private static final int LOCATION_PROVIDER_CODE = 1100;

    private Menu mMenu;
    private String mTimeLimitKey;
    private GoogleMap mMap;
    private List<Marker> mMarkers;
    private TrackerService mService;
    private boolean mBound;
    private boolean mNotificationActive;
    private boolean mDisplayHomeEnabled;
    private boolean mGpsEnabled;
    private TrackerActivityPresenter mPresenter;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            TrackerService.ServiceBinder binder = (TrackerService.ServiceBinder) iBinder;
            mService = binder.getService();

            if(mService.isConnected()) {

                mService.startTracking();
                mGpsEnabled = true;
            }
            else {

                mLocationFab.performClick();
                mGpsEnabled = false;
            }

            TimeLimit timeLimit = mService.getTimeLimit();

            // After successfully connecting with the service, check if there was a time limit set
            if(null != timeLimit && timeLimit.getTime() != 0) {

                // If a time limit exists, then change the state of the activity to show the
                // notification that was previously set by the user
                mPresenter.setNotificationActive(true);

                // Re-draw the menu panel
                invalidateOptionsMenu();
            }

            mPresenter.setBoundToService(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            mPresenter.setBoundToService(false);
        }
    };

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mRootLayout;

    @BindView(R.id.toolbar)
    Toolbar mBar;

    @BindView(R.id.locationFab)
    FloatingActionButton mLocationFab;

    private Marker mCurrentMarker;
    private Location mCurrentLocation;

    private BroadcastReceiver mNewLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // In case the user is viewing all positions within a date range, then don't bother
            // in showing the current position on the map
            if(mMarkers.size() > 0) {

                mPresenter.setMarkerVisible(false);
                return;
            }
            else {

                mPresenter.setMarkerVisible(true);
            }

            Location location = intent.getParcelableExtra(BroadcastLocation.KEY);

            // Check if the incoming intent contains a valid new location
            if(location == null) {

                return;
            }

            // Check if the same location is being sent
            if(mCurrentLocation != null && location.equals(mCurrentLocation))
                return;

            mCurrentLocation = location;
            LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            MarkerOptions options = new MarkerOptions();
            options.position(currentLatLng);
            options.title(getString(R.string.marker_current_location));

            // Remove current marker from the map before assigning the new one
            if(mCurrentMarker != null) {

                mCurrentMarker.setVisible(false);
                mCurrentMarker.remove();
            }

            // Create the new marker with the newest location
            mCurrentMarker = mMap.addMarker(options);
            mPresenter.moveMapCamera(currentLatLng);
            mPresenter.zoomMapCamera(ZoomValues.get(CameraLevel.Streets), 2000, null);
        }
    };

    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        // Set notification active back to false when the notification has been launched
        mPresenter.setNotificationActive(false);

        // Re-draw the menu panel
        invalidateOptionsMenu();
        }
    };

    private BroadcastReceiver mGpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        LocationProvider provider = intent.getParcelableExtra(BroadcastGps.KEY);

        if(provider.isEnabled()) {

            mPresenter.setFabVisible(true);
            mPresenter.setMarkerVisible(true);
            mLocationFab.performClick();
            mGpsEnabled = true;
        }
        else {

            mGpsEnabled = false;

            if(!mDisplayHomeEnabled) {

                mPresenter.setMarkerVisible(false);
                mPresenter.moveMapCamera(new LatLng(0,0));
                mPresenter.zoomMapCamera(ZoomValues.get(CameraLevel.World), 2000, null);
            }
        }

        // Re-draw the menu panel
        invalidateOptionsMenu();
        }
    };

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        mMenu = menu;

        if(mGpsEnabled) {

            menuWithGPSOn(menu);
        }
        else {

            menuWithGPSOff(menu);
        }

        return true;
    }

    private void menuWithGPSOn(Menu menu) {

        menu.findItem(R.id.action_export_data).setVisible(true);

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
    }

    private void menuWithGPSOff(Menu menu) {

        menu.findItem(R.id.action_notification_active).setVisible(false);
        menu.findItem(R.id.action_notification_none).setVisible(false);
        menu.findItem(R.id.action_export_data).setVisible(false);
    }

    @Override
    public void onBackPressed() {

        // Check if the upper right arrow icon is showing
        // This will indicate that the user is currently viewing past locations
        if(mDisplayHomeEnabled) {

            mPresenter.displayMenuIcons(true);

            // Re-draw the menu panel
            invalidateOptionsMenu();

            mPresenter.setTitle(getString(R.string.app_name));

            // Hide the upper right arrow icon
            mPresenter.setDisplayHome(false);

            // Show the appropriate layout
            onBackPress();
        }
        else {

            // IF user is not viewing past locations, it means he/she is just exiting the app
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        DialogFragment dialog;

        switch(item.getItemId()) {

            case android.R.id.home:

                onBackPressed();
                break;

            case R.id.action_date_range:

                dialog = new DialogDateRange();
                dialog.show(getSupportFragmentManager(), getString(R.string.dialog_date_range_tag));
                break;

            case R.id.action_notification_none:

                dialog = new DialogNotification();
                dialog.show(getSupportFragmentManager(), getString(R.string.dialog_notification_tag));
                break;

            case R.id.action_notification_active:

                TimeLimit timeLimit = mService.getTimeLimit();

                dialog = DialogViewNotification.newInstance(
                    timeLimit.getTime(),
                    timeLimit.getCreatedAt()
                );

                dialog.show(getSupportFragmentManager(), getString(R.string.dialog_view_notification_tag));
                break;

            case R.id.action_export_data:

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_CODE);

                    break;
                }

                exportLocation();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void exportLocation() {

        Intent intent = new Intent(TrackerActivity.this, LocationsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new TrackerActivityPresenter(this);
        mMarkers = new LinkedList<>();
        mTimeLimitKey = getString(R.string.time_limit_key);

        if(savedInstanceState != null && savedInstanceState.containsKey(mTimeLimitKey)) {

            mPresenter.setNotificationActive(true);

            // Re-draw the menu panel
            invalidateOptionsMenu();
        }

        ButterKnife.bind(this);

        setSupportActionBar(mBar);

        SupportMapFragment fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {

        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_CODE);
            return;
        }

        Intent intent = new Intent(this, TrackerService.class);

        // Call start service in case it was killed by the user
        startService(intent);

        // Bind to TrackerService to store the location of the device periodically
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        registerReceivers();
    }

    private void registerReceivers() {
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

        // Register a receiver to listen to gps events from the service
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                mGpsReceiver,
                new IntentFilter(BroadcastGps.ACTION)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister any broadcast receivers when the app is not in the foreground
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNewLocationReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGpsReceiver);
        if(mBound) {

            mPresenter.setBoundToService(false);
            unbindService(mConnection);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mService != null) {

            // Get the current time limit, if there is any
            TimeLimit timeLimit = mService.getTimeLimit();

            // Check if the user has set up a location based time limit
            // Zero means there hasn't been one set yet
            if(timeLimit != null && timeLimit.getTime() > 0) {

                outState.putLong(mTimeLimitKey, timeLimit.getTime());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {

            case LOCATION_PROVIDER_CODE:

                menuWithGPSOn(mMenu);
                mPresenter.setMarkerVisible(true);
                mLocationFab.performClick();
                break;
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

                    // Register the receivers here as well since onStart will not be called when
                    // permissions dialog is prompted
                    registerReceivers();

                    Intent intent = new Intent(TrackerActivity.this, TrackerService.class);

                    // The first time the application is ever ran, start the service as a normal service
                    // in order to invoke onStartCommand and return START_STICKY to allow the service
                    // to restart when the last client unbinds from it
                    startService(intent);

                    // Bind to the service once that is started.
                    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                }
                break;

            case EXTERNAL_STORAGE_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        return;
                    }

                    exportLocation();
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

            if(getSupportActionBar() != null) {

                mPresenter.moveMapCamera(marker.getPosition());
                mPresenter.setTitle(marker.getTitle());
            }

            return true;
            }
        });
    }

    @Override
    public void onDateRangeSelected(Date startDate, Date endDate) {

        List<UserLocation> locations = getLocations(startDate, endDate);

        if(locations.size() > 0) {

            clearMarkers(mMarkers);
            showLocations(locations);

            mPresenter.setDisplayHome(true);
            mPresenter.setFabVisible(false);
            mPresenter.setMarkerVisible(false);

            // Move the camera to the first marker in the array of markers
            mPresenter.moveMapCamera(mMarkers.get(0).getPosition());
            mPresenter.zoomMapCamera(ZoomValues.get(CameraLevel.Streets), 2000, null);

            mPresenter.displayMenuIcons(false);
        }
        else {

            Toast
                .makeText(
                    this,
                    "You weren't anywhere in those dates. Maybe you disappeared for a while...",
                    Toast.LENGTH_LONG)
                .show();
        }
    }

    @Override
    public void onSetNotification(long time, long createdAt) {

        // Notify the service about a time limit being set by the user
        mService.trackTime(time, createdAt);

        // Notify the activity about the notification so it can appropriately draw menu icons
        mPresenter.setNotificationActive(true);

        // Re-draw the menu panel
        invalidateOptionsMenu();

        // Notify the user about successfully creating a time limit
        mPresenter.showSnackbar(getString(R.string.snackbar_notification_created));
    }

    @OnClick(R.id.locationFab)
    public void onLocationFabClick(View view) {

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            // Before moving and zooming in the current marker, check current marker is a valid marker
            if(mCurrentMarker != null) {

                mPresenter.moveMapCamera(mCurrentMarker.getPosition());
                mPresenter.zoomMapCamera(ZoomValues.get(CameraLevel.Streets), 2000, null);
            }
        }
        else {

            DialogFragment dialog = DialogLocationProvider.newInstance();
            dialog.show(getSupportFragmentManager(), "dialog_provider");
        }
    }

    @Override
    public void onNotificationDelete() {

        long affectedRow = mService.removeTimeLimit();

        if(affectedRow > -1) {

            // By passing 0 for time and created at, the service will identify it as an invalid
            // time limit, thus it will stop tracking time
            mService.trackTime(0, 0);

            // Notify the activity that the notification has been deleted
            mPresenter.setNotificationActive(false);

            // Re-draw the menu panel
            invalidateOptionsMenu();

            // Notify the user through a snackbar message about successfully deleting a notification
            mPresenter.showSnackbar(getString(R.string.snackbar_notification_deleted));
        }
        else {

            Toast.makeText(this, "Can't remove time limit at this moment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showSnackbar(String message) {

        Snackbar
            .make(
                mRootLayout,
                message,
                Snackbar.LENGTH_SHORT)
            .show();
    }

    @Override
    public void setBoundToService(boolean bound) {

        mBound = bound;
    }

    @Override
    public void setNotificationActive(boolean active) {

        mNotificationActive = active;
    }

    @Override
    public void displayMenuIcons(boolean display) {

        if(mNotificationActive) {

            mMenu.findItem(R.id.action_notification_active).setVisible(display);
        }
        else {

            mMenu.findItem(R.id.action_notification_none).setVisible(display);
        }

        mMenu.findItem(R.id.action_export_data).setVisible(display);
        mMenu.findItem(R.id.action_date_range).setVisible(display);
    }

    @Override
    public void moveMapCamera(LatLng latLng) {

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void zoomMapCamera(float zoomLvl, int animDuration, GoogleMap.CancelableCallback callback) {

        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLvl), animDuration, callback);
    }

    @Override
    public void setFabVisible(boolean visible) {

       if(visible) {

           mLocationFab.show();
       }
       else {

           mLocationFab.hide();
       }
    }

    @Override
    public void setDisplayHome(boolean visible) {

        if(getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(visible);
            mDisplayHomeEnabled = visible;
        }
    }

    @Override
    public void setMarkerVisible(boolean visible) {

        if(mCurrentMarker != null) {

            mCurrentMarker.setVisible(visible);
        }
    }

    @Override
    public void setTitle(String title) {

        if(getSupportActionBar() != null) {

            getSupportActionBar().setTitle(title);
        }
    }

    public void showLocations(List<UserLocation> locations) {

        for (UserLocation location : locations) {

            MarkerOptions options = getMarkerOptions(location);
            Marker marker = mMap.addMarker(options);
            mMarkers.add(marker);
        }
    }

    private List<UserLocation> getLocations(Date minDate, Date maxDate) {

        PositionTrackerDataSource source = new PositionTrackerDataSource(this);
        return source.readLocationsWithRange(
            minDate.getTime(),
            maxDate.getTime()
        );
    }

    private MarkerOptions getMarkerOptions(UserLocation location) {

        PositionTrackerDataSource source = new PositionTrackerDataSource(this);

        MarkerOptions options = new MarkerOptions();
        options.position(location.getPosition());
        options.title(source.readLocationAddress
            (
                location.getPosition().latitude,
                location.getPosition().longitude
            )
            .getFullAddress()
        );

        return options;
    }

    private void clearMarkers(List<Marker> markers) {

        for(Marker marker : markers) {

            marker.remove();
        }

        // Clear out all markers
        markers.clear();
    }

    @Override
    public void onActivateProvider() {

        Intent callGPSSettingIntent = new Intent(
            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        startActivityForResult(callGPSSettingIntent, LOCATION_PROVIDER_CODE);
    }

    private void onBackPress() {

        clearMarkers(mMarkers);
        mPresenter.setFabVisible(true);

        if(mGpsEnabled) {

            mPresenter.setMarkerVisible(true);
            mPresenter.moveMapCamera(mCurrentMarker.getPosition());
            mPresenter.zoomMapCamera(ZoomValues.get(CameraLevel.Streets), 2000, null);
        }
        else {

            mPresenter.setMarkerVisible(false);
            mPresenter.moveMapCamera(new LatLng(0,0));
            mPresenter.zoomMapCamera(ZoomValues.get(CameraLevel.World), 2000, null);
        }
    }
}
