package com.example.sunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.utilities.NetworkUtils;
import com.example.sunshine.utilities.OpenWeatherJsonUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView mWeatherTextView;
    TextView mErrorMessageTextView;
    ProgressBar mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);
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
        int itemselected = item.getItemId();
        if(itemselected==R.id.action_refresh){
            loadWeatherData();
            Toast.makeText(this,"Refreshed", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadWeatherData(){

        showWeatherDataView();
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new sunShineAsyncTask().execute(location);
    }
    public void showWeatherDataView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mWeatherTextView.setVisibility(View.VISIBLE);
    }
    public void showErrorMessage(){
        mWeatherTextView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
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

        @Override
        protected void onPostExecute(String[] weatherData) {
            mLoadingBar.setVisibility(View.INVISIBLE);
           if(weatherData!=null){
               showWeatherDataView();
               for(String weatherstring:weatherData)
               {
                   mWeatherTextView.append((weatherstring)+"\n\n\n");
               }
           } else {
               showErrorMessage();
           }
        }
    }
}
