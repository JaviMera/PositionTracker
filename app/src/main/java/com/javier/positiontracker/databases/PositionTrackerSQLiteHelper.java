package com.javier.positiontracker.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by javie on 2/17/2017.
 */

public class PositionTrackerSQLiteHelper extends SQLiteOpenHelper {


    public static final String LOCATION_TABLE = "location";
    public static final String LOCATION_LAT = "latitude";
    public static final String LOCATION_LONG = "longitude";
    public static final String LOCATION_DATE = "date";
    private String CREATE_LOCATION_TABLE = "CREATE TABLE "
            + LOCATION_TABLE
            + "("
            + LOCATION_LAT + " INTEGER, "
            + LOCATION_LONG + " INTEGER, "
            + LOCATION_DATE + " INTEGER, "
            + "PRIMARY KEY (" + LOCATION_LAT + ", " + LOCATION_LONG + ")"
            + ")";

    public PositionTrackerSQLiteHelper(Context context) {
        super(context, "position_tracker.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
