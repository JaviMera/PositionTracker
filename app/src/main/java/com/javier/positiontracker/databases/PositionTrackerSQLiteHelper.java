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
    public static final int VERSION = 10;
    public static final long LAST_LOCATION_ID_VALUE = 1;

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
            + "PRIMARY KEY (" + LOCATION_LAT + ", " + LOCATION_LONG + ", " + LOCATION_DATE + ")"
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

    public static final String LOCATION_ADDRESS_TABLE = "location_address";
    public static final String LOCATION_ADDRESS_LAT = "latitude";
    public static final String LOCATION_ADDRESS_LONG = "longitude";
    public static final String LOCATION_ADDRESS_STREET = "street";
    public static final String LOCATION_ADDRESS_AREA = "area";
    public static final String LOCATION_ADDRESS_POSTAL = "postal";
    private String CREATE_LOCATION_ADDRESS_TABLE = "CREATE TABLE "
        + LOCATION_ADDRESS_TABLE
        + "("
        + LOCATION_ADDRESS_LAT + " REAL, "
        + LOCATION_ADDRESS_LONG + " REAL, "
        + LOCATION_ADDRESS_STREET + " TEXT, "
        + LOCATION_ADDRESS_AREA + " TEXT, "
        + LOCATION_ADDRESS_POSTAL + " TEXT, "
        + "PRIMARY KEY (" + LOCATION_ADDRESS_LAT + ", " + LOCATION_ADDRESS_LONG + ")"
        +")";

    public static final String LAST_LOCATION_TABLE = "last_location";
    public static final String LAST_LOCATION_ID = "last_location_id";
    public static final String LAST_LOCATION_PROVIDER = "provider";
    public static final String LAST_LOCATION_LAT = "latitude";
    public static final String LAST_LOCATION_LONG = "longitude";
    private String CREATE_LAST_LOCATION_TABLE = "CREATE TABLE "
        + LAST_LOCATION_TABLE
        + "("
        + LAST_LOCATION_ID + " INTEGER, "
        + LAST_LOCATION_LAT + " REAL, "
        + LAST_LOCATION_LONG + " REAL, "
        + LAST_LOCATION_PROVIDER + " TEXT,"
        + "PRIMARY KEY (" + LAST_LOCATION_ID + ")"
        + ")";

    public PositionTrackerSQLiteHelper(Context context) {
        super(context, POSITION_TRACKER_DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(CREATE_TIME_LIMIT_TABLE);
        sqLiteDatabase.execSQL(CREATE_LOCATION_ADDRESS_TABLE);
        sqLiteDatabase.execSQL(CREATE_LAST_LOCATION_TABLE);
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

                // Drop table after deleting the primary key composed of lat and long
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
                break;

            case 5:
            case 6:
                // Drop table after creating compound key with lat long and date
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
                break;

            case 7:
                sqLiteDatabase.execSQL(CREATE_LOCATION_ADDRESS_TABLE);
                break;

            case 8:
                sqLiteDatabase.execSQL(CREATE_LAST_LOCATION_TABLE);
                break;

            case 9:
                // re-create table with id column
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LAST_LOCATION_TABLE);
                break;
        }
    }
}
