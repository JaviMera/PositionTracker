package com.javier.positiontracker.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.javier.positiontracker.R;
import com.javier.positiontracker.model.LocationAddress;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by javie on 3/14/2017.
 */

public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.LocationViewHolder>{

    private Context mContext;
    private List<LocationAddress> mAddresses;

    public LocationRecyclerAdapter(Context context) {

        mContext = context;
        mAddresses = new LinkedList<>();
    }

    public void setLocations(List<LocationAddress> locations) {

        int count = mAddresses.size();
        mAddresses.clear();
        notifyItemRangeRemoved(0, count);

        mAddresses.addAll(locations);
        notifyItemRangeInserted(0, mAddresses.size());
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater
            .from(mContext)
            .inflate(R.layout.address_item, parent, false);

        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {

        holder.bind(mAddresses.get(position));
    }

    @Override
    public int getItemCount() {

        return mAddresses.size();
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView mStreetTextView;
        TextView mAreaTextView;
        TextView mTimeTextView;

        LocationViewHolder(View itemView) {

            super(itemView);

            mStreetTextView = (TextView) itemView.findViewById(R.id.streetTextView);
            mAreaTextView = (TextView) itemView.findViewById(R.id.areaTextView);
            mTimeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
        }

        void bind(LocationAddress locationAddress) {

            mStreetTextView.setText(locationAddress.getStreet());
            mAreaTextView.setText(locationAddress.getArea());
            mTimeTextView.setText(
                String.format("%s:%s", locationAddress.getHour(), locationAddress.getMinute())
            );
        }
    }
}
