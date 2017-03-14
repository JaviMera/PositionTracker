package com.javier.positiontracker.ui;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by javie on 3/12/2017.
 */

public interface TrackerActivityView {

    void showSnackbar(String message);
    void setBoundToService(boolean isBound);
    void setNotificationActive(boolean isNotificationActive);
    void drawMenuIcons();
    void moveMapCamera(LatLng latLng);
    void zoomMapCamera(float zoomLvl, int animDuration, GoogleMap.CancelableCallback callback);
    void setFabVisible(boolean visible);
    void setDisplayHome(boolean visible);
    void setMarkerVisible(boolean visible);
}
