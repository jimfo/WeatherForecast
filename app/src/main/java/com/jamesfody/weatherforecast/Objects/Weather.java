package com.jamesfody.weatherforecast.Objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;

import com.jamesfody.weatherforecast.Utils.FormatUtils;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by Jim on 11/26/2017.
 *
 */

public class Weather implements Parcelable{

    /** Tag for the log messages */
    public static final String LOG_TAG = Weather.class.getSimpleName();


    private String mCityName;
    private String mWeatherDescription;
    private double mCurrentTemperature;
    private double mHighTemperature;
    private double mLowTemperature;
    private double mLatitude;
    private double mLongitude;
    private double mHumidity;
    private double mWindDirection;
    private double mWindSpeed;
    private long mUnixTime;
    private long mSunrise;
    private long mSunset;
    private boolean mIsDay;
    private DecimalFormat df = new DecimalFormat("#");
    private static String mTemperatureUnit = "imperial";
    private static String mWindSpeedUnit = "imperial";

    public Weather(){
    }

    public Weather(String desc, double currTemp, double hiTemp, double lowTemp, double hum, double wDir,
                   double wSpeed, long ut){
        mWeatherDescription = desc;
        mCurrentTemperature = currTemp;
        mHighTemperature = hiTemp;
        mLowTemperature = lowTemp;
        mHumidity = hum;
        mWindDirection = wDir;
        mWindSpeed = wSpeed;
        mUnixTime = ut;
    }

    public Weather (String name, String desc, double currentTemp, double hiTemp, double lowTemp){
        mCityName = name;
        mWeatherDescription = desc;
        mCurrentTemperature = currentTemp;
        mHighTemperature = hiTemp;
        mLowTemperature = lowTemp;
    }

    public String getmCityName() {
        return mCityName;
    }

    public String getmWeatherDescription() {
        return mWeatherDescription;
    }

    public SpannableString getmCurrentTemperature() {
        return convertTemperature(mCurrentTemperature);
    }

    public SpannableString getmHighTemperature() {
        return convertTemperature(mHighTemperature);
    }

    public double getmLongitude(){
        return mLongitude;
    }

    public double getmLatitude(){
        return mLatitude;
    }

    public SpannableString getmLowTemperature() {
        return convertTemperature(mLowTemperature);
    }

    public String getmHumidity() {
        return String.format(Locale.ENGLISH, "%02.0f", mHumidity) + "%";
    }

    public double getmWindDirection() {
        return mWindDirection;
    }

    public double getmWindSpeed() {
        return mWindSpeed;
    }

    public long getmUnixTime() {
        return mUnixTime;
    }

    public long getmSunrise(){
        return mSunrise;
    }

    public long getmSunset(){
        return mSunset;
    }

    public boolean getmIsDay(){
        return mIsDay;
    }

    public static String getmTemperatureUnit(){
        return mTemperatureUnit;
    }

    public static String getmWindSpeedUnit(){
        return mWindSpeedUnit;
    }

    public void setCityName(String city){
        mCityName = city;
    }

    public void setmIsDay(boolean b){
        mIsDay = b;
    }

    public void setmSunset(long l){
        mSunset = l;
    }

    public void setmSunrise(long l){
        mSunrise = l;
    }

    public void setmLatitude(double lat){
        mLatitude = lat;
    }

    public void setmLongitude(double lng){
        mLongitude = lng;
    }

    public void setmCurrentTemperature(String temperatureUnit){
        mTemperatureUnit = temperatureUnit;
    }

    public static void setmTemperatureUnits(String temperatureUnit){
        mTemperatureUnit = temperatureUnit;
    }

    public static void setmWindSpeedUnit(String windSpeedUnit){
        mWindSpeedUnit = windSpeedUnit;
    }


    public boolean getIsSunUp(){

        Long tsLong = System.currentTimeMillis();

        if (tsLong >= (mSunrise) && tsLong < (mSunset)){
            setmIsDay(true);
        }
        else{
            setmIsDay(false);
        }

        return getmIsDay();
    }

