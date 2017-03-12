package com.javier.positiontracker.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.javier.positiontracker.ui.TrackerActivity;
import com.javier.positiontracker.R;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by javie on 3/6/2017.
 */
public class DialogNotification extends DialogFragment{

    private OnNotificationCallback mListener;

    public interface OnNotificationCallback {

        void onSetNotification(long time, long createdAt);
    }

    @BindView(R.id.timeSeekBar)
    SeekBar mSeekBar;

    @BindView(R.id.timeTextView)
    TextView mTimeTextView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = (TrackerActivity)context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.time_notification, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        ButterKnife.bind(this, view);

        mTimeTextView.setText("0");

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                mTimeTextView.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        alertDialog.setView(view);

        return alertDialog.create();
    }

    @OnClick(R.id.setNotification)
    public void onSetNotificationClick(View view) {

        int minutes = mSeekBar.getProgress();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // Send the minutes selected to Main Activity
        mListener.onSetNotification(
            minutes,
            calendar.getTimeInMillis());

        // Dismiss dialog when the user taps on OK
        dismiss();
    }
}
