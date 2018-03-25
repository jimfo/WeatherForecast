package com.jamesfody.weatherforecast.Utils;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.jamesfody.weatherforecast.Database.DBHelper;
import com.jamesfody.weatherforecast.Objects.StaticContext;
import com.jamesfody.weatherforecast.Objects.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Jim on 11/26/2017.
 *
 */

public final class NetworkUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    /** Key for OpenWeatherMap API*/
    private static String REQUEST_URL_KEY = "&units=Imperial&appid=b5360cabf9d8535045649e71e3f2764f";

    private DBHelper mydb;

    /**
     * Create a private constructor because no one should ever create a {@link NetworkUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name NetworkUtils (and an object instance of NetworkUtils is not needed).
     */
    private NetworkUtils(){}

    /**
     * Query the OpenWeatherMap API dataset and return an {@link Weather} object to represent a single earthquake.
     */
    public static ArrayList<Weather> fetchDailyWeatherData(String request) {
        
        // Create URL object
        URL todayURL = createUrl(request);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(todayURL);
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        return extractDailyWeather(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {

        URL url = null;
        try {
            url = new URL(stringUrl);
        }
        catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // Check if url is null
        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else{
                Log.v(LOG_TAG, "Error Response Code " + urlConnection.getResponseCode());
            }
        }
        catch (IOException e) {
            Log.v(LOG_TAG, "Error receiving JSon " + e);
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<Weather> extractDailyWeather(String response){

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(response)) {
            return null;
        }

        Weather forecast;
        ArrayList<Weather> weatherForecastList = new ArrayList<>();

        // Try to parse the JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Parse the response given by the JSON_RESPONSE string and
            // build up a list of TODAY Weather objects with the corresponding data.
            JSONObject jsonObj = new JSONObject(response);

            if(!jsonObj.get("cod").equals("404")) {
                if (jsonObj.has("list")) {
                    JSONObject cityName = jsonObj.getJSONObject("city");///
                    JSONArray fiveDayList = jsonObj.getJSONArray("list");

                    for (int idx = 0; idx < fiveDayList.length(); idx += 8) {

                        JSONObject jsonWeather = fiveDayList.getJSONObject(idx);
                        forecast = getWeatherObjects(jsonWeather);
                        forecast.setCityName(cityName.getString("name"));///
                        weatherForecastList.add(forecast);
                    }
                } else {
                    forecast = getWeatherObjects(jsonObj);

                    forecast.setCityName(jsonObj.getString("name"));

                    JSONObject sysInfo = jsonObj.getJSONObject("sys");
                    forecast.setmSunrise(sysInfo.getLong("sunrise") * 1000L);
                    forecast.setmSunset(sysInfo.getLong("sunset") * 1000L);

                    // Get latitude and longitude from JSON coord object
                    JSONObject jsonCoord = jsonObj.getJSONObject("coord");
                    forecast.setmLatitude(jsonCoord.getDouble("lat"));
                    forecast.setmLongitude(jsonCoord.getDouble("lon"));

                    weatherForecastList.add(forecast);
                }
            }
            else{
                Toast.makeText(StaticContext.getAppContext(), "City Not Found", Toast.LENGTH_LONG).show();
                //return null;
            }

        }
        catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return weatherForecastList;
    }

    private static Weather getWeatherObjects(JSONObject jObj){

        Weather w = new Weather();

        try {

            // Get weather and icon from JSON weather array
            JSONArray jsonArray = jObj.getJSONArray("weather");
            JSONObject jsonWeather = jsonArray.getJSONObject(0);
            String description = jsonWeather.getString("description");
            String icon = jsonWeather.getString("icon");

            // Get temperature, max and min, and humidity from JSON main object
            JSONObject jsonMain = jObj.getJSONObject("main");
            double temperature  = jsonMain.getDouble("temp");
            double humidity     = jsonMain.getDouble("humidity");
            double highTemp     = jsonMain.getDouble("temp_max");
            double lowTemp      = jsonMain.getDouble("temp_min");

            // Get wind speed and direction from JSON wind object
            JSONObject jsonWind = jObj.getJSONObject("wind");
            double windSpeed = jsonWind.getDouble("speed");
            double windDirection = 0.0;
            if( jsonWind.has("deg")){
                windDirection = jsonWind.getDouble("deg");
            }

            long unixTime = jObj.getLong("dt");

            w = new Weather(description, temperature, highTemp, lowTemp, humidity,
                    windDirection, windSpeed, unixTime);
        }
        catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("NetworkUtils", "Problem parsing the earthquake JSON results", e);
        }
        return w;
    }

    public static String[] createRequestURLs(ArrayList<String> cities){

        ArrayList<String> allRequestURLs = new ArrayList<>();
        allRequestURLs.addAll(createDailyRequestURLs(cities));
        allRequestURLs.addAll(createWeeklyRequestURLs(cities));
        String[] cityArr = new String[allRequestURLs.size()];
        cityArr = allRequestURLs.toArray(cityArr);

        return cityArr;
    }

    private static ArrayList<String> createDailyRequestURLs(ArrayList<String> cities){

        /** URL to query the OpenWeatherMap API dataset for daily weather information */
        String DAILY_REQUEST_URL = "http://api.openweathermap.org/data/2.5/weather?q=";

        ArrayList<String> dailyRequestURLs = new ArrayList<>();

        for(int idx = 0; idx < cities.size(); idx++){
            //Log.v(LOG_TAG, DAILY_REQUEST_URL + cities.get(idx) + REQUEST_URL_KEY + "\n");
            dailyRequestURLs.add(DAILY_REQUEST_URL + cities.get(idx) + REQUEST_URL_KEY);
        }

        return dailyRequestURLs;
    }

    private static ArrayList<String> createWeeklyRequestURLs(ArrayList<String> cities){

        /** URL to query the OpenWeatherMap API dataset for weekly weather information */
        String WEEKLY_REQUEST_URL = "http://api.openweathermap.org/data/2.5/forecast?q=";

        ArrayList<String> weeklyRequestURLs = new ArrayList<>();

        for(int idx = 0; idx < cities.size(); idx++){

            weeklyRequestURLs.add(WEEKLY_REQUEST_URL + cities.get(idx) + REQUEST_URL_KEY);
        }

        return weeklyRequestURLs;
    }

//    static String formatTemperature(double t){
//        String units = "F";
//
//        if(Weather.getmTemperatureUnit().equals("metric")){
//            units = "C";
//            t = (t - 32) * (5/9);
//        }
//
//        return String.format(Locale.ENGLISH, "%02.0f", t) + (char) 0x00B0 + units;
//    }



    public static ArrayList<Weather> getArrayList(Bundle extras){
        ArrayList<Weather> data = new ArrayList<>();

        // Verify extras exists and contains the specified key
        if (extras != null) {
            if (extras.containsKey("weather_data")) {
                data = extras.getParcelableArrayList("weather_data");
            }
        }

        return data;
    }




    public static boolean isNumeric(String str) {
        // Typical function to determine if string is numeric or not
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
