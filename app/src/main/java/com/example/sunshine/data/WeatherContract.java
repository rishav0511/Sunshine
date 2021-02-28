package com.example.sunshine.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.sunshine.utilities.SunshineDateUtils;

/**
 * Defines table and column names for the weather database. This class is not necessary, but keeps
 * the code organized.
 */
public class WeatherContract {
    public static String CONTENT_AUTHORITY = "com.example.sunshine";
    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Sunshine.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = "weather";


    public static final class WeatherEntry implements BaseColumns{
        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_DATE="date";
        public static final String COLUMN_WEATHER_ID="weather_id";
        public static final String COLUMN_MIN_TEMP="min";
        public static final String COLUMN_MAX_TEMP="max";
        public static final String COLUMN_HUMIDITY="humidity";
        public static final String COLUMN_PRESSURE="pressure";
        public static final String COLUMN_WIND_SPEED="wind";
        public static final String COLUMN_DEGREES="degrees";

        /**
         * Builds a URI that adds the weather date to the end of the forecast content URI path.
         * This is used to query details about a single weather entry by date. This is what we
         * use for the detail view query. We assume a normalized date is passed to this method.
         *
         * @param Date Normalized date in milliseconds
         * @return Uri to query details about a single weather entry
         */
        public static Uri buildWeatherUriWithDate(long Date){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(Date)).build();
        }

        /**
         * Returns just the selection part of the weather query from a normalized today value.
         * This is used to get a weather forecast from today's date. To make this easy to use
         * in compound selection, we embed today's date as an argument in the query.
         *
         * @return The selection part of the weather query for today onwards
         */
        public static String getSqlSelectForTodayOnwards(){
            long normalizedUtcNow = SunshineDateUtils.normalizeDate(System.currentTimeMillis());
            return WeatherEntry.COLUMN_DATE + ">=" + normalizedUtcNow;
        }
    }

}
