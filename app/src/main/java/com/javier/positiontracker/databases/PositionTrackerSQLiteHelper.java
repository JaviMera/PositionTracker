package com.javier.positiontracker.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by javie on 2/17/2017.
 */

public class PositionTrackerSQLiteHelper extends SQLiteOpenHelper {

    public static final String POSITION_TRACKER_DB = "position_tracker.db";
    public static final int VERSION = 3;

    public static final String LOCATION_TABLE = "location";
    public static final String LOCATION_LAT = "latitude";
    public static final String LOCATION_LONG = "longitude";
    public static final String LOCATION_DATE = "date";

    private String CREATE_LOCATION_TABLE = "CREATE TABLE "
            + LOCATION_TABLE
            + "("
            + LOCATION_LAT + " REAL, "
            + LOCATION_LONG + " REAL, "
            + LOCATION_DATE + " INTEGER, "
            + "PRIMARY KEY (" + LOCATION_LAT + ", " + LOCATION_LONG + ")"
            + ")";

    public static final String LOCATION_TIME_TABLE = "location_time";
    public static final String LOCATION_TIME_LAT = "latitude";
    public static final String LOCATION_TIME_LONG = "longitude";
    public static final String LOCATION_TIME_ELAPSE = "time";

    private String CREATE_LOCATION_TIME_TABLE = "CREATE TABLE "
        + LOCATION_TIME_TABLE
        + "("
        + LOCATION_TIME_LAT + " REAL, "
        + LOCATION_TIME_LONG + " REAL, "
        + LOCATION_TIME_ELAPSE + " INTEGER DEFAULT 0, "
        + "PRIMARY KEY (" + LOCATION_TIME_LAT + ", " + LOCATION_TIME_LONG + ")"
        + ")";

    public PositionTrackerSQLiteHelper(Context context) {
        super(context, POSITION_TRACKER_DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(CREATE_LOCATION_TIME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        switch(oldVersion) {

            case 1:

                // Changed lat long column types to be REAL and not INTEGER
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
                onCreate(sqLiteDatabase);
                break;

            case 2:
                sqLiteDatabase.execSQL(CREATE_LOCATION_TIME_TABLE);
                break;
        }
    }
}
