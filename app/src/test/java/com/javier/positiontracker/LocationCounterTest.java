package com.javier.positiontracker;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by javie on 3/8/2017.
 */

public class LocationCounterTest {

    private LocationCounter mTarget;

    @Before
    public void setUp() throws Exception {

        mTarget = new LocationCounter();
    }

    @Test
    public void incrementCounter() throws Exception {

        // Arrange
        long time = 1000;

        // Act
        mTarget.increment(time);
        mTarget.increment(time);

        // Assert
        Assert.assertEquals(1000 * 2, mTarget.getCounter());
    }

    @Test
    public void resetReturnsZero() throws Exception {

        // Arrange
        long time = 2000;

        // Act
        mTarget.increment(time);
        mTarget.reset();

        // Assert
        Assert.assertTrue(mTarget.getCounter() == 0L);
    }
}
