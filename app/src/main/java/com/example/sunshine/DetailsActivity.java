package com.example.sunshine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {
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
}