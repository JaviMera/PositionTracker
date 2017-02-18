package com.javier.positiontracker;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.javier.positiontracker.databases.PositionTrackerDataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by javie on 2/17/2017.
 */

@RunWith(AndroidJUnit4.class)
public class PositionTrackerDataSourceTest {

    private PositionTrackerDataSource mTarget;

    @Before
    public void name() throws Exception {

        mTarget = new PositionTrackerDataSource(InstrumentationRegistry.getContext());
    }

    @Test
    public void dbShouldCreateLocation() throws Exception {


    }
}
