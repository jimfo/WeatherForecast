package com.jamesfody.weatherforecast.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jamesfody.weatherforecast.Objects.Weather;
import com.jamesfody.weatherforecast.R;
import com.jamesfody.weatherforecast.Utils.GeoCoderUtils;

import java.util.ArrayList;

/**
 * Created by Jim on 12/5/2017.
 *
 */

public class DeleteCitiesAdapter extends ArrayAdapter<Weather> {

    public DeleteCitiesAdapter(Context context, ArrayList<Weather> cities) {
        super(context, 0, cities);
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.delete_list_item, parent, false);
        }

        // Get the {@link Weather} object located at this position in the list
        Weather currentCity = getItem(position);

        double latitude = currentCity.getmLatitude();
        double longitude = currentCity.getmLongitude();

        CheckBox itemSelected = listItemView.findViewById(R.id.item_selected);

        TextView cityName = listItemView.findViewById(R.id.item_city_name);
        cityName.setText(currentCity.getmCityName());

        TextView stateCountry = listItemView.findViewById(R.id.item_state_name);
        stateCountry.setText(GeoCoderUtils.getStateAndCountry(latitude,longitude));

//        itemSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(buttonView.isChecked()){
//                    buttonView.setTag("on");
//                }
//                else{
//                    buttonView.setTag("off");
//                }
//            }
//        });


        return listItemView;
    }
}












