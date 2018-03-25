package com.jamesfody.weatherforecast.Objects;

import android.text.SpannableString;

/**
 * Created by Jim on 12/14/2017.
 *
 */

public class Locations {
    private SpannableString mCity;
    private String mCountry;

    public Locations(SpannableString city, String country){
        mCity = city;
        mCountry = country;
    }

    public SpannableString getmCity(){
        return mCity;
    }

    public String getmCountry(){
        return mCountry;
    }
}
