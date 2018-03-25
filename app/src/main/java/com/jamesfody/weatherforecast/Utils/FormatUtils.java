package com.jamesfody.weatherforecast.Utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;

import com.jamesfody.weatherforecast.Objects.Weather;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jim on 12/31/2017.
 */

public final class FormatUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = FormatUtils.class.getSimpleName();

    private FormatUtils(){}

    static String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy", Locale.US);
        return dateFormat.format(dateObject);
    }

    public static String formatTime(long ut) {
//        Date date = new Date(ut);
//        Log.v(LOG_TAG, "TIME : " + ut);
//        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
//        sdf.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getDisplayName()));
//        return sdf.format(date);
        String am_pm;
        String hour;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ut);

        if(calendar.get(Calendar.AM_PM) == 1){
            am_pm = "PM";
        }
        else{
            am_pm = "AM";
        }

        if(calendar.get(Calendar.HOUR_OF_DAY) > 12){
            hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY) - 12);
        }
        else if(calendar.get(Calendar.HOUR_OF_DAY) == 0){
            hour = String.valueOf(12);
        }
        else{
            hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        }


        String minutes = String.format(Locale.ENGLISH,"%02d", calendar.get(Calendar.MINUTE));
//        return calendar.get(Calendar.HOUR_OF_DAY) + ":" + minutes + " " + am_pm;
        return hour + ":" + minutes + " " + am_pm;

    }

    public static String formatDay(long ut){

        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.ENGLISH);
        Date dateFormat = new Date(ut * 1000);

        return sdf.format(dateFormat);
    }

    public static int[] getWeatherIconName(Context context, String[] id, boolean sunrise){

        int[] resid = new int[id.length];

        for(int idx = 0; idx < id.length; idx++){

            String str = id[idx].replaceAll("\\s+","");

            if(!sunrise){
                str = "n" + str;
            }

            resid[idx] = context.getResources().getIdentifier(str, "drawable", "com.jamesfody.weatherforecast");
        }
        return resid;
    }

    public static SpannableString shrinkUnit(String temp){
        SpannableString temperature = new SpannableString(temp);
        temperature.setSpan(new RelativeSizeSpan(0.7f),temperature.length()-1, temperature.length(), 0);
        return temperature;
    }


    public static CharSequence windChillAndHeatIndex(Weather w){
        //   {HI} = c1 + c2T + c3R + c4TR + c5(T^2) +c6(R^2) + c7(T^2)R + c8T(R^2) + c9(T^2)(R^2)
        //   temperature equal to or greater than 80 °F
        //   and relative humidity equal to or greater than 40%

        //   HI = heat index (in degrees Fahrenheit)
        //   T = ambient dry-bulb temperature (in degrees Fahrenheit)
        //   R = relative humidity (percentage value between 0 and 100)
        //   c1 = -42.379, c2 = 2.04901523, c3 = 10.14333127, c4 = -0.22475541, c5 =-6.83783 x 10^{-3},
        //   c6 = -5.481717 x 10^{-2}, c7 = 1.22874 x 10^{-3}, c8 = 8.5282 x 10^{-4}, c9 = -1.99 x 10^{-6}

        //   Wind Chill = 35.74 + 0.6215T – 35.75(V^0.16) + 0.4275T(V^0.16)
        //   T = temperature in Fahrenheit / V = wind speed. Use below 50 °F


        double temp = w.getImperialTemperature();
        double hum = Double.parseDouble(w.getmHumidity().substring(0, w.getmHumidity().length() - 1));
        double windspeed = w.getmWindSpeed();

        CharSequence factor = "";
        if(temp > 79 && hum > 39){
            double heatindex = -42.379 + (2.04901523 * temp) + (10.14333127 * hum) + (-0.22475541 * temp * hum) +
                    ((-6.83783 * Math.pow(10, -3)) * Math.pow(temp, 2)) + ((-5.481717 * Math.pow(10, -2)) * Math.pow(hum, 2)) +
                    ((1.22874 * Math.pow(10, -3)) * Math.pow(temp, 2) * hum) + ((8.5282 * Math.pow(10, -4)) * (temp * Math.pow(hum, 2))) +
                    ((-1.99 * Math.pow(10, -6)) * ((Math.pow(temp, 2) * Math.pow(hum, 2))));
            factor = TextUtils.concat(" Heat Index ", w.convertTemperature(heatindex));
        }

        if(temp < 50  && windspeed > 2){
            //35.74 + (0.6215 * temperature) - (35.75 * Math.pow(speed, 0.16)) + (0.4275 * temperature * Math.pow(speed, 0.16));
            double windChillTemp =  (35.74 + (0.6215 * temp) - (35.75 * Math.pow(windspeed, 0.16))
                    + (0.4275 * temp * Math.pow(windspeed, 0.16))) * 10000 / 10000.0 ;
            factor = TextUtils.concat(" Wind Chill ", w.convertTemperature(windChillTemp));
        }

        return factor;
    }
}
