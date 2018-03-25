package com.jamesfody.weatherforecast.Adapters;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jamesfody.weatherforecast.Objects.Locations;
import com.jamesfody.weatherforecast.R;

import java.util.ArrayList;

/**
 * Created by Jim on 12/14/2017.
 *
 */

public class AddCityAdapter extends ArrayAdapter<Locations> {

    public AddCityAdapter(Context context, ArrayList<Locations> loc) {
        super(context, 0, loc);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.add_city_item, parent, false);
        }

        // Create a location object from the current item in list.
        Locations currentLocation = getItem(position);

        // Extract city and country from location object.
        SpannableString city = currentLocation.getmCity();
        String country = currentLocation.getmCountry();

        // Set textview text.
        TextView cityName = listItemView.findViewById(R.id.add_state_tv);
        cityName.setText(city);

        TextView countryName = listItemView.findViewById(R.id.add_country_tv);
        countryName.setText(country);

        return listItemView;
    }

}
