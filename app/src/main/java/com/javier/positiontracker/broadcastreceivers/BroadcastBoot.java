package com.javier.positiontracker.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.javier.positiontracker.TrackerService;

/**
 * Created by javie on 3/9/2017.
 */

public class BroadcastBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Intent serviceIntent = new Intent(context, TrackerService.class);
            context.startService(serviceIntent);
        }
    }
}
