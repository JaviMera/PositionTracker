package com.javier.positiontracker.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.gson.Gson;
import com.javier.positiontracker.R;

/**
 * Created by javie on 3/17/2017.
 */

public class TrackerSharedPreferences {

    private Context mContext;
    private String mKey;

    public TrackerSharedPreferences(Context context, String key) {

        mContext = context;
        mKey = key;
    }

    public boolean containsString(String keyString) {

        Gson gson = new Gson();
        SharedPreferences preferences = mContext.getSharedPreferences(mKey, Context.MODE_PRIVATE);

        return preferences.contains(keyString);
    }

    public <T extends Object> void putString(String keyString, T value) {

        Gson gson = new Gson();
        mContext.getSharedPreferences(mKey, Context.MODE_PRIVATE)
            .edit()
            .putString(keyString, gson.toJson(value))
            .apply();
    }

    public <T> T getString(String keyString, Class<T> objectClass) {

        Gson gson = new Gson();
        SharedPreferences preferences = mContext.getSharedPreferences(mKey, Context.MODE_PRIVATE);
        String json = preferences.getString(keyString, "");

        return gson.fromJson(json, objectClass);
    }

    public void removeString(String keyCurrentLocation) {

        mContext.getSharedPreferences(mKey, Context.MODE_PRIVATE)
            .edit()
            .remove(keyCurrentLocation)
            .apply();
    }
}
