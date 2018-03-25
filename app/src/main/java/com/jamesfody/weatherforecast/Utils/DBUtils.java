package com.jamesfody.weatherforecast.Utils;

/**
 * Created by Jim on 1/6/2018.
 */

public class DBUtils {

    //    public void populateDB() throws IOException{
//        // This is a one time call to populate the database
//
    // Get names of city and latlngs from their files
//        InputStream inputCityName = getResources().openRawResource(R.raw.city_names);
//        InputStream inputLatLng = getResources().openRawResource(R.raw.lat_lng);
//
//        InputStreamReader inputCityReader = new InputStreamReader(inputCityName);
//        InputStreamReader inputLatLngReader = new InputStreamReader(inputLatLng);
//
//        BufferedReader cityReader = new BufferedReader(inputCityReader,9999999);
//        BufferedReader latlngReader = new BufferedReader(inputLatLngReader,9999999);
//
//        String cityLine = cityReader.readLine();
//        String latlngLine = latlngReader.readLine();
//
//        int count = 0;
//        while (latlngLine != null) {
//
//            String[] latlng = latlngLine.split("\\s+");
//
//            double lat = Double.parseDouble(latlng[0]);
//            double lng = Double.parseDouble(latlng[1]);
//
//            // Get and country from Geocoder
//            String stateCountry = NetworkUtils.getStateAndCountry(lat,lng);
//
//            // Database is very large add only US data
//            if(stateCountry.contains("United States"))
//                mydb.insertCityData(cityLine,stateCountry);
//
//            cityLine = cityReader.readLine();
//            latlngLine = latlngReader.readLine();
//        }
//        cityReader.close();
//        latlngReader.close();
//    }
}
