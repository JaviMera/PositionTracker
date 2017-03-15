package com.javier.positiontracker.ui;

/**
 * Created by javie on 3/15/2017.
 */

public interface LocationsActivityView {

    void setRecyclerEnabled(boolean enabled);
    void setSpinnerEnabled(boolean enabled);
    void initializeRecyclerView();
    void initializeSpinnerView();
    void initializeCheckBoxView();
}
