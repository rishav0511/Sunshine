package com.example.sunshine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    // array of string to store data
    private String[] mWeatherData;
    public ForecastAdapter(){

    }
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mWeatherTextView;

        public ForecastAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mWeatherTextView=itemView.findViewById(R.id.tv_weather_data);
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        View view=LayoutInflater.from(context).inflate(R.layout.forecast_list_item,parent,false);
        return new ForecastAdapterViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder holder, int position) {
        String weatherForThisDay=mWeatherData[position];
        holder.mWeatherTextView.setText(weatherForThisDay);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(null==mWeatherData){
            return 0;
        }
        return mWeatherData.length;
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param weatherData String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public void setWeatherData(String[] weatherData){
        mWeatherData=weatherData;
        notifyDataSetChanged();
    }
}
