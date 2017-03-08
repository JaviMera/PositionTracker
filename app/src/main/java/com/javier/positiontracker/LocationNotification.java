package com.javier.positiontracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

/**
 * Created by javie on 3/8/2017.
 */

public class LocationNotification {

    private final Context mContext;
    private NotificationManager mManager;

    public static final int NOTIFICATION_REQUEST_CODE = 11;

    public LocationNotification(Context context, NotificationManager manager) {

        mContext = context;
        mManager = manager;
    }

    public void send(String title, String content, int iconId) {

        Notification notification = createNotification(title, content, iconId);
        mManager.notify(NOTIFICATION_REQUEST_CODE, notification);
    }

    private Notification createNotification(String title, String content, int iconId) {

        // Begin creating a notification
        Notification.Builder notificationBuilder = new Notification.Builder(mContext);

        // Set the icon that will be displayed on the left of the notification
        notificationBuilder.setSmallIcon(iconId);

        // Set the title of the message
        notificationBuilder.setContentTitle(title);

        // Set the body of the message
        notificationBuilder.setContentText(content);

        // Set the notification to vibrate
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

        // PRIORITY_HIGH will make notification show as a heads-up Notification
        // instead of just displaying the icon at the top of the device.
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);

        return notificationBuilder.build();
    }
}
