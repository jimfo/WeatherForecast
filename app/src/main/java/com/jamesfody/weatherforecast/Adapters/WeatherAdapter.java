package com.jamesfody.weatherforecast.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jamesfody.weatherforecast.Objects.Weather;
import com.jamesfody.weatherforecast.R;
import com.jamesfody.weatherforecast.Utils.FormatUtils;
import com.jamesfody.weatherforecast.Utils.GeoCoderUtils;

import java.util.ArrayList;

/**
 * Created by Jim on 12/3/2017.
 *
 */

public class WeatherAdapter extends ArrayAdapter<Weather> {

    public WeatherAdapter(Context context, ArrayList<Weather> weather) {
        super(context, 0, weather);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        Weather currentWeather = getItem(position);

        double latitude = currentWeather.getmLatitude();
        double longitude = currentWeather.getmLongitude();

        LinearLayout weatherAdapterLL = listItemView.findViewById(R.id.list_item_ll);
        if(currentWeather.getIsSunUp()){
            weatherAdapterLL.setBackgroundColor(Color.parseColor("#33B5E5"));
        }
        else{
            weatherAdapterLL.setBackgroundColor(Color.parseColor("#0F5A70"));
        }

        // Find the TextView in the list_item.xml layout with the ID city_tv.
        TextView cityNameTV = listItemView.findViewById(R.id.city_tv);
        cityNameTV.setText(currentWeather.getmCityName());

        // Find the ImageView in the list_item.xml with the ID weather_image_iv
        ImageView weatherImageTV = listItemView.findViewById(R.id.weather_image_iv);
        String[] resID = {currentWeather.getmWeatherDescription()};
        int[] imgID = FormatUtils.getWeatherIconName(this.getContext(), resID, currentWeather.getIsSunUp());
        weatherImageTV.setImageResource(imgID[0]);

        // Find the ImageView in the list_item.xml with the ID current_temperature_tv
        TextView currentTemperatureTV = listItemView.findViewById(R.id.current_temperature_tv);
        currentTemperatureTV.setText(currentWeather.getmCurrentTemperature());

        // Find the ImageView in the list_item.xml with the ID temperature_high_tv
        TextView highTemperatureTV = listItemView.findViewById(R.id.temperature_high_tv);
        highTemperatureTV.setText(currentWeather.getmHighTemperature());

        // Find the ImageView in the list_item.xml with the ID temperature_low_tv
        TextView lowTemperatureTV = listItemView.findViewById(R.id.temperature_low_tv);
        lowTemperatureTV.setText(currentWeather.getmLowTemperature());

        // Find the ImageView in the list_item.xml with the ID weather_description_tv
        TextView weatherDescriptionTV = listItemView.findViewById(R.id.weather_description_tv);
        weatherDescriptionTV.setText(currentWeather.getmWeatherDescription());

        TextView countryTV = listItemView.findViewById(R.id.country_tv);
        countryTV.setText(GeoCoderUtils.getStateAndCountry(latitude,longitude));

        // Return the whole list item layout.
        return listItemView;
    }
}
