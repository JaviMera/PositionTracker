package com.javier.positiontracker.model;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by javie on 3/18/2017.
 */

public class AddressCache {

    public static final int MEMORY_AMOUNT = 8;

    private LruCache mMemoryCache;
    private int mMemory;

    public AddressCache(int maxMemory) {

        mMemory = maxMemory / MEMORY_AMOUNT;

        mMemoryCache = new LruCache(maxMemory);
    }

    public int getTotalMemory() {

        return mMemory;
    }

    public LocationAddress insert(LatLng key, LocationAddress value) {

        if(key == null || value == null)
            return null;

        if(mMemoryCache.get(key) != null)
            return null;

        return (LocationAddress) mMemoryCache.put(key, value);
    }

    public int size() {

        return mMemoryCache.size();
    }

    public LocationAddress get(LatLng key) {

        return (LocationAddress) mMemoryCache.get(key);
    }
}
