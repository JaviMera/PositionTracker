package com.javier.positiontracker.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.javier.positiontracker.ui.TrackerActivity;

/**
 * Created by javie on 3/20/2017.
 */

public class DialogLocationProvider extends DialogFragment {

    public static DialogFragment newInstance() {

        return new DialogLocationProvider();
    }

    public interface OnProviderListener {

        void onActivateProvider();
    }

    private OnProviderListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = (TrackerActivity) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            getContext());

        alertDialogBuilder
            .setMessage("GPS is disabled in your device. Enable it?")
            .setCancelable(false)
            .setPositiveButton("Enable GPS",positiveButtonListener())
            .setNegativeButton("Cancel", negativeButtonListener());

        return alertDialogBuilder.create();
    }

    private DialogInterface.OnClickListener positiveButtonListener() {

        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onActivateProvider();
                dismiss();
            }
        };
    }

    private DialogInterface.OnClickListener negativeButtonListener() {

        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        };
    }
}
