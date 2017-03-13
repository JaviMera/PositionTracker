package com.javier.positiontracker.model;

/**
 * Created by javie on 3/8/2017.
 */

public class LocationThreshold {

    private long mThreshold;

    public LocationThreshold() {

        reset();
    }

    public void setThreshold(long threshold) {

        mThreshold = threshold;
    }

    public long getThreshold() {

        return mThreshold;
    }

    public boolean hasValidThreshold() {

        return mThreshold > 0L;
    }

    public void reset() {

        mThreshold = 0L;
    }
}
