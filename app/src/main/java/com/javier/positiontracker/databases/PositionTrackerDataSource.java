package com.javier.positiontracker.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.exceptions.ExistingLocationException;
import com.javier.positiontracker.model.UserLocation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by javie on 2/17/2017.
 */

public class PositionTrackerDataSource {

    private PositionTrackerSQLiteHelper mHelper;
    private SQLiteDatabase mDb;

    public PositionTrackerDataSource(Context ctx) {

        mHelper = new PositionTrackerSQLiteHelper(ctx);
    }

    public long insertUserLocation(UserLocation location) {

        mDb = mHelper.getWritableDatabase();
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

    public long insertUserLocationTime(double latitude, double longitude) {

        mDb = mHelper.getWritableDatabase();
        mDb.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(PositionTrackerSQLiteHelper.LOCATION_TIME_LAT, latitude);
        values.put(PositionTrackerSQLiteHelper.LOCATION_TIME_LONG, longitude);

        long rowId = mDb.insert(
            PositionTrackerSQLiteHelper.LOCATION_TIME_TABLE,
            null,
            values
        );

        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        mDb.close();

        return rowId;
    }

    public boolean isClosed() {

        return !mDb.isOpen();
    }

    public void clean() {

        mDb = mHelper.getWritableDatabase();
        mDb.delete(PositionTrackerSQLiteHelper.LOCATION_TABLE, null, null);
        mDb.delete(PositionTrackerSQLiteHelper.LOCATION_TIME_TABLE, null, null);

        mDb.close();
    }

    public List<UserLocation> readLocationsWithRange(long minDate, long maxDate) {

        List<UserLocation> locations = new LinkedList<>();
        mDb = mHelper.getReadableDatabase();
        mDb.beginTransaction();

        Cursor cursor = mDb.query(
            PositionTrackerSQLiteHelper.LOCATION_TABLE,
            new String[]{
                PositionTrackerSQLiteHelper.LOCATION_LAT,
                PositionTrackerSQLiteHelper.LOCATION_LONG,
                PositionTrackerSQLiteHelper.LOCATION_DATE,
            },
            PositionTrackerSQLiteHelper.LOCATION_DATE + " BETWEEN ? AND ?",
            new String[]{String.valueOf(minDate), String.valueOf(maxDate)},
            null,null,null
        );

        if(cursor.moveToFirst()) {

            do {

                long latitude = readLong(cursor, PositionTrackerSQLiteHelper.LOCATION_LAT);
                long longitude = readLong(cursor, PositionTrackerSQLiteHelper.LOCATION_LONG);
                long date = readLong(cursor, PositionTrackerSQLiteHelper.LOCATION_DATE);

                UserLocation location = new UserLocation(new LatLng(latitude, longitude), date);
                locations.add(location);

            }while(cursor.moveToNext());
        }

        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        mDb.close();

        return locations;
    }

    private long readLong(Cursor cursor, String column) {

        int index = cursor.getColumnIndex(column);
        return cursor.getLong(index);
    }


    public boolean hasLocation(UserLocation location) {

        mDb = mHelper.getReadableDatabase();
        mDb.beginTransaction();

        double latitude = location.getLatLong().latitude;
        double longitude = location.getLatLong().longitude;

        Cursor cursor = mDb.query(
                PositionTrackerSQLiteHelper.LOCATION_TABLE,
                new String[]{
                    PositionTrackerSQLiteHelper.LOCATION_LAT,
                    PositionTrackerSQLiteHelper.LOCATION_LONG
                },
                PositionTrackerSQLiteHelper.LOCATION_LAT + "=? AND " + PositionTrackerSQLiteHelper.LOCATION_LONG + "=?",
                new String[]{String.valueOf(latitude), String.valueOf(longitude)},
                null,null,null
        );

        boolean recordExists = false;
        if(cursor.moveToFirst()) {

            cursor.close();
            recordExists = true;
        }

        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        mDb.close();

        return recordExists;
    }
}
