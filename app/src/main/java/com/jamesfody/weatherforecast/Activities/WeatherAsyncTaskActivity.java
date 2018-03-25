package com.jamesfody.weatherforecast.Activities;

import android.os.AsyncTask;

import com.jamesfody.weatherforecast.Objects.Weather;
import com.jamesfody.weatherforecast.Utils.NetworkUtils;

import java.util.ArrayList;

/**
 * Created by Jim on 12/9/2017.
 *
 */

public abstract class WeatherAsyncTaskActivity extends AsyncTask<String, Void, ArrayList<Weather>> {

    /** Tag for the log messages */
    public static final String LOG_TAG = WeatherAsyncTaskActivity.class.getSimpleName();

    @Override
    final protected ArrayList<Weather>  doInBackground(String... names) {
        // Create Weather objects of todays weather and forecast weather

        ArrayList<Weather> result = new ArrayList<>();

        for(int idx = 0; idx < names.length; idx++){

                result.addAll(NetworkUtils.fetchDailyWeatherData(names[idx]));
        }

        return result;
    }
}
