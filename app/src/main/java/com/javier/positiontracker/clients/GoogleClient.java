package com.javier.positiontracker.clients;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.javier.positiontracker.TrackerService;

/**
 * Created by javie on 2/22/2017.
 */

public class GoogleClient implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    private final long mInterval = 10000L;

    @Override
    public void onLocationChanged(Location location) {

        mParent.onNewLocation(location);
    }

    private TrackerService mParent;
    private GoogleApiClient mGoogleClient;
    private LocationRequest mRequest;

    public GoogleClient(Context context, LocationUpdate listener) {

        mGoogleClient = new GoogleApiClient.Builder(context)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

        mRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(mInterval)
            .setFastestInterval(1000L);

        mParent = (TrackerService) listener;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(mGoogleClient.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(mGoogleClient.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleClient, mRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void connect() {

        mGoogleClient.connect();
    }

    public void disconnect() {

        if(mGoogleClient.isConnected()) {

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleClient, this);
            mGoogleClient.disconnect();
        }
    }

    public long getTimeInterval() {

        return mInterval;
    }

    public boolean isConnected() {

        return mGoogleClient.isConnected();
    }
}
