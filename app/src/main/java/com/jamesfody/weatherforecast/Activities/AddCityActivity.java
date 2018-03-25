package com.jamesfody.weatherforecast.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jamesfody.weatherforecast.Adapters.AddCityAdapter;
import com.jamesfody.weatherforecast.Database.DBHelper;
import com.jamesfody.weatherforecast.Objects.Locations;
import com.jamesfody.weatherforecast.Objects.Weather;
import com.jamesfody.weatherforecast.R;
import com.jamesfody.weatherforecast.Utils.GeoCoderUtils;

import java.util.ArrayList;

/**
 * Created by Jim on 12/7/2017.
 *
 */

public class AddCityActivity extends Activity {

    /** Tag for the log messages */
    public static final String LOG_TAG = AddCityActivity.class.getSimpleName();

    private ArrayList<String> mCityNames = new ArrayList<>();
    private ArrayList<Weather> mWeatherData = new ArrayList<>();
    private DBHelper mydb;
    private ListView cityListView;
    private ArrayList<Locations> cityLocations;
    private AddCityAdapter addCityAdapter;
    private EditText addCityET;
    private TextView currentLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String mCaller;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_city_layout);

        if(android.os.Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getColor(R.color.add_city_color));
        }

        // Get ArrayList of weather objects sent by main activity
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        handleExtras(extras);

        mydb = new DBHelper(this);
        addCityET = findViewById(R.id.add_city_et);

        cityListView = findViewById(R.id.add_city_lv);

        cityLocations = new ArrayList<>();

        addCityAdapter = new AddCityAdapter(this, cityLocations);

        currentLocation = findViewById(R.id.current_location_tv);

        final LinearLayout buttonBorder = findViewById(R.id.button_border);

        addCityET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cityLocations.addAll(mydb.getAllCities(s));
                addCityAdapter.notifyDataSetChanged();
                if (s.equals("")) {
                    refreshList("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                String enteredText = addCityET.getText().toString();
                if (!enteredText.equals("") || !enteredText.isEmpty()) {
                    currentLocation.setVisibility(View.GONE);
                    buttonBorder.setVisibility(View.GONE);
                } else {
                    currentLocation.setVisibility(View.VISIBLE);
                    buttonBorder.setVisibility(View.VISIBLE);
                }
                refreshList(enteredText);
            }
        });

        cityListView.setAdapter(addCityAdapter);

        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView cityName = view.findViewById(R.id.add_state_tv);
                if(!mCityNames.contains(cityName.getText().toString())) {
                    addCityET.setText(cityName.getText());
                    Log.v(LOG_TAG, cityName.getText().toString());
                    String city = cityName.getText().toString().replaceAll(" ", "%20");
                    Log.v(LOG_TAG, cityName.getText().toString());
                    mCityNames.add(city);
                    GeoCoderUtils.saveCityListToFile(mCityNames);
                    gotoMainActivity();
                }
                else{
                    Toast.makeText(getApplicationContext(), "City name is already in list", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public Intent packExtras(Intent i){
        i.putParcelableArrayListExtra("weather_data", mWeatherData);
        i.putStringArrayListExtra("city_names", mCityNames);
        i.putExtra("caller", "add_activity");
        return i;
    }

    public void refreshList(String s) {
        if (!s.equals("")) {
            cityLocations.clear();
            cityLocations.addAll(mydb.getAllCities(s));
            addCityAdapter.notifyDataSetChanged();
            cityListView.setAdapter(new AddCityAdapter(this, cityLocations));
        } else {
            addCityAdapter.clear();
            cityLocations.clear();
            cityListView.setAdapter(null);
            addCityAdapter.notifyDataSetChanged();
        }
    }

    public void handleExtras(Bundle b) {
        if (b != null) {
            if (b.containsKey("city_names")) {
                mCityNames.addAll(b.getStringArrayList("city_names"));
            }
            if(b.containsKey("weather_data")){
                mWeatherData = b.getParcelableArrayList("weather_data");
            }
            if(b.containsKey("caller")){
                mCaller = b.getString("caller");
            }
        }
    }

    public void gotoMainActivity() {
        Intent backIntent = new Intent(AddCityActivity.this, MainActivity.class);
        startActivity(packExtras(backIntent));
    }

    public void selectCity(View v) {
        switch (v.getId()) {
            case (R.id.weather_back_tv):
                if (mCityNames.size() > 0) {
                    Intent backIntent;
                    if(mCaller.equals("weather_list_view")){
                        backIntent = new Intent(AddCityActivity.this, WeatherListActivity.class);
                    }
                    else{
                        backIntent = new Intent(AddCityActivity.this, MainActivity.class);
                    }
                    startActivity(packExtras(backIntent));
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.v(LOG_TAG, "Inside permission");

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                mCityNames.add(GeoCoderUtils.getCity(latitude, longitude));
                gotoMainActivity();
            }
        }
    }


    public void getLocation(View v) {
        Log.v(LOG_TAG, "inside location");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // If we don't have permission then ask user for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            Log.v(LOG_TAG, "inside permission1");

        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            mCityNames.add(GeoCoderUtils.getCity(latitude, longitude));
            gotoMainActivity();
        }

    }
}

