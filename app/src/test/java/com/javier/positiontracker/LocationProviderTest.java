package com.javier.positiontracker;

import com.javier.positiontracker.model.LocationProvider;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by javie on 3/19/2017.
 */
public class LocationProviderTest {

    private LocationProvider mTarget;

    @Test
    public void isEnabledReturnsTrue() throws Exception {

        // Arrange
        mTarget = new LocationProvider(true);

        // Act
        boolean enabled = mTarget.isEnabled();

        // Assert
        Assert.assertTrue(enabled);
    }

    @Test
    public void isEnabledReturnsFalse() throws Exception {

        // Arrange
        mTarget = new LocationProvider(false);

        // Act
        boolean enabled = mTarget.isEnabled();

        // Assert
        Assert.assertFalse(enabled);
    }
}