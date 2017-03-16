package com.javier.positiontracker.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by javie on 2/17/2017.
 */

public class PositionTrackerSQLiteHelper extends SQLiteOpenHelper {

    public static final String POSITION_TRACKER_DB = "position_tracker.db";
    public static final int VERSION = 5;

    public static final String LOCATION_TABLE = "location";
    public static final String LOCATION_LAT = "latitude";
    public static final String LOCATION_LONG = "longitude";
    public static final String LOCATION_DATE = "date";

    private String CREATE_LOCATION_TABLE = "CREATE TABLE "
            + LOCATION_TABLE
            + "("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LOCATION_LAT + " REAL, "
            + LOCATION_LONG + " REAL, "
            + LOCATION_DATE + " INTEGER"
            + ")";

    public static final String TIME_LIMIT_TABLE = "time_limit";
    public static final String TIME_LIMIT_TIME = "time";
    public static final String TIME_LIMIT_CREATION_TIME = "created_at";
    private String CREATE_TIME_LIMIT_TABLE = "CREATE TABLE "
        + TIME_LIMIT_TABLE
        + "("
        + TIME_LIMIT_TIME + " INTEGER, "
        + TIME_LIMIT_CREATION_TIME + " INTEGER"
        + ")";

    public PositionTrackerSQLiteHelper(Context context) {
        super(context, POSITION_TRACKER_DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(CREATE_TIME_LIMIT_TABLE);
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
                sqLiteDatabase.execSQL("CREATE TABLE "
                    + "location_time"
                    + "("
                    + "latitude" + " REAL, "
                    + "longitude" + " REAL, "
                    + "time_elapse" + " INTEGER DEFAULT 0, "
                    + "PRIMARY TIME_KEY (" + "latitude" + ", " + "longitude" + ")"
                    + ")"
                );

                break;

            case 3:
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "location_time");
                sqLiteDatabase.execSQL(CREATE_TIME_LIMIT_TABLE);
                break;

            case 4:
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
                break;
        }
    }
}
