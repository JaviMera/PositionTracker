package com.javier.positiontracker.model;

/**
 * Created by javie on 3/12/2017.
 */

public class TimeLimit {

    private long mTime;
    private long mCreatedAt;

    public TimeLimit(long time, long createdAt) {

        mTime = time;
        mCreatedAt = createdAt;
    }

    public long getTime() {
        return mTime;
    }

    public long getCreatedAt() {
        return mCreatedAt;
    }
}
