package com.javier.positiontracker.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.javier.positiontracker.R;
import com.javier.positiontracker.model.UserLocation;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by javie on 3/14/2017.
 */

public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.LocationViewHolder>{

    private Context mContext;
    private List<UserLocation> mLocations;
    private boolean mEnabled;

    public LocationRecyclerAdapter(Context context) {

        mContext = context;
        mLocations = new LinkedList<>();
        mEnabled = true;
    }

    public void setLocations(List<UserLocation> locations) {

        mLocations.clear();
        mLocations.addAll(locations);
        notifyItemRangeInserted(0, mLocations.size());
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater
            .from(mContext)
            .inflate(R.layout.recycler_location_item, parent, false);

        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {

        holder.bind(mLocations.get(position));
    }

    @Override
    public int getItemCount() {

        return mLocations.size();
    }

    public void setEnabled(boolean b) {

        mEnabled = b;
        notifyItemRangeChanged(0, mLocations.size());
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView mLocationTextView;

        LocationViewHolder(View itemView) {

            super(itemView);

            mLocationTextView = (TextView) itemView.findViewById(R.id.locationTextView);
        }

        void bind(UserLocation userLocation) {

            mLocationTextView.setText(
                String.format(Locale.ENGLISH,
                    "Lat: %f Long: %f",
                    userLocation.getPosition().latitude,
                    userLocation.getPosition().longitude
                )
            );

            if(mEnabled) {

                mLocationTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
            }
            else {

                mLocationTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.darker_gray));
            }
        }
    }
}
