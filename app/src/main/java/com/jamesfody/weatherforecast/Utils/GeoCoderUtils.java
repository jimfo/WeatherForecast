package com.jamesfody.weatherforecast.Utils;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import com.jamesfody.weatherforecast.Objects.StaticContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jim on 12/31/2017.
 *
 */

public final class GeoCoderUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = GeoCoderUtils.class.getSimpleName();

    private GeoCoderUtils(){}

    public static ArrayList<String> getListOfCities(Bundle extras){
        ArrayList<String> cityList = new ArrayList<>();

        return cityList;
    }

    public static String getCity(double lat, double lng){
        Geocoder geocoder = new Geocoder(StaticContext.getAppContext(), Locale.getDefault());
        List<Address> addresses = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try{
            addresses = geocoder.getFromLocation(lat,lng,1);
            if(!addresses.isEmpty())
                sb.append(addresses.get(0).getLocality());
        }
        catch (IOException e) {
            Log.v(LOG_TAG, "Error");
            //e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getStateAndCountry(double lat, double lng){

        Geocoder geocoder = new Geocoder(StaticContext.getAppContext(), Locale.getDefault());
        List<Address> addresses = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);

            if(!addresses.isEmpty()) {
                if (addresses.get(0).getAdminArea() == null) {
                    sb.append("").append(addresses.get(0).getCountryName());
                } else if (addresses.get(0).getAdminArea() == null && addresses.get(0).getCountryName() == null) {
                    sb.append("");
                } else {
                    sb.append(addresses.get(0).getAdminArea()).append(", ").append(addresses.get(0).getCountryName());
                }
            }

        }
        catch (IOException e) {
            Log.v(LOG_TAG, "Error");
            //e.printStackTrace();
        }
        return sb.toString();
    }

    public static void saveCityListToFile(ArrayList<String> cities){

        File file = StaticContext.getAppContext().getFileStreamPath("city_list.txt");

        try {

            FileOutputStream fileOut = StaticContext.getAppContext().openFileOutput("city_list.txt",
                    StaticContext.getAppContext().MODE_PRIVATE);
            DataOutputStream dataOut = new DataOutputStream(fileOut);
            dataOut.writeInt(cities.size());

            // write to file contents of city or delete the file
            if(cities.size() != 0) {
                for (String line : cities) {
                    dataOut.writeUTF(line);
                }
            }
            else{
                file.delete();
            }
            dataOut.flush();
            dataOut.close();
        }
        catch (IOException exc) {
            exc.printStackTrace();
        }

        //Log.v(LOG_TAG, "Cities " + cities.toString());
    }

    public static ArrayList<String> getCityListFromFile(){

        ArrayList<String> cityNameList = new ArrayList<>();

        FileInputStream input = null;

        try {
            input = StaticContext.getAppContext().openFileInput("city_list.txt");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        DataInputStream dataIn = new DataInputStream(input);

        int listSize;
        try {
            listSize = dataIn.readInt();

            for (int i = 0; i < listSize; i++) {
                String line = dataIn.readUTF();
                cityNameList.add(line);
                // Log.v(LOG_TAG, line);
            }

            dataIn.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //Log.v(LOG_TAG, cityNameList.toString());
        return cityNameList;
    }

    public static boolean isFileEmpty(){
        File file = StaticContext.getAppContext().getFileStreamPath("city_list.txt");

        if(file.length() == 0){
            return true;
        }
        else{
            return false;
        }
    }
}
