package com.javier.positiontracker.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by javie on 3/15/2017.
 */

public class RecyclerLinearLayout extends LinearLayoutManager {

    private boolean mCanScroll;

    public RecyclerLinearLayout(Context context) {
        super(context);

        mCanScroll = true;
    }

    public void setCanScroll(boolean canScroll) {

        mCanScroll = canScroll;
    }

    @Override
    public boolean canScrollVertically() {

        return mCanScroll;
    }
}
