package com.example.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.sunshine.data.WeatherContract.WeatherEntry;


/**
 * Manages a local database for weather data.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="weather.db";
    public static final int DATABASE_VERSION=3;

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //to create database for first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_WEATHER_TABLE =
                "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                     /*
                     * WeatherEntry did not explicitly declare a column called "_ID". However,
                     * WeatherEntry implements the interface, "BaseColumns", which does have a field
                     * named "_ID". We use that here to designate our table's primary key.
                     */
                 WeatherEntry._ID               +   " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                 WeatherEntry.COLUMN_DATE       +   " INTEGER NOT NULL, "   +
                 WeatherEntry.COLUMN_WEATHER_ID +   " INTEGER NOT NULL, "   +

                 WeatherEntry.COLUMN_MIN_TEMP   +   " REAL NOT NULL, "      +
                 WeatherEntry.COLUMN_MAX_TEMP   +   " REAL NOT NULL, "      +

                 WeatherEntry.COLUMN_HUMIDITY   +   " REAL NOT NULL, "      +
                 WeatherEntry.COLUMN_PRESSURE   +   " REAL NOT NULL, "      +

                 WeatherEntry.COLUMN_WIND_SPEED +   " REAL NOT NULL, "      +
                 WeatherEntry.COLUMN_DEGREES    +   " REAL NOT NULL, "      +
                 " UNIQUE (" + WeatherEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";
        // here we create table after declaring schema in above
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME);
        onCreate(db);
    }
}
