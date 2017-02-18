package com.javier.positiontracker.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

/**
 * Created by javie on 2/17/2017.
 */

public class PositionTrackerDataSource {

    private PositionTrackerSQLiteHelper mHelper;

    public PositionTrackerDataSource(Context ctx) {

        mHelper = new PositionTrackerSQLiteHelper(ctx);
    }

    public void create() {

        SQLiteDatabase mDb = mHelper.getWritableDatabase();
        mDb.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(PositionTrackerSQLiteHelper.LOCATION_LAT, 2);
        values.put(PositionTrackerSQLiteHelper.LOCATION_LONG, -2);
        values.put(PositionTrackerSQLiteHelper.LOCATION_DATE, new Date().getTime());

        long rowId = mDb.insert(
                PositionTrackerSQLiteHelper.LOCATION_TABLE,
                null,
                values);

        mDb.setTransactionSuccessful();
        mDb.endTransaction();
    }
}
