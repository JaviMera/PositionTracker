package com.javier.positiontracker.ui;

/**
 * Created by javie on 3/15/2017.
 */

public class LocationsActivityPresenter {

    private LocationsActivityView mView;

    public LocationsActivityPresenter(LocationsActivityView view) {

        mView = view;
    }

    public void setRecyclerEnabled(boolean enabled) {

        mView.setRecyclerEnabled(enabled);
    }

    public void setSpinnerEnabled(boolean enabled) {

        mView.setSpinnerEnabled(enabled);
    }

    public void initializeRecyclerView() {

        mView.initializeRecyclerView();
    }

    public void initializeSpinnerView() {

        mView.initializeSpinnerView();
    }

    public void initializeCheckBoxView() {

        mView.initializeCheckBoxView();
    }
}
