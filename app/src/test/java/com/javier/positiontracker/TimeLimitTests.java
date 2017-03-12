package com.javier.positiontracker;

import com.javier.positiontracker.model.TimeLimit;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Created by javie on 3/12/2017.
 */

public class TimeLimitTests {

    private TimeLimit mTarget;
    private long mTime = 60 * 1000; // 1 minute
    private long mCreatedAt = new Date().getTime(); // current time

    @Before
    public void setUp() throws Exception {

        mTarget = new TimeLimit(mTime, mCreatedAt);
    }

    @Test
    public void getTime() throws Exception {

        // Assert
        Assert.assertEquals(mTime, mTarget.getTime());
    }

    @Test
    public void getCreatedAt() throws Exception {

        // Assert
        Assert.assertEquals(mCreatedAt, mTarget.getCreatedAt());
    }
}
