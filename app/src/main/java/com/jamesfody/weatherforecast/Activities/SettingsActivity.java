package com.jamesfody.weatherforecast.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jamesfody.weatherforecast.Objects.Weather;
import com.jamesfody.weatherforecast.R;
import com.jamesfody.weatherforecast.Utils.GeoCoderUtils;
import com.jamesfody.weatherforecast.Utils.NetworkUtils;

import java.util.ArrayList;

/**
 * Created by Jim on 12/5/2017.
 *
 */

public class SettingsActivity extends Activity {

    TextView temperatureUnit;
    TextView windSpeedUnit;
    int unitOfMeasureWind = -1;
    int unitOfMeasureTemp = -1;
    int unitOfMeasureInterval = -1;
    ArrayList<Weather> mWeatherData = new ArrayList<>();
    ArrayList<String> mCityNames = new ArrayList<>();
    Intent mainActivityIntent;
    String mCaller = "";
    Bundle mExtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        if(android.os.Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getColor(R.color.add_city_color));
        }

        mainActivityIntent = new Intent(SettingsActivity.this, MainActivity.class);

        Intent intent = getIntent();
        mExtras = intent.getExtras();

        if(mExtras != null) {
            mWeatherData.clear();
            mCityNames.clear();
            mWeatherData = mExtras.getParcelableArrayList("weather_data");
            mCityNames.addAll(mExtras.getStringArrayList("city_names"));
            mCaller = mExtras.getString("caller");
            GeoCoderUtils.saveCityListToFile(mCityNames);
        }

        temperatureUnit = findViewById(R.id.temperature_unit_tv);
        windSpeedUnit = findViewById(R.id.wind_speed_unit_tv);

        // Set text based on set unit of measure in Weather
        if(Weather.getmTemperatureUnit().equals("imperial")){
            temperatureUnit.setText(getResources().getString(R.string.temp_units));
            unitOfMeasureTemp = 0;
        }
        else{
            temperatureUnit.setText(getResources().getString(R.string.celsius));
            unitOfMeasureTemp = 1;
        }

        // Set text based on set unit of measure in Weather
        if(Weather.getmWindSpeedUnit().equals("imperial")){
            windSpeedUnit.setText(getResources().getString(R.string.mph));
            unitOfMeasureWind = 0;
        }
        else{
            windSpeedUnit.setText(getResources().getString(R.string.metersecond));
            unitOfMeasureWind = 1;
        }
    }

    public Intent packExtras(Intent i){
        i.putParcelableArrayListExtra("weather_data", mWeatherData);
        i.putStringArrayListExtra("city_names", mCityNames);
        i.putExtra("caller", "settings_activity");
        return i;
    }

    public void selectView(View view){
        switch(view.getId()){
            case (R.id.temperature_ll):
                String[] temperatureUnits = {(char) 0x00B0 + "F", (char) 0x00B0 + "C"};
                String dialogTitle = "Temperature unit";
                dialog(temperatureUnits, unitOfMeasureTemp, dialogTitle);
                break;
            case (R.id.wind_speed_ll):
                String[] windSpeedUnits = {"MPH", "m/s"};
                dialogTitle = "Wind speed unit";
                dialog(windSpeedUnits, unitOfMeasureWind, dialogTitle);
                break;
            case (R.id.update_interval_ll):
                String[] intervalUnits = {"Manual", "Every hour", "Every 3 hr", "Every 6 hr", "Every 12 hr"};
                dialogTitle = "Update interval";
                dialog(intervalUnits, unitOfMeasureInterval, dialogTitle);
                break;
            case (R.id.edit_city_list_tv):
                Intent reorderCityIntent = new Intent(SettingsActivity.this, ReorderCityActivity.class);
                startActivity(packExtras(reorderCityIntent));
                break;
        }
    }

    void dialog(final String[] units, int unitOfMeasure, String title) {

        final String PREFERENCES = "preferences" ;
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(Html.fromHtml("<font color='#000'><b>" + title + "</b></font>"));
        builder.setSingleChoiceItems(units, unitOfMeasure, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (units[item].equals("MPH")) {
                    windSpeedUnit.setText(getResources().getString(R.string.mph));
                    editor.putString("wind_speed_unit", "imperial");
                    Weather.setmWindSpeedUnit("imperial");
                }
                else if (units[item].equals("m/s")){
                    windSpeedUnit.setText(getResources().getString(R.string.metersecond));
                    editor.putString("wind_speed_unit", "metric");
                    Weather.setmWindSpeedUnit("metric");
                }
                else if (units[item].equals((char) 0x00B0 + "F")){
                    temperatureUnit.setText(getResources().getString(R.string.temp_units));
                    editor.putString("temperature_unit", "imperial");
                    Weather.setmTemperatureUnits("imperial");
                }
                else if (units[item].equals((char) 0x00B0 + "C")){
                    temperatureUnit.setText(getResources().getString(R.string.celsius));
                    editor.putString("temperature_unit", "metric");
                    Weather.setmTemperatureUnits("metric");
                }
                else{

                }
                editor.apply();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int cancel) {
                finish();
            } });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void backToMain(View v){
        switch(v.getId()){
            case (R.id.settings_back_btn):
                Intent backIntent = new Intent (SettingsActivity.this, MainActivity.class);
                startActivity(packExtras(backIntent));
                break;

        }
    }
}
