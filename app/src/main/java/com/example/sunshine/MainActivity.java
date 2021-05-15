package com.example.sunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.AsyncTaskLoader;
import android.content.ContentProvider;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.data.WeatherContract;
import com.example.sunshine.sync.SunshineSyncUtils;
import com.example.sunshine.utilities.FakeDataUtils;
import com.example.sunshine.utilities.NetworkUtils;
import com.example.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {
    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * weather data.
     */
    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };


    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private ProgressBar mLoadingBar;
    private int mPosition = RecyclerView.NO_POSITION;
    public static final int LOADER_ID=0;
    private static String TAG= MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView=(RecyclerView)findViewById(R.id.recycler_view_forecast);
        //setting linearlayoutmanager for recycler View
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        // adapter for recycler view
        mForecastAdapter=new ForecastAdapter(this, this);
        mRecyclerView.setAdapter(mForecastAdapter);

        mLoadingBar=(ProgressBar)findViewById(R.id.pb_loading_indicator);

        showLoading();
        getLoaderManager().initLoader(LOADER_ID,null,this);
        SunshineSyncUtils.startImmediateSync(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        switch (id){
            case LOADER_ID:
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                Log.v(TAG,"print ho rha"+forecastQueryUri);
                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();
                return new CursorLoader(this,
                        forecastQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: "+LOADER_ID);
        }
    }

    // calling showWeatherData() or showErrorMessage()
    // depending on data fetched
//    * NOTE: There is one small bug in this code. If no data is present in the cursor do to an
//     * initial load being performed with no access to internet, the loading indicator will show
//     * indefinitely, until data is present from the ContentProvider. This will be fixed in a
//     * future version of the course.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        if(mPosition == RecyclerView.NO_POSITION)
            mPosition=0;
        mRecyclerView.smoothScrollToPosition(mPosition);
//        while(data.moveToNext())
//        {
//            Log.v(TAG,"print ho rha");
//        }
        if(data.getCount()!=0)
            showWeatherDataView();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemSelected = item.getItemId();
        if(itemSelected==R.id.action_map){
            openLocationInMap();
            return true;
        }
        if(itemSelected==R.id.action_settings){
            Intent intent =new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void openLocationInMap(){

        String address=SunshinePreferences.getPreferredWeatherLocation(this);
        Uri geoLocation = Uri.parse("geo:0,0?q="+address);
        Intent intent =new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
//        if(intent.resolveActivity(getPackageManager())!=null){
//            startActivity(intent);
//        } else {
//            Log.d(TAG,"Couldn't call "+geoLocation.toString()+", no receiving apps installed!");
//            Toast.makeText(
//                    this,
//                    "No application can handle the link",
//                    Toast.LENGTH_SHORT
//            ).show();
//        }
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e){
            e.printStackTrace();
        }
    }

    //show weather data if data fetched
    public void showWeatherDataView(){
        mLoadingBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the loading indicator visible and hide the weather View and error
     * message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showLoading()
    {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingBar.setVisibility(View.VISIBLE);
    }
    @Override
    public void onClick(long date) {
        Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
        Uri uriForDateClicked = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date);
        intent.setData(uriForDateClicked);
        startActivity(intent);
        Toast.makeText(this,"Clicked", Toast.LENGTH_SHORT).show();
    }
}
