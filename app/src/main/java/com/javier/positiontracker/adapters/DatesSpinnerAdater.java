package com.javier.positiontracker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.javier.positiontracker.R;

import java.util.List;
import java.text.SimpleDateFormat;

/**
 * Created by javie on 3/14/2017.
 */

public class DatesSpinnerAdater extends ArrayAdapter<Long> {

    private LayoutInflater mInflater;
    private List<Long> mDates;

    public DatesSpinnerAdater(@NonNull Context context, List<Long> dates) {
        super(context, 0);

        mInflater = LayoutInflater.from(context);
        mDates = dates;
    }

    @Nullable
    @Override
    public Long getItem(int position) {

        return mDates.get(position);
    }

    @Override
    public int getPosition(@Nullable Long item) {

        return mDates.indexOf(item);
    }

    @Override
    public int getCount() {

        return mDates.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SpinnerViewHolder holder;
        View view = convertView;

        if(null == view) {

            view = mInflater.inflate(R.layout.date_item, null);
            holder = new SpinnerViewHolder();
            holder.dateTextView = (TextView) view.findViewById(R.id.dateTextView);

            view.setTag(holder);
        }
        else {

            holder = (SpinnerViewHolder) view.getTag();
        }

        SimpleDateFormat  formatter = new SimpleDateFormat("MMM, dd yyyy");
        holder.dateTextView.setText(formatter.format(mDates.get(position)));

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return getView(position, convertView, parent);
    }

    private static class SpinnerViewHolder {

        TextView dateTextView;
    }
}
