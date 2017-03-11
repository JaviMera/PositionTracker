package com.javier.positiontracker.broadcastreceivers;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by javie on 3/8/2017.
 */

public class BroadcastLocation extends BroadcastBase{

    public static final String ACTION = BroadcastLocation.class.getSimpleName() + ".NEW_LOCATION";
    public static final String KEY = "new_location";

    public BroadcastLocation(LocalBroadcastManager manager) {

        super(manager);
    }

    @Override
    public void send(Parcelable value) {

        // Create intent with new location information
        Intent intent = new Intent(ACTION);
        intent.putExtra(KEY, value);

        // Broadcast the intent to main activity
        mManager.sendBroadcast(intent);
    }
}
