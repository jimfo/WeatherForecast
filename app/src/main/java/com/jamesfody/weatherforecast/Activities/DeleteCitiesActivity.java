package com.jamesfody.weatherforecast.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jamesfody.weatherforecast.Adapters.DeleteCitiesAdapter;
import com.jamesfody.weatherforecast.Objects.StaticContext;
import com.jamesfody.weatherforecast.Objects.Weather;
import com.jamesfody.weatherforecast.R;
import com.jamesfody.weatherforecast.Utils.GeoCoderUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Jim on 12/5/2017.
 *
 */

public class DeleteCitiesActivity extends Activity {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = DeleteCitiesActivity.class.getSimpleName();

    private ArrayList<String> mCityNames = new ArrayList<>();
    private ArrayList<Weather> mWeatherData = new ArrayList<>();
    private ListView listView;
    private DeleteCitiesAdapter adapter;
    CheckBox selectAllCB;
    CheckBox selectedItem;
    TextView counterTV;
    TextView deleteTV;
    int numberSelected = 0;

   // File file = StaticContext.getAppContext().getFileStreamPath("city_list.txt");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_cities_layout);

        if(android.os.Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getColor(R.color.add_city_color));
        }

        // Get ArrayList of weather objects sent by main activity
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        selectAllCB = findViewById(R.id.select_all_cb);
        counterTV = findViewById(R.id.count_selected);
        deleteTV = findViewById(R.id.delete_tv);
        numberSelected = 0;

        // Verify extras exists and contains the specified key
        if (extras != null) {
            if (extras.containsKey("weather_data")) {
                mWeatherData.clear();
                mWeatherData = extras.getParcelableArrayList("weather_data");
            }
            if (extras.containsKey("city_names")) {
                mCityNames.clear();
                mCityNames.addAll(extras.getStringArrayList("city_names"));
            }
        }

        deleteTV.setTextColor(getColor(R.color.grey));

        // Create an {@link WeatherAdapter}, whose data source is a list of {@link Weather}.
        adapter = new DeleteCitiesAdapter(this, mWeatherData);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list_item, which is declared in the
        // weather_list.xml layout file.
        listView = findViewById(R.id.delete_list_view);

        // Make the {@link ListView} use the {@link WeatherAdapter}, so that the
        // {@link ListView} will display list items for each {@link Weather} in the list.
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedItem = listView.getChildAt(position).findViewById(R.id.item_selected);

                selectedItem.setChecked(!selectedItem.isChecked());

                if(selectedItem.isChecked()){
                    numberSelected++;
                    if(numberSelected == listView.getAdapter().getCount()){
                        selectAllCB.setChecked(true);
                    }
                    Log.v(LOG_TAG, "Inside onItemChecked " + numberSelected);
                }
                else {
                    numberSelected--;
                    if (numberSelected < listView.getAdapter().getCount()) {
                        selectAllCB.setChecked(false);
                    }
                }

                if(numberSelected > 0){
                    deleteTV.setTextColor(getColor(R.color.daytime_theme));
                }
                else{
                    deleteTV.setTextColor(getColor(R.color.grey));
                }
                counterTV.setText(getString(R.string.number_selected, numberSelected));
            }
        });
    }


    public Intent packExtras(Intent i){
        i.putParcelableArrayListExtra("weather_data", mWeatherData);
        i.putStringArrayListExtra("city_names", mCityNames);
        i.putExtra("caller", "delete_city_activity");
        return i;
    }

    public void selectAll(View v) {

        CheckBox cb;

        switch (v.getId()) {

            case (R.id.delete_tv):
                // Delete selected items and notify adapter
                // Delete all items and call addCityActivity
                if (numberSelected > 0) {
                    int citiesCount = listView.getCount();

                    for (int idx = citiesCount - 1; idx >= 0; idx--) {

                        cb = listView.getChildAt(idx).findViewById(R.id.item_selected);

                        if (cb.isChecked()) {

                            // Change checked state.
                            cb.setChecked(false);

                            // Remove the city from cityNames list
                            mCityNames.remove(idx);

                            // Remove the Weather object from the ArrayList
                            mWeatherData.remove(idx);

                            // Update the file of city names
                            GeoCoderUtils.saveCityListToFile(mCityNames);

                            // Notify adapter of changes
                            adapter.notifyDataSetChanged();
                        }
                    }
                    // if the list of cityNames is zero, overwrite the file with an empty array
                    // and go to addCityActivity
                    if (mCityNames.size() == 0) {
                        GeoCoderUtils.saveCityListToFile(new ArrayList<String>());
                        goToAddCityActivity();
                    }
                    counterTV.setText(getString(R.string.number_selected, 0));
                    deleteTV.setTextColor(getColor(R.color.grey));
                    numberSelected = 0;
                }
                break;

            case (R.id.cancel_tv):
                // Cancel returns to WeatherListActivity.

                Intent wlvIntent = new Intent(DeleteCitiesActivity.this, WeatherListActivity.class);
                startActivity(packExtras(wlvIntent));

                break;

            case (R.id.select_all_cb):
                // Scroll through list and set all items checked/unchecked.

                // get the select all checkbox at top of page

                int checkedCount = listView.getChildCount();

                // if the selectAllCB is checked set all checkboxes checked
                // if the selectAllCB is unchecked set all checkboxes unchecked
                if (selectAllCB.isChecked()) {
                    for (int idx = 0; idx < checkedCount; idx++) {
                        cb = listView.getChildAt(idx).findViewById(R.id.item_selected);
                        cb.setChecked(true);
                        numberSelected = listView.getAdapter().getCount();
                        deleteTV.setTextColor(getColor(R.color.daytime_theme));
                        counterTV.setText(getString(R.string.number_selected, listView.getAdapter().getCount()));
                    }
                } else {
                    for (int idx = 0; idx < checkedCount; idx++) {
                        cb = listView.getChildAt(idx).findViewById(R.id.item_selected);
                        cb.setChecked(false);
                        numberSelected = 0;
                        deleteTV.setTextColor(getColor(R.color.grey));
                        counterTV.setText(getString(R.string.number_selected, 0));
                    }
                }

                break;
        }
    }

    public void goToAddCityActivity() {
        Intent addCityIntent = new Intent(DeleteCitiesActivity.this, AddCityActivity.class);
        startActivity(packExtras(addCityIntent));
    }
}

