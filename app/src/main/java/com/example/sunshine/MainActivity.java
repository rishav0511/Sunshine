package com.example.sunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.utilities.NetworkUtils;
import com.example.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler, LoaderManager.LoaderCallbacks<String []> {
    RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    TextView mErrorMessageTextView;
    ProgressBar mLoadingBar;
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
        mForecastAdapter=new ForecastAdapter(this);
        mRecyclerView.setAdapter(mForecastAdapter);

        mErrorMessageTextView=(TextView)findViewById(R.id.error_loading_data);
        mLoadingBar=(ProgressBar)findViewById(R.id.pb_loading_indicator);

        /*
         * From MainActivity, we have implemented the LoaderCallbacks interface with the type of
         * String array. (implements LoaderCallbacks<String[]>) The variable callback is passed
         * to the call to initLoader below. This means that whenever the loaderManager has
         * something to notify us of, it will do so through this callback.
         */
        LoaderManager.LoaderCallbacks<String[]> callback=MainActivity.this;
        /*
         * The second parameter of the initLoader method below is a Bundle. Optionally, you can
         * pass a Bundle to initLoader that you can then access from within the onCreateLoader
         * callback. In our case, we don't actually use the Bundle, but it's here in case we wanted
         * to.
         */
        Bundle bundleForLoader=null;
        getLoaderManager().initLoader(LOADER_ID,bundleForLoader,callback);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            /* This String array will hold and help cache our weather data */
            String[] mWeatherData=null;

            @Override
            protected void onStartLoading() {
                if(mWeatherData!=null){
                    deliverResult(mWeatherData);
                } else {
                    mLoadingBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                String locationQuery=SunshinePreferences.getPreferredWeatherLocation(MainActivity.this);
                URL weatherURL = NetworkUtils.buildUrl(locationQuery);
                try {
                    String weatherResults=NetworkUtils.getResponseFromHttpUrl(weatherURL);
                    String [] simpleJsonWeatherData=OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this,weatherResults);
                    return simpleJsonWeatherData;
                } catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
            public void deliverResult(String[] data){
                mWeatherData=data;
                super.deliverResult(data);
            }
        };
    }

    // calling showWeatherData() or showErrorMessage()
    // depending on data fetched
    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mLoadingBar.setVisibility(View.INVISIBLE);
        mForecastAdapter.setWeatherData(data);
        if(null==data){
            showErrorMessage();
        } else {
            showWeatherDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemSelected = item.getItemId();
        if(itemSelected==R.id.action_refresh){
            invalidateData();
            getLoaderManager().initLoader(LOADER_ID,null,this);
            return true;
        }
        if(itemSelected==R.id.action_map){
            openLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    public void invalidateData(){
        mForecastAdapter.setWeatherData(null);
    }

    private void openLocationInMap(){
        String address="1600 Ampitheatre Parkway, CA";
        Uri geoLocation = Uri.parse("geo:0,0?q="+address);
        Intent intent =new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        } else {
            Log.d(TAG,"Couldn't call "+geoLocation.toString()+", no receiving apps installed!");
        }
    }

    //show weather data if data fetched
    public void showWeatherDataView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    // show error message if no data fetched
    public void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String weatherForDay) {
        Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,weatherForDay);
        startActivity(intent);
        Toast.makeText(this,"Clicked", Toast.LENGTH_SHORT).show();
    }

}
