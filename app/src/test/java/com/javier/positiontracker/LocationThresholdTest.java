package com.javier.positiontracker;

import com.javier.positiontracker.model.LocationThreshold;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by javie on 3/8/2017.
 */

public class LocationThresholdTest {

    private LocationThreshold mTarget;

    @Before
    public void setUp() throws Exception {

        mTarget = new LocationThreshold();
    }

    @Test
    public void setThreshold() throws Exception {

        // Arrange
        long expectedThreshold = 2 * 60 * 1000;

        // Act
        mTarget.setThreshold(expectedThreshold);

        // Assert
        Assert.assertEquals(expectedThreshold, mTarget.getThreshold());
    }

    @Test
    public void noThresholdReturnsValidThresholdFalse() throws Exception {

        // Assert
        Assert.assertFalse(mTarget.hasValidThreshold());
    }

    @Test
    public void thresholdReturnsValidThresholdTrue() throws Exception {

        // Act
        mTarget.setThreshold(1000);

        // Assert
        Assert.assertTrue(mTarget.hasValidThreshold());
    }

    @Test
    public void resetThresholdReturnsValidThresholdFalse() throws Exception {

        // Act
        mTarget.setThreshold(1000);
        mTarget.reset();

        // Assert
        Assert.assertFalse(mTarget.hasValidThreshold());
    }
}
