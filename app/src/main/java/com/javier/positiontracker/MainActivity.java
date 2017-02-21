package com.javier.positiontracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.model.UserLocation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.os.Handler;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback {

    public static final int FINE_LOCATION_CODE = 100;

    private GoogleApiClient mClient;
    private Location mLastLocation;
    private GoogleMap mMap;
    private ArrayList<Marker> mMarkers;

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
                Date date1 = getDate(2017, Calendar.FEBRUARY, 19, 0, 0);
                Date date2 = getDate(2017, Calendar.FEBRUARY, 20, 23, 59);

                showLocations(date1, date2);
                if(!mMarkers.isEmpty()) {

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mMarkers.get(0).getPosition()));
                }

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
        mMarkers = new ArrayList<>();

        ButterKnife.bind(this);

        setSupportActionBar(mBar);

        SupportMapFragment fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        mClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_CODE);
            return;
        }

        mClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mClient.isConnected()) {

            LocationServices.FusedLocationApi.removeLocationUpdates(mClient, this);
            mClient.disconnect();
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

                    mClient.connect();
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest request = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            .setInterval(10000L)
            .setFastestInterval(1000L);

        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

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

    public void showLocations(Date minDate, Date maxDate) {

        PositionTrackerDataSource source = new PositionTrackerDataSource(this);
        final List<UserLocation> locations = source.readLocationsWithRange(
                minDate.getTime(),
                maxDate.getTime()
        );

        for (UserLocation location : locations) {

            MarkerOptions options = new MarkerOptions();
            options.position(location.getLatLong());
            options.title(location.toString());

            Marker marker = mMap.addMarker(options);
            mMarkers.add(marker);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
    }

    private Date getDate(int year, int month, int day, int hour, int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE,minute);

        return calendar.getTime();
    }
}
