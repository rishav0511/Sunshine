package com.example.sunshine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private TextView mWeatherDetails;
    private String mForecast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mWeatherDetails=(TextView)findViewById(R.id.weather_details);
        Intent intent = getIntent();
        if(intent!=null){
            if(intent.hasExtra(Intent.EXTRA_TEXT)){
                mForecast=intent.getStringExtra(Intent.EXTRA_TEXT);
                mWeatherDetails.setText(mForecast);
            }
        }
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
}