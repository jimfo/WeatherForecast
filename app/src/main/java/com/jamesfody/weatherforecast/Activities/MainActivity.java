package com.jamesfody.weatherforecast.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jamesfody.weatherforecast.Database.DBHelper;
import com.jamesfody.weatherforecast.Fragments.FiveDayFragment;
import com.jamesfody.weatherforecast.Fragments.TodayFragment;
import com.jamesfody.weatherforecast.Objects.StaticContext;
import com.jamesfody.weatherforecast.Objects.Weather;
import com.jamesfody.weatherforecast.R;
import com.jamesfody.weatherforecast.Utils.FormatUtils;
import com.jamesfody.weatherforecast.Utils.GeoCoderUtils;
import com.jamesfody.weatherforecast.Utils.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jim on 11/28/2017.
 *
 * Sets up the UI for display and function
 */

public class MainActivity extends AppCompatActivity{

    /** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String PREFERENCES = "preferences";
    SharedPreferences sharedpreferences;

    /** Theme colors for UI */
    private static String mLLColor = "#33B5E5";
    private static String mTodayColor = "#1A9CCB";
    private static String mFiveDayColor = "#1DADE2";
    private static ArrayList<String> mCityNames = new ArrayList<>();
    private static ArrayList<Weather> mWeatherData = new ArrayList<>();
    private static ArrayList<Weather> mTodayWeather = new ArrayList<>();
    private static int mCurrentCity = 0;
    private static int mNumberOfCities;
    private String mRefreshTime;
    private DBHelper mydb;

    LinearLayout mainLL, resetLL;
    ImageView listIconIV, plusIconIV, settingsIcon, nextIconIV, prevIconIV ,weatherIconIV;
    TextView currentCityTV, cityCountTV, currentTempTV, weatherDescriptionTV, hiloTemperatureTV;
    TextView humidityTV, windSpeedTV, todayTV, fiveDayTV, updateTimeTV;
    Bundle todayBundle;
    Bundle forecastBundle;
    ArrayList<Weather> allCities = new ArrayList<>();
    ArrayList<Weather> activeCity = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DBHelper(this);
        sharedpreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        Weather.setmTemperatureUnits(sharedpreferences.getString("temperature_unit", "imperial"));
        Weather.setmWindSpeedUnit(sharedpreferences.getString("wind_speed_unit", "imperial"));

        // One time call to populate db
//        try {
//            populateDB();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        // TODO clean this up
        if(extras != null) {
            if (extras.containsKey("position")) {
                mCurrentCity = extras.getInt("position");
            }
            if(extras.containsKey("city_names")){
                mCityNames.clear();
                mCityNames.addAll(extras.getStringArrayList("city_names"));
            }
            if(extras.containsKey("weather_data")){
                mWeatherData.clear();
                mWeatherData = extras.getParcelableArrayList("weather_data");
            }
//            if(extras.containsKey("position")){
//                mCurrentCity = extras.getInt("position");
//                displayActiveCity();
//            }
        }

//        cityNames.add("Hatboro");
//        cityNames.add("Portland");
//        cityNames.add("Chicago");
//        NetworkUtils.saveCityListToFile(new ArrayList<String>());
//        cityNames.addAll(NetworkUtils.getCityListFromFile());
//        for(int idx = 0; idx < cityNames.size(); idx++){
//            Log.v(LOG_TAG, cityNames.get(idx)+"\n");
//        }

        // List of LinearLayouts that need theme changed at various times
        mainLL = findViewById(R.id.main_ll);
        resetLL = findViewById(R.id.resetbar_ll);

        // List of cities, add cities, and settings ImageViews
        listIconIV   = findViewById(R.id.list_icon);
        plusIconIV   = findViewById(R.id.plus_icon);
        settingsIcon = findViewById(R.id.settings_icon);

        // Previous and Next chevrons, current city name, and city count total
        prevIconIV    = findViewById(R.id.prev_icon);
        currentCityTV = findViewById(R.id.city);
        cityCountTV   = findViewById(R.id.city_count);
        nextIconIV    = findViewById(R.id.next_icon);

        // Weather icon, current temperature
        weatherIconIV = findViewById(R.id.weather_icon);
        currentTempTV = findViewById(R.id.current_temp);

        // Weather description, HiLo, and Real Feel
        weatherDescriptionTV = findViewById(R.id.weather_description);
        hiloTemperatureTV    = findViewById(R.id.hilo_temperature);

        // Humidity and wind direction
        humidityTV  = findViewById(R.id.humidity);
        windSpeedTV = findViewById(R.id.wind_speed);

        // Today and 5 Day TextViews
        todayTV   = findViewById(R.id.today_tv);
        fiveDayTV = findViewById(R.id.fiveDay_tv);

        // Update TextView and Refresh ImageView
        updateTimeTV = findViewById(R.id.updateTime);

        // Check if there is a connection to the internet
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If connected to internet get a list of all request urls and execute task
        if(networkInfo != null && networkInfo.isConnected()){

            File file = StaticContext.getAppContext().getFileStreamPath("city_list.txt");

            // First we need to check if cityNames is empty
            if(mCityNames.size() == 0 || mCityNames == null){

                // Next check to see if file exists
                if(!file.exists()){
                    // The file doesn't exist and we have no city names
                    // Now we create the file and go to addCityActivity
                    try {
                        file.createNewFile();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    goToAddCityActivity();
                }
                else if(GeoCoderUtils.isFileEmpty()){
                    // The file exists but is empty

                    goToAddCityActivity();
                }
                else{
                    // The file exists but is not empty
                    // Create new AsyncTask with contents of file

                    mCityNames.addAll(GeoCoderUtils.getCityListFromFile());

                    if(mCityNames.size() == 0){
                        goToAddCityActivity();
                    }
                    new WeatherAsyncTask().execute(NetworkUtils.createRequestURLs(GeoCoderUtils.getCityListFromFile()));
                }
            }
            else{
                // CityNames is not empty. During execution cityNames should have the most current
                // list of cities.
                new WeatherAsyncTask().execute(NetworkUtils.createRequestURLs(mCityNames));
            }
            mNumberOfCities = mCityNames.size();
        }
        else{

            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
        }

        listIconIV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent weatherLVIntent = new Intent(MainActivity.this, WeatherListActivity.class);
                mWeatherData.addAll(allCities.subList(0, mCityNames.size()));
                startActivity(packExtras(weatherLVIntent));
            }
        });
    }

    public void goToAddCityActivity(){
        Intent addCityIntent = new Intent (MainActivity.this, AddCityActivity.class);
        startActivity(packExtras(addCityIntent));
    }

    public Intent packExtras(Intent i){
        i.putParcelableArrayListExtra("weather_data", mWeatherData);
        i.putParcelableArrayListExtra("today_weather", mTodayWeather);
        i.putStringArrayListExtra("city_names", mCityNames);
        i.putExtra("caller", "reorder_city_activity");
        i.putExtra("refresh_time", mRefreshTime);
        return i;
    }

    public void topBarSelection(View v){

        switch (v.getId()){
            case (R.id.settings_icon):
                mWeatherData.clear();
                mWeatherData.addAll(allCities.subList(0, mCityNames.size()));
                Intent settingsIntent = new Intent (MainActivity.this, SettingsActivity.class);
                startActivity(packExtras(settingsIntent));
                break;
            case (R.id.plus_icon):
                goToAddCityActivity();
                break;
        }
    }

    /**
     *
     * @param v : the view that has been clicked
     */
    public void selectView(View v){
        // Actions for prev and next buttons

        switch(v.getId()){
            case R.id.next_icon:
                if(mCurrentCity == mCityNames.size()-1){
                    mCurrentCity = 0;
                }
                else{
                    mCurrentCity++;
                }
                break;
            case R.id.prev_icon:
                if(mCurrentCity == 0){
                    mCurrentCity = mCityNames.size()-1;
                }
                else{
                    mCurrentCity--;
                }
                break;
        }
        displayActiveCity();
        setThemeColors(allCities.get(mCurrentCity).getIsSunUp());
        setStatusBarBarTheme(allCities.get(mCurrentCity).getIsSunUp());
    }

    /**
     *
     * @param weather : contains all cities current weather and forecast
     */
    public void createCitySpecificArrayList(ArrayList<Weather> weather){
        // Extract current city's weather forecast

        activeCity.clear();

        int startIdx = (mCurrentCity * 5) + mCityNames.size();
        int endIdx = startIdx + 5;

        activeCity.add(weather.get(mCurrentCity));
        activeCity.addAll(weather.subList(startIdx, endIdx));
    }

    public void displayActiveCity(){

        createCitySpecificArrayList(allCities);
        updateUI(activeCity);
        updateTodayFragmentUI(activeCity);
        updateForecastFragmentUI(activeCity);

        Fragment frag = new TodayFragment();
        frag.setArguments(todayBundle);
        createFragment(frag);
    }
    /**
     *
     * @param view : the view that has been clicked for fragment creation
     */
    public void selectFragment(View view){
        // Creates the fragments for the fiveday or today section

        Fragment frag;

        if(view == findViewById(R.id.today_tv)){
            todayTV.setBackgroundColor(Color.parseColor(mTodayColor));
            fiveDayTV.setBackgroundColor(Color.parseColor(mFiveDayColor));
            frag = new TodayFragment();
            frag.setArguments(todayBundle);
        }
        else{
            fiveDayTV.setBackgroundColor(Color.parseColor(mTodayColor));
            todayTV.setBackgroundColor(Color.parseColor(mFiveDayColor));
            frag = new FiveDayFragment();
            frag.setArguments(forecastBundle);
        }

        createFragment(frag);
    }

    /**
     *
     * @param view : the view that was clicked
     */
    public void updateWeather(View view){
        if(view == findViewById(R.id.updateTime)){
            new WeatherAsyncTask().execute(NetworkUtils.createRequestURLs(mCityNames));
        }
    }

    private void createFragment(Fragment f){

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, f);
        fragmentTransaction.commit();
    }

    /**
     *
     * @param sunUp : boolean variable for night or day
     */
    public void setThemeColors(boolean sunUp){
        // Change the UI theme based on whether it is night or day

        if (sunUp){
            mLLColor = "#33B5E5";
            mTodayColor = "#D91a9ccb";
            mFiveDayColor = "#A61a9ccb";
        }
        else{
            mLLColor = "#0F5A70";
            mTodayColor = "#D90C485A";
            mFiveDayColor = "#A60C485A";
        }
    }

    public void setStatusBarBarTheme(boolean sunUp){

        if(android.os.Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if(sunUp) {
                window.setStatusBarColor(this.getColor(R.color.daytime_theme));
            }
            else{
                window.setStatusBarColor(this.getColor(R.color.evening_theme));
            }
        }
    }

    /**
     *
     * @param result : ArrayList of Weather objects
     */
    private void updateUI(ArrayList<Weather> result){
        // Update the local UI
        setThemeColors(result.get(0).getIsSunUp());

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Theme backgrounds
        mainLL.setBackgroundColor(Color.parseColor(mLLColor));
        fiveDayTV.setBackgroundColor(Color.parseColor(mFiveDayColor));
        todayTV.setBackgroundColor(Color.parseColor(mTodayColor));

        // Weather image and current temperature
        String currImgId = result.get(0).getmWeatherDescription();
        String cityCount = mCurrentCity + 1 + " / " + mNumberOfCities;
        String[] resID = {currImgId};
        int[] imgID = FormatUtils.getWeatherIconName(this, resID, result.get(0).getIsSunUp());
        weatherIconIV.setImageResource(imgID[0]);
        currentTempTV.setText(result.get(0).getmCurrentTemperature());

        // Current city displayed and count of cities in list
        currentCityTV.setText(result.get(0).getmCityName());
        cityCountTV.setText(cityCount);

        // Weather description and high / low temperature
        weatherDescriptionTV.setText(result.get(0).getmWeatherDescription());

        CharSequence hiloDisplay = TextUtils.concat(result.get(0).getHiLoTemperature(), FormatUtils.windChillAndHeatIndex(result.get(0)));
        hiloTemperatureTV.setText(hiloDisplay);

        // TODO Add different windsock icons based on wind speed
        // Humidity and wind speed
        humidityTV.setText(getString(R.string.percent_humidity, result.get(0).getmHumidity()));
        windSpeedTV.setText(getString(R.string.wind_speed,result.get(0).getWindSpeedAndDirection()));

        // Time weather was updated
        mRefreshTime = FormatUtils.formatTime(System.currentTimeMillis());
        updateTimeTV.setVisibility(View.VISIBLE);
        updateTimeTV.setText(getString(R.string.updated, mRefreshTime));
    }

    /**
     *
     * @param result : ArrayList of Weather objects
     */
    public void updateTodayFragmentUI(ArrayList<Weather> result){
        // Information required for today fragment

        todayBundle = new Bundle();

        todayBundle.putString("sunrise", FormatUtils.formatTime(result.get(0).getmSunrise()));
        todayBundle.putString("sunset", FormatUtils.formatTime(result.get(0).getmSunset()));

        todayBundle.putString("dayDesc", result.get(0).getmWeatherDescription());
        todayBundle.putString("nightDesc", result.get(0).getmWeatherDescription());

        todayBundle.putString("todayTheme", mTodayColor);
        todayBundle.putBoolean("sunUp", result.get(0).getIsSunUp());
    }

    /**
     *
     * @param result : ArrayList of Weather objects
     */
    public void updateForecastFragmentUI(ArrayList<Weather> result){
        // Information required for five day fragment
        // TODO double check the information is from the correct hour

        forecastBundle = new Bundle();

        forecastBundle.putString("highOne", result.get(1).getmHighTemperature().toString());
        forecastBundle.putLong("dateOne", result.get(1).getmUnixTime());
        forecastBundle.putString("iconOne", result.get(1).getmWeatherDescription());

        forecastBundle.putString("highTwo", result.get(2).getmHighTemperature().toString());
        forecastBundle.putLong("dateTwo", result.get(2).getmUnixTime());
        forecastBundle.putString("iconTwo", result.get(2).getmWeatherDescription());

        forecastBundle.putString("highThree", result.get(3).getmHighTemperature().toString());
        forecastBundle.putLong("dateThree", result.get(3).getmUnixTime());
        forecastBundle.putString("iconThree", result.get(3).getmWeatherDescription());

        forecastBundle.putString("highFour", result.get(4).getmHighTemperature().toString());
        forecastBundle.putLong("dateFour", result.get(4).getmUnixTime());
        forecastBundle.putString("iconFour", result.get(4).getmWeatherDescription());

        forecastBundle.putString("highFive", result.get(5).getmHighTemperature().toString());
        forecastBundle.putLong("dateFive", result.get(5).getmUnixTime());
        forecastBundle.putString("iconFive", result.get(5).getmWeatherDescription());

        forecastBundle.putBoolean("sunUp", result.get(0).getIsSunUp());
        forecastBundle.putString("fiveDayTheme", mTodayColor);
    }

    private class WeatherAsyncTask extends WeatherAsyncTaskActivity {

        protected void onPreExecute(){

        }

        protected void onPostExecute(ArrayList<Weather> result) {
            super.onPostExecute(result);
            // Actions to take when data is returned

            allCities.clear();
            allCities.addAll(result);

            // Set theme based on night or day first
            setThemeColors(result.get(0).getIsSunUp());
            setStatusBarBarTheme(result.get(0).getIsSunUp());
            displayActiveCity();
        }
    }
}