    // I had some format date / time / day methods in NetworkUtils and thought I should format temperature there as well.
    //
    public CharSequence getHiLoTemperature() {
//        String units = "F";
//        double high = mHighTemperature;
//        double low = mLowTemperature;
//
//        if(Weather.getmTemperatureUnit().equals("metric")){
//            units = "C";
//            high = (high - 32) * (5/9);
//            low  = (low - 32)  * (5/9);
//        }
        return TextUtils.concat(getmHighTemperature(), " / ", getmLowTemperature());
//        return convertTemperature(mHighTemperature) + " / " + convertTemperature(mLowTemperature);
//        return df.format(high) + (char) 0x00B0 + units + " / " + df.format(low) + (char) 0x00B0 + units;
//        return String.format(Locale.ENGLISH,"%02.0f", high) + (char) 0x00B0 + units + " / " +
//                String.format(Locale.ENGLISH,"%02.0f", low) + (char) 0x00B0 + units;
    }

    public double getImperialTemperature(){
        return mCurrentTemperature;
    }

    // TODO place in NetworkUtils
    public SpannableString convertTemperature(double temp){
        String units = "F";

        if(mTemperatureUnit.equals("metric")){
            units = "C";
            temp = (int) ((temp - 32) * .5556);
        }

//        SpannableString temperature = new SpannableString(df.format(temp) + (char) 0x00B0 + units);
//        temperature.setSpan(new RelativeSizeSpan(0.7f),temperature.length()-1, temperature.length(), 0);
//        return temperature;
        return FormatUtils.shrinkUnit(df.format(temp) + (char) 0x00B0 + units);
        //return df.format(temp) + (char) 0x00B0 + units;
    }

//    private SpannableString shrinkUnit(String temp){
//        SpannableString temperature = new SpannableString(temp);
//        temperature.setSpan(new RelativeSizeSpan(0.7f),temperature.length()-1, temperature.length(), 0);
//        return temperature;
//    }

    public String getWindSpeedAndDirection(){
        String speed, units;

        // Nifty little formula found on the interwebs
        // 360 degrees / 22.5 = 16 different directions. Add .5 to correct for 'on the line' values.
        // 348.75 / 22.5 = 15.5 is N but without adding the .5 it would return a direction of NNE.
        // Cast the result as an int and use the modulo or the result as an index in the direction array.
        int val = (int) ((getmWindDirection() / 22.5 ) + .5);
        String[] direction = {"N","NNE","NE","ENE","E","ESE", "SE", "SSE","S","SSW","SW","WSW","W","WNW","NW","NNW"};

        // Format the speed and set the units.
        if(mWindSpeedUnit.equals("metric")) {
            units = " m/s";
            speed = df.format(getmWindSpeed() * .447);
        }
        else{
            units = " MPH";
            speed = df.format(getmWindSpeed());
        }

        return direction[val % 16] + " " + speed + units;
    }

    // Override toString to check data
    @Override
    public String toString(){
        return new StringBuilder()
                .append("City         : ").append(getmCityName()).append("\n")
                .append("Weather      : ").append(getmWeatherDescription()).append("\n")
                .append("Sunrise      : ").append(FormatUtils.formatTime(getmSunrise())).append("\n")
                .append("Sunset       : ").append(FormatUtils.formatTime(getmSunset())).append("\n")
                .append("Current Time : ").append(FormatUtils.formatTime(System.currentTimeMillis())).append("\n")
                .append("Longitude    : ").append(getmLongitude()).append("\n")
                .append("Latitude     : ").append(getmLatitude()).append("\n")
                .append("Is Sun Up    : ").append(getIsSunUp()).append("\n").append("\n").toString();
    }

    // The remaining section is to make objects parcelable for passing in Intents (between activities).
    protected Weather(Parcel in) {
        mCityName = in.readString();
        mWeatherDescription = in.readString();
        mSunrise = in.readLong();
        mSunset = in.readLong();
        mCurrentTemperature = in.readDouble();
        mHighTemperature = in.readDouble();
        mLowTemperature = in.readDouble();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCityName);
        dest.writeString(mWeatherDescription);
        dest.writeLong(mSunrise);
        dest.writeLong(mSunset);
        dest.writeDouble(mCurrentTemperature);
        dest.writeDouble(mHighTemperature);
        dest.writeDouble(mLowTemperature);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Weather> CREATOR = new Parcelable.Creator<Weather>() {
        @Override
        public Weather createFromParcel(Parcel in) {
            return new Weather(in);
        }

        @Override
        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };
}

