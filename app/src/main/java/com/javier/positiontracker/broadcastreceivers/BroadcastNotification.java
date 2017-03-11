package com.javier.positiontracker.broadcastreceivers;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by javie on 3/11/2017.
 */

public class BroadcastNotification extends BroadcastBase {

    public static final String ACTION = BroadcastNotification.class.getSimpleName();

    public BroadcastNotification(LocalBroadcastManager manager) {
        super(manager);
    }

    @Override
    public void send(Parcelable value) {

        // Create intent with new location information
        Intent intent = new Intent(ACTION);

        // Broadcast the intent to main activity
        mManager.sendBroadcast(intent);
    }
}
