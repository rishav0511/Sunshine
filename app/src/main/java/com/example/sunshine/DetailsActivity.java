package com.example.sunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.content.CursorLoader;

import com.example.sunshine.data.WeatherContract;
import com.example.sunshine.utilities.SunshineDateUtils;
import com.example.sunshine.utilities.SunshineWeatherUtils;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTemperatureView;
    private TextView mLowTemperatureView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;
    private String mForecast;

    //Uri that is used to access the chosen day's weather details.
    private Uri mUri;

    public static final int INDEX_WEATHER_DATE=0;
    public static final int INDEX_WEATHER_MAX_TEMP=1;
    public static final int INDEX_WEATHER_MIN_TEMP=2;
    public static final int INDEX_WEATHER_HUMIDITY=3;
    public static final int INDEX_WEATHER_PRESSURE=4;
    public static final int INDEX_WEATHER_WIND_SPEED=5;
    public static final int INDEX_WEATHER_DEGREES=6;
    public static final int INDEX_WEATHER_CONDITION_ID=7;

    public static final String [] WEATHER_DETAIL_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    public static final int ID_DETAIL_LOADER = 69;
    private String mForecastSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mDateView=(TextView)findViewById(R.id.date);
        mDescriptionView=(TextView)findViewById(R.id.description);
        mHighTemperatureView=(TextView)findViewById(R.id.high_temperature);
        mLowTemperatureView=(TextView)findViewById(R.id.low_temperature);
        mHumidityView=(TextView)findViewById(R.id.humidity);
        mWindView=(TextView)findViewById(R.id.wind);
        mPressureView=(TextView)findViewById(R.id.pressure);
        mUri=getIntent().getData();
        if(mUri==null) throw new NullPointerException("URI for DetailsActivity cannot be NULL");
        getLoaderManager().initLoader(ID_DETAIL_LOADER,null,this);
    }

    private Intent createShareForecastIntent(){
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                                .setType("text/plain")
                                .setText(mForecast+FORECAST_SHARE_HASHTAG)
                                .getIntent();
        return shareIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail,menu);
        MenuItem menuItem=menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId==R.id.action_settings){
            Intent intent =new Intent(DetailsActivity.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArgs) {

        switch (loaderId) {

//          COMPLETED (23) If the loader requested is our detail loader, return the appropriate CursorLoader
            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        WEATHER_DETAIL_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    /**
     * Runs on the main thread when a load is complete. If initLoader is called (we call it from
     * onCreate in DetailActivity) and the LoaderManager already has completed a previous load
     * for this Loader, onLoadFinished will be called immediately. Within onLoadFinished, we bind
     * the data to our views so the user can see the details of the weather on the date they
     * selected from the forecast.
     *
     * @param loader The cursor loader that finished.
     * @param data   The cursor that is being returned.
     */
    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData=false;
        if(data!=null && data.moveToFirst()){
            cursorHasValidData=true;
        }
        if(!cursorHasValidData){
            return;
        }
        long localDateMidnightGmt = data.getLong(INDEX_WEATHER_DATE);
        String dateText = SunshineDateUtils.getFriendlyDateString(this,localDateMidnightGmt,true);
        mDateView.setText(dateText);

        int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
        String description = SunshineWeatherUtils.getStringForWeatherCondition(this,weatherId);
        mDescriptionView.setText(description);

        double highInCelcius = data.getDouble(INDEX_WEATHER_MAX_TEMP);
        String highString = SunshineWeatherUtils.formatTemperature(this,highInCelcius);
        mHighTemperatureView.setText(highString);

        double lowInCelcius = data.getDouble(INDEX_WEATHER_MIN_TEMP);
        String lowString = SunshineWeatherUtils.formatTemperature(this,lowInCelcius);
        mLowTemperatureView.setText(lowString);

        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity,humidity);
        mHumidityView.setText(humidityString);

        float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = SunshineWeatherUtils.getFormattedWind(this,windSpeed,windDirection);
        mWindView.setText(windString);

        float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);
        String pressureString = getString(R.string.format_pressure,pressure);
        mPressureView.setText(pressureString);

        mForecastSummary = String.format("%s - %s - %s/%s", dateText, description, highString, lowString);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }
}