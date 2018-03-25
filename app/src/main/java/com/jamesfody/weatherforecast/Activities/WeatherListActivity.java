package com.jamesfody.weatherforecast.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jamesfody.weatherforecast.Adapters.WeatherAdapter;
import com.jamesfody.weatherforecast.Objects.StaticContext;
import com.jamesfody.weatherforecast.Objects.Weather;
import com.jamesfody.weatherforecast.R;
import com.jamesfody.weatherforecast.Utils.FormatUtils;
import com.jamesfody.weatherforecast.Utils.GeoCoderUtils;
import com.jamesfody.weatherforecast.Utils.NetworkUtils;

import java.util.ArrayList;

/**
 * Created by Jim on 12/2/2017.
 *
 * Weather List View Activity
 */

public class WeatherListActivity extends Activity {

    public static final String LOG_TAG = WeatherListActivity.class.getSimpleName();
    private WeatherAdapter mWeatherAdapter;
    private ArrayList<Weather> mWeatherData = new ArrayList<>();
    private static ArrayList<Weather> mTodayWeather = new ArrayList<>();
    private ArrayList<String> mCityNames = new ArrayList<>();
    private PopupWindow mPopupWindow;
    private LayoutInflater mLayoutInflater;
    private RelativeLayout mRelativeLayout;
    private TextView mUpdateTime;
    private Bundle mExtras;
    private ImageView weatherAddBtn;
    private ImageView deleteBtn;
    private String refreshTime;
    private LinearLayout mLinearLayout;
    private ListView mListView;
    private int mPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_list);

        mRelativeLayout = findViewById(R.id.weather_list_rl);

        mLinearLayout = findViewById(R.id.ll_weather_list);

        weatherAddBtn = findViewById(R.id.weather_list_add_btn);

        deleteBtn = findViewById(R.id.delete_btn_iv);

        mUpdateTime = findViewById(R.id.updateTime);

        // Get ArrayList of weather objects sent by main activity
        Intent intent = getIntent();
        mExtras = intent.getExtras();

        if(mExtras != null) {
            mWeatherData.clear();
            mCityNames.clear(); //mTodayWeather.addAll(allCities.subList(0, mCityNames.size()));
            mWeatherData = mExtras.getParcelableArrayList("weather_data");
            mCityNames.addAll(mExtras.getStringArrayList("city_names"));
            GeoCoderUtils.saveCityListToFile(mCityNames);

            if (mExtras.containsKey("caller")) {
                Log.v(LOG_TAG, mExtras.getString("caller"));
                if ("add_activity".equals(mExtras.getString("caller"))) {
                    weatherAddBtn.animate().scaleX(0.0f).scaleY(0.0f).setDuration(0).start();
                    weatherAddBtn.animate().scaleX(1.0f).scaleY(1.0f).setDuration(1000).start();
                }
            }
            if(mExtras.containsKey("refresh_time")){
                mUpdateTime.setText(getString(R.string.updated, mExtras.getString("refresh_time")));
            }
        }

        if(mWeatherData.get(0).getIsSunUp()){
            mRelativeLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.daytime_theme));
        }
        else{
            mRelativeLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.evening_theme));
        }

        if(android.os.Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if(mWeatherData.get(0).getIsSunUp()){
                window.setStatusBarColor(this.getColor(R.color.daytime_theme));
            }
            else{
                window.setStatusBarColor(this.getColor(R.color.evening_theme));
            }
        }

        // Create an {@link WeatherAdapter}, whose data source is a list of {@link Weather}.
        mWeatherAdapter = new WeatherAdapter(this, mWeatherData);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list_item, which is declared in the
        // weather_list.xml layout file.
        mListView = findViewById(R.id.list_view);

        // Make the {@link ListView} use the {@link WeatherAdapter}, so that the
        // {@link ListView} will display list items for each {@link Word} in the list.
        mListView.setAdapter(mWeatherAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                // Initialize a new instance of LayoutInflater service
                mLayoutInflater = (LayoutInflater) StaticContext.getAppContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                // Inflate the custom layout/view
                View customView = mLayoutInflater.inflate(R.layout.delete_item_popup,null);

                TextView cityNameTV = customView.findViewById(R.id.delete_city_tv);
                cityNameTV.setText(mWeatherData.get(position).getmCityName());

                TextView deleteCityTV = customView.findViewById(R.id.delete_action_tv);

                mPopupWindow = new PopupWindow(customView,1200 ,350,true);
                mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0,0);

                deleteCityTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mPopupWindow.dismiss();

                        String msgTitle = "<font face=\"casual\"><b>Delete</b><br/><br/>City will be deleted. Delete?</font>";

                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(WeatherListActivity.this);
                        alertDialog.setMessage(Html.fromHtml(msgTitle)).setCancelable(false)
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                    }
                                })
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        // Remove the city name from list of city names.
                                        mCityNames.remove(position);

                                        // Update the file with updated list of city names.
                                        GeoCoderUtils.saveCityListToFile(mCityNames);

                                        // Remove the Weather object from Weather List.
                                        mWeatherData.remove(position);

                                        // Update the adapter.
                                        mWeatherAdapter.notifyDataSetChanged();

                                        if(mCityNames.size() == 0){
                                            gotoAddCityActivity();
                                        }
                                    }
                                });
                        AlertDialog builder =  alertDialog.create();
                        builder.show();
                        builder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(StaticContext.getAppContext(), R.color.yesno));
                        builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(StaticContext.getAppContext(), R.color.yesno));

                    }
                });
                customView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mPopupWindow.dismiss();
                        return false;
                    }
                });

                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent activeCityIntent = new Intent (WeatherListActivity.this, MainActivity.class);
                startActivity(packExtras(activeCityIntent));
            }
        });

        updateWeather(findViewById(R.id.updateTime));
        mWeatherData.clear();
        mWeatherAdapter.notifyDataSetChanged();
    }

    public void topBarSelection(View v){

        switch (v.getId()){
            case (R.id.settings_btn_iv):
                Intent settingsIntent = new Intent (WeatherListActivity.this, SettingsActivity.class);
                startActivity(packExtras(settingsIntent));
                break;
            case (R.id.back_btn_tv):
                Intent mainActivityIntent = new Intent (WeatherListActivity.this, MainActivity.class);
                startActivity(packExtras(mainActivityIntent));
                break;
            case(R.id.weather_list_add_btn):
                weatherAddBtn.animate().scaleX(0.0f).scaleY(0.0f).setDuration(500)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                            gotoAddCityActivity();
                            }
                        })
                        .start();
                break;
            case (R.id.delete_btn_iv):
                Intent deleteIntent = new Intent (WeatherListActivity.this, DeleteCitiesActivity.class);
                startActivity(packExtras(deleteIntent));
                break;
        }
    }

    public void gotoAddCityActivity(){
        Intent addCityIntent = new Intent (WeatherListActivity.this, AddCityActivity.class);
        startActivity(packExtras(addCityIntent));
    }

    public Intent packExtras(Intent i){
        i.putParcelableArrayListExtra("weather_data", mWeatherData);
        i.putStringArrayListExtra("city_names", mCityNames);
        i.putExtra("caller", "weather_list_view");
        i.putExtra("position",  0);
        return i;
    }

    /**
     *
     * @param view : the view that was clicked
     */
    public void updateWeather(View view){
        if(view == findViewById(R.id.updateTime)){
            mCityNames.clear();
            mCityNames.addAll(GeoCoderUtils.getCityListFromFile());
            mUpdateTime.setText(getString(R.string.updated, FormatUtils.formatTime(System.currentTimeMillis())));
            new WeatherAsyncTask().execute(NetworkUtils.createRequestURLs(mCityNames));

        }
    }

    private class WeatherAsyncTask extends WeatherAsyncTaskActivity {

        protected void onPostExecute(ArrayList<Weather> result) {
            super.onPostExecute(result);
            // Actions to take when data is returned

            mWeatherData.clear();
            mWeatherData.addAll(result.subList(0, mCityNames.size()));

            mWeatherAdapter.notifyDataSetChanged();
        }
    }
}
