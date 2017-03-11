package com.javier.positiontracker.broadcastreceivers;

import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by javie on 3/11/2017.
 */

public abstract class BroadcastBase {

    protected LocalBroadcastManager mManager;

    public BroadcastBase(LocalBroadcastManager manager) {

        mManager = manager;
    }

    public abstract void send(Parcelable value);
}
