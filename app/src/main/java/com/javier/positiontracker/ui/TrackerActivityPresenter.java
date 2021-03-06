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

    public void displayMenuIcons(boolean display) {

        mView.displayMenuIcons(display);
    }

    public void moveMapCamera(LatLng latLng) {

        mView.moveMapCamera(latLng);
    }

    public void zoomMapCamera(float zoomLvl, int animDuration, GoogleMap.CancelableCallback callback) {

        mView.zoomMapCamera(zoomLvl, animDuration, callback);
    }

    public void setFabVisible(boolean visible) {

        mView.setFabVisible(visible);
    }

    public void setDisplayHome(boolean visible) {

        mView.setDisplayHome(visible);
    }

    public void setMarkerVisible(boolean visible) {

        mView.setMarkerVisible(visible);
    }

    public void setTitle(String title) {

        mView.setTitle(title);
    }
}
