package com.javier.positiontracker.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.javier.positiontracker.ui.TrackerActivity;
import com.javier.positiontracker.R;

/**
 * Created by javie on 3/11/2017.
 */

public class DialogViewNotification extends DialogFragment {

    private static final String TIME_KEY = "time";
    private static final String CREATED_AT_KEY = "created_at";

    private OnViewNotification mListener;

    public interface OnViewNotification {

        void onNotificationDelete();
    }

    public static DialogViewNotification newInstance(long time, long createdAt) {

        DialogViewNotification dialogViewNotification = new DialogViewNotification();
        Bundle bundle = new Bundle();
        bundle.putLong(TIME_KEY, time);
        bundle.putLong(CREATED_AT_KEY, createdAt);
        dialogViewNotification.setArguments(bundle);

        return dialogViewNotification;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = (TrackerActivity)context;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        long time = getArguments().getLong(TIME_KEY);
        long createdAt = getArguments().getLong(CREATED_AT_KEY);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        dialogBuilder
            .setTitle(R.string.view_notification_dialog_title)
            .setMessage(String.format(
                getString(
                    R.string.view_notification_dialog_message),
                    time,
                    format.format(createdAt)
                    ))
            .setCancelable(true)
            .setPositiveButton(R.string.view_notification_dialog_ok_text, null)
            .setNegativeButton(R.string.view_notification_dialog_cancel_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                mListener.onNotificationDelete();
                }
            });

        return dialogBuilder.create();
    }
}
