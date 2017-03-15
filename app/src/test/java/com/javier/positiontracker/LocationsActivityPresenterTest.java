package com.javier.positiontracker;

import com.javier.positiontracker.ui.LocationsActivityPresenter;
import com.javier.positiontracker.ui.LocationsActivityView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Created by javie on 3/15/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class LocationsActivityPresenterTest {

    @Mock
    public LocationsActivityView mView;

    private LocationsActivityPresenter mTarget;

    @Before
    public void setUp() throws Exception {

        mTarget = new LocationsActivityPresenter(mView);
    }

    @Test
    public void initializeRecyclerView() throws Exception {

        // Act
        mTarget.initializeRecyclerView();

        // Assert
        Mockito.verify(mView).initializeRecyclerView();
    }

    @Test
    public void initializeSpinnerView() throws Exception {

        // Act
        mTarget.initializeSpinnerView();

        // Assert
        Mockito.verify(mView).initializeSpinnerView();
    }

    @Test
    public void initializeCheckBoxView() throws Exception {

        // Act
        mTarget.initializeCheckBoxView();

        // Assert
        Mockito.verify(mView).initializeCheckBoxView();
    }

    @Test
    public void setRecyclerEnabled() throws Exception {

        // Arrange
        boolean enabled = false;

        // Act
        mTarget.setRecyclerEnabled(enabled);

        // Assert
        Mockito.verify(mView).setRecyclerEnabled(enabled);
    }

    @Test
    public void setSpinnerEnabled() throws Exception {

        // Arrange
        boolean enabled = true;

        // Act
        mTarget.setSpinnerEnabled(enabled);

        // Assert
        Mockito.verify(mView).setSpinnerEnabled(enabled);
    }
}