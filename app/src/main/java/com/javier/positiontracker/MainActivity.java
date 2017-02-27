package com.javier.positiontracker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.dialogs.DateRangeListener;
import com.javier.positiontracker.dialogs.DialogDateRange;
import com.javier.positiontracker.model.UserLocation;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        DateRangeListener{

    public static final int FINE_LOCATION_CODE = 100;

    private GoogleMap mMap;
    private Map<UserLocation, Marker> mMarkers;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        if(intent.getStringExtra(TrackerService.CONNECTION_STATUS).equals("CONNECTED")) {

            Snackbar
                .make(mRootLayout, "CONNECTED", Snackbar.LENGTH_SHORT)
                .show();
        }
        }
    };

    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        if(intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {

        }
        }
    };

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mRootLayout;

    @BindView(R.id.toolbar)
    Toolbar mBar;

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.action_date_range:

                DialogDateRange dialog = new DialogDateRange();
                dialog.show(getSupportFragmentManager(), "dialog_date_range");
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

        ButterKnife.bind(this);

        setSupportActionBar(mBar);

        SupportMapFragment fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(TrackerService.TAG);

        // Register a broadcast receiver instance in order to receive messages from our service
        registerReceiver(mMessageReceiver, filter);

        // Listen to changes from GPS provider, when the gps is turned on or offd
        registerReceiver(gpsReceiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_CODE);
        }

        Intent intent = new Intent(MainActivity.this, TrackerService.class);
        startService(intent);
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
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mMessageReceiver);
    }

    public void showLocations(Date minDate, Date maxDate) {

        PositionTrackerDataSource source = new PositionTrackerDataSource(this);
        final List<UserLocation> locations = source.readLocationsWithRange(
            minDate.getTime(),
            maxDate.getTime()
        );

        // Loop through the current locations to see which ones do not match the current filter
        List<UserLocation> locationsToRemove = new ArrayList<>();

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
            options.position(location.getLatLong());
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
}
