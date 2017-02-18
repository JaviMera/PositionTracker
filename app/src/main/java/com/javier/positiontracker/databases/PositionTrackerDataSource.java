package com.javier.positiontracker.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.javier.positiontracker.model.UserLocation;

/**
 * Created by javie on 2/17/2017.
 */

public class PositionTrackerDataSource {

    private PositionTrackerSQLiteHelper mHelper;

    public PositionTrackerDataSource(Context ctx) {

        mHelper = new PositionTrackerSQLiteHelper(ctx);
    }

    public long create(UserLocation location) {

        SQLiteDatabase mDb = mHelper.getWritableDatabase();
        mDb.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(PositionTrackerSQLiteHelper.LOCATION_LAT, location.getLatLong().latitude);
        values.put(PositionTrackerSQLiteHelper.LOCATION_LONG, location.getLatLong().longitude);
        values.put(PositionTrackerSQLiteHelper.LOCATION_DATE, location.getDate());

        long rowId = mDb.insert(
                PositionTrackerSQLiteHelper.LOCATION_TABLE,
                null,
                values);

        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        mDb.close();
        return rowId;
    }
}
