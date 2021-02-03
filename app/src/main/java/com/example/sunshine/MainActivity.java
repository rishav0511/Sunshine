package com.example.sunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView mWeatherTextView;
    TextView mErrorMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);
        mErrorMessageTextView=(TextView)findViewById(R.id.error_loading_data);
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
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new sunShineAsyncTask().execute(location);
    }
    public void showWeatherDataView(){

    }
    public void showErrorMessage(){

    }
    public class sunShineAsyncTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            String weatherResults=null;
            URL weatherURL = NetworkUtils.buildUrl(strings[0]);
            try {
                weatherResults=NetworkUtils.getResponseFromHttpUrl(weatherURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return weatherResults;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s!=null && !s.equals("")){
                mWeatherTextView.setText(s);
            }
        }
    }
}
