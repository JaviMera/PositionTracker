package com.javier.positiontracker.ui;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by javie on 3/12/2017.
 */

public class TrackerActivityPresenter {

    private TrackerActivityView mView;

    public TrackerActivityPresenter(TrackerActivityView view) {

        mView = view;
    }

    public void showSnackbar(String message) {

        mView.showSnackbar(message);
    }

    public void setBoundToService(boolean isBound) {

        mView.setBoundToService(isBound);
    }

    public void setNotificationActive(boolean isNotificationActive) {

        mView.setNotificationActive(isNotificationActive);
    }

    public void drawMenuIcons() {

        mView.drawMenuIcons();
    }

    public void moveMapCamera(LatLng latLng) {

        mView.moveMapCamera(latLng);
    }

    public void zoomMapCamera(float zoomLvl, int animDuration, GoogleMap.CancelableCallback callback) {

        mView.zoomMapCamera(zoomLvl, animDuration, callback);
    }
}
