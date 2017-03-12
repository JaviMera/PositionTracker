package com.javier.positiontracker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.ui.TrackerActivityPresenter;
import com.javier.positiontracker.ui.TrackerActivityView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by javie on 3/12/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class TrackerActivityPresenterTests {

    @Mock
    public TrackerActivityView mView;

    private TrackerActivityPresenter mTarget;

    @Before
    public void setUp() throws Exception {

        mTarget = new TrackerActivityPresenter(mView);
    }

    @Test
    public void showSnackbar() throws Exception {

        // Arrange
        String message = "bush did harambe";

        // Act
        mTarget.showSnackbar(message);

        // Assert
        Mockito.verify(mView).showSnackbar(message);
    }

    @Test
    public void setBoundToService() throws Exception {

        // Arrange
        boolean isBound = false;

        // Act
        mTarget.setBoundToService(isBound);

        // Assert
        Mockito.verify(mView).setBoundToService(isBound);
    }

    @Test
    public void setNotificationActive() throws Exception {

        // Arrange
        boolean isNotificationActive = false;

        // Act
        mTarget.setNotificationActive(isNotificationActive);

        // Assert
        Mockito.verify(mView).setNotificationActive(isNotificationActive);
    }

    @Test
    public void drawMenuIcons() throws Exception {

        // Act
        mTarget.drawMenuIcons();

        // Assert
        Mockito.verify(mView).drawMenuIcons();
    }

    @Test
    public void moveMapCamera() throws Exception {

        // Arrange
        LatLng latLng = new LatLng(1,2);

        // Act
        mTarget.moveMapCamera(latLng);

        // Assert
        Mockito.verify(mView).moveMapCamera(latLng);
    }

    @Test
    public void zoomMapCamera() throws Exception {

        // Arrange
        float zoomLvl = 0f;
        int animDuration = 1000;
        GoogleMap.CancelableCallback callback = null;

        // Act
        mTarget.zoomMapCamera(zoomLvl, animDuration, callback);

        // Assert
        Mockito.verify(mView).zoomMapCamera(zoomLvl, animDuration, callback);
    }
}
