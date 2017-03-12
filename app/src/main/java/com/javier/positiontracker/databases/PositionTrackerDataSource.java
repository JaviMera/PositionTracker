package com.javier.positiontracker.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;
import com.javier.positiontracker.model.TimeLimit;
import com.javier.positiontracker.model.UserLocation;

import java.util.Date;
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
        values.put(PositionTrackerSQLiteHelper.LOCATION_LAT, location.getPosition().latitude);
        values.put(PositionTrackerSQLiteHelper.LOCATION_LONG, location.getPosition().longitude);
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

    public boolean isClosed() {

        return !mDb.isOpen();
    }

    public void clean() {

        mDb = mHelper.getWritableDatabase();
        mDb.delete(PositionTrackerSQLiteHelper.LOCATION_TABLE, null, null);
        mDb.delete(PositionTrackerSQLiteHelper.TIME_LIMIT_TABLE, null, null);

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

                double latitude = readDouble(cursor, PositionTrackerSQLiteHelper.LOCATION_LAT);
                double longitude = readDouble(cursor, PositionTrackerSQLiteHelper.LOCATION_LONG);
                long date = getLong(cursor, PositionTrackerSQLiteHelper.LOCATION_DATE);

                UserLocation location = new UserLocation(new LatLng(latitude, longitude), date);
                locations.add(location);

            }while(cursor.moveToNext());
        }

        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        mDb.close();

        return locations;
    }

    private long getLong(Cursor cursor, String column) {

        int index = cursor.getColumnIndex(column);
        return cursor.getLong(index);
    }

    private double readDouble(Cursor cursor, String column) {

        int index = cursor.getColumnIndex(column);
        return cursor.getDouble(index);
    }


    public boolean hasLocation(UserLocation location) {

        mDb = mHelper.getReadableDatabase();

        double latitude = location.getPosition().latitude;
        double longitude = location.getPosition().longitude;

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

        mDb.close();

        return recordExists;
    }

    public long insertTimeLimit(long minutes, long createdAt) {

        mDb = mHelper.getWritableDatabase();
        mDb.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(PositionTrackerSQLiteHelper.TIME_LIMIT_TIME, minutes);
        values.put(PositionTrackerSQLiteHelper.TIME_LIMIT_CREATION_TIME, createdAt);

        long rowId = mDb.insert(
            PositionTrackerSQLiteHelper.TIME_LIMIT_TABLE,
            null,
            values);

        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        mDb.close();

        return rowId;
    }

    public TimeLimit readTimeLimit() {

        mDb = mHelper.getReadableDatabase();

        Cursor cursor = mDb.query(
            PositionTrackerSQLiteHelper.TIME_LIMIT_TABLE,
            new String[]{
                PositionTrackerSQLiteHelper.TIME_LIMIT_TIME,
                PositionTrackerSQLiteHelper.TIME_LIMIT_CREATION_TIME
            },
            null,
            null,
            null,
            null,
            null
        );

        TimeLimit timeLimit = null;
        if(cursor.moveToFirst()) {

            long time = getLong(cursor, PositionTrackerSQLiteHelper.TIME_LIMIT_TIME);
            long createdAt = getLong(cursor, PositionTrackerSQLiteHelper.TIME_LIMIT_CREATION_TIME);

            timeLimit = new TimeLimit(time, createdAt);
        }

        cursor.close();
        mDb.close();

        return timeLimit;
    }

    public long deleteTimeLimit() {

        mDb = mHelper.getWritableDatabase();
        mDb.beginTransaction();

        long affectedRow = mDb.delete(
            PositionTrackerSQLiteHelper.TIME_LIMIT_TABLE,
            "1",
            null
        );

        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        mDb.close();

        return affectedRow;
    }
}
