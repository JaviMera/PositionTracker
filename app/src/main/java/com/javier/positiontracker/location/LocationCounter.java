package com.javier.positiontracker.location;

/**
 * Created by javie on 3/8/2017.
 */

public class LocationCounter {

    private long mCounter;

    public LocationCounter() {

        reset();
    }

    public void increment(long time) {

        mCounter += time;
    }

    public long getCounter() {

        return mCounter;
    }

    public void reset() {

        mCounter = 0L;
    }
}
