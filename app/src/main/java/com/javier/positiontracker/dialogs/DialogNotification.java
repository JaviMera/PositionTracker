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

import com.javier.positiontracker.MainActivity;
import com.javier.positiontracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by javie on 3/6/2017.
 */
public class DialogNotification extends DialogFragment{

    private OnNotificationCallback mListener;

    public interface OnNotificationCallback {

        void onSetNotification(int time);
    }

    @BindView(R.id.timeSeekBar)
    SeekBar mSeekBar;

    @BindView(R.id.timeTextView)
    TextView mTimeTextView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = (MainActivity)context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.time_notification, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        ButterKnife.bind(this, view);

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


    }
}
