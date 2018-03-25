package com.jamesfody.weatherforecast.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.jamesfody.weatherforecast.Adapters.ReorderCityAdapter;
import com.jamesfody.weatherforecast.Objects.Weather;
import com.jamesfody.weatherforecast.R;
import com.jamesfody.weatherforecast.Utils.GeoCoderUtils;
import com.jamesfody.weatherforecast.Utils.NetworkUtils;

import java.util.ArrayList;

/**
 * Created by Jim on 1/4/2018.
 *
 *
 */

public class ReorderCityActivity extends Activity implements ReorderCityAdapter.OnStartDragListener {

    private ItemTouchHelper mItemTouchHelper;
    private static ArrayList<Weather> mWeatherData = new ArrayList<>();
    private static ArrayList<String> mCityNames = new ArrayList<>();
    private String mCaller = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reorder_city_list_layout);

        // Get ArrayList of weather objects sent by main activity
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras != null) {
            mWeatherData = extras.getParcelableArrayList("weather_data");
            mCityNames.clear();
            mCityNames.addAll(extras.getStringArrayList("city_names"));
            mCaller = extras.getString("caller");
            GeoCoderUtils.saveCityListToFile(mCityNames);
        }

        if(android.os.Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if(mWeatherData.get(0).getIsSunUp()) {
                window.setStatusBarColor(this.getColor(R.color.daytime_theme));
            }
            else{
                window.setStatusBarColor(this.getColor(R.color.evening_theme));
            }
        }

        RecyclerView mReorderCityRecyclerView;
        ReorderCityAdapter mReorderCityAdapter;

        mReorderCityRecyclerView = findViewById(R.id.reorder_city_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mReorderCityRecyclerView.setLayoutManager(layoutManager);
        mReorderCityRecyclerView.setHasFixedSize(true);
        mReorderCityAdapter = new ReorderCityAdapter(this, mWeatherData, mCityNames, this);
        ItemTouchHelper.Callback callback = new EditItemTouchHelperCallback(mReorderCityAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mReorderCityRecyclerView);
        mReorderCityRecyclerView.setAdapter(mReorderCityAdapter);

    }

    public void setmWeatherData(ArrayList<Weather> weatherdata) {
        mWeatherData.clear();
        mWeatherData.addAll(weatherdata);
    }

    public void setmCityNames(ArrayList<String> cityNames) {
        mCityNames.clear();
        mCityNames.addAll(cityNames);
    }

    public void topBarSelection(View v){
        switch (v.getId()) {
            case (R.id.reorder_city_plus_icon):
                Intent addCityIntent = new Intent (ReorderCityActivity.this, AddCityActivity.class);
                startActivity(packExtras(addCityIntent));
                break;

            case (R.id.reorder_city_delete_icon):
                Intent deleteIntent = new Intent (ReorderCityActivity.this, DeleteCitiesActivity.class);
                startActivity(packExtras(deleteIntent));
                break;

            case (R.id.reorder_city_back_tv):
                Log.v("TAGGG", "REORDER CITY");
                Intent weatherLVIntent = new Intent(ReorderCityActivity.this, SettingsActivity.class);
                startActivity(packExtras(weatherLVIntent));
                break;
        }
    }

    public Intent packExtras(Intent i){
        i.putParcelableArrayListExtra("weather_data", mWeatherData);
        i.putStringArrayListExtra("city_names", mCityNames);
        i.putExtra("caller", "reorder_city_activity");
        return i;
    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
