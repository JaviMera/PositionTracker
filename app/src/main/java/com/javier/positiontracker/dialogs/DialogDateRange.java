package com.javier.positiontracker.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.javier.positiontracker.ui.TrackerActivity;
import com.javier.positiontracker.R;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by javie on 2/21/2017.
 */

public class DialogDateRange extends DialogFragment {

    public static final String TAG = DialogDateRange.class.getSimpleName();

    private TrackerActivity mParent;

    @BindView(R.id.startDateView)
    DatePicker mStartDate;

    @BindView(R.id.endDateView)
    DatePicker mEndDate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mParent = (TrackerActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.date_range, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.setDateRangeButton)
    public void onSetClick(View view) {

        Date startDate = getDate(mStartDate, 0,0,0);
        Date endDate = getDate(mEndDate, 23,59,59);

        // Send the selected dates back to the parent activity
        mParent.onDateRangeSelected(startDate, endDate);

        // Close the dialog when taping on set
        dismiss();
    }

    private Date getDate(DatePicker picker, int hour, int minute, int second) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, picker.getYear());
        calendar.set(Calendar.MONTH, picker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, picker.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        return calendar.getTime();
    }
}