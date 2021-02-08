package com.example.sunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler {
    RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    TextView mErrorMessageTextView;
    ProgressBar mLoadingBar;
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
        loadWeatherData();
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
            loadWeatherData();
            mForecastAdapter.setWeatherData(null);
            return true;
        }
        if(itemSelected==R.id.action_map){
            openLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    public void loadWeatherData(){

        showWeatherDataView();
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new sunShineAsyncTask().execute(location);
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

    public class sunShineAsyncTask extends AsyncTask<String,Void,String[]>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... strings) {
            String weatherResults=null;
            URL weatherURL = NetworkUtils.buildUrl(strings[0]);
            try {
                weatherResults=NetworkUtils.getResponseFromHttpUrl(weatherURL);
                String [] simpleJsonWeatherData = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this,weatherResults);
                return simpleJsonWeatherData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        // calling showWeatherData() or showErrorMessage()
        // depending on data fetched
        @Override
        protected void onPostExecute(String[] weatherData) {
            mLoadingBar.setVisibility(View.INVISIBLE);
           if(weatherData!=null){
               showWeatherDataView();
               mForecastAdapter.setWeatherData(weatherData);
           } else {
               showErrorMessage();
           }
        }
    }
}
