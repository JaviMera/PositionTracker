package com.javier.positiontracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.model.UserLocation;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LatLng latLng = new LatLng(20.00, -36.200);
        long date = new Date().getTime();
        UserLocation location = new UserLocation(latLng, date);

        PositionTrackerDataSource source = new PositionTrackerDataSource(this);
        source.create(location);
    }
}
