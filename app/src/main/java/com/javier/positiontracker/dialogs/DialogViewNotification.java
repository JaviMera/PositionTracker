package com.javier.positiontracker.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.javier.positiontracker.MainActivity;
import com.javier.positiontracker.R;

/**
 * Created by javie on 3/11/2017.
 */

public class DialogViewNotification extends DialogFragment {

    public static final String KEY = "time";
    private OnViewNotification mListener;

    public interface OnViewNotification {

        void onNotificationDelete();
    }

    public static DialogViewNotification newInstance(int time) {

        DialogViewNotification dialogViewNotification = new DialogViewNotification();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY, time);
        dialogViewNotification.setArguments(bundle);

        return dialogViewNotification;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = (MainActivity)context;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int time = getArguments().getInt(KEY);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        dialogBuilder
            .setTitle(R.string.view_notification_dialog_title)
            .setMessage(String.format(getString(R.string.view_notification_dialog_message), time))
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
