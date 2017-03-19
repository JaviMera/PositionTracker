package com.javier.positiontracker.broadcastreceivers;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by javie on 3/19/2017.
 */

public class BroadcastGps extends BroadcastBase {

    public static final String ACTION = BroadcastLocation.class.getSimpleName() + ".LOCATION_PROVIDER";
    public static final String KEY = "gps_enabled";

    public BroadcastGps(LocalBroadcastManager manager) {
        super(manager);
    }

    @Override
    public void send(Parcelable value) {

        Intent intent = new Intent(ACTION);
        intent.putExtra(KEY, value);

        mManager.sendBroadcast(intent);
    }
}
