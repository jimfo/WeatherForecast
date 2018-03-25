package com.jamesfody.weatherforecast.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.jamesfody.weatherforecast.Objects.Locations;
import com.jamesfody.weatherforecast.Objects.StaticContext;
import com.jamesfody.weatherforecast.R;

import java.util.ArrayList;

/**
 * Created by Jim on 12/11/2017.
 *
 * Standard DBHelper class. Boiler plate.
 */

public class DBHelper extends SQLiteOpenHelper {

    /** Tag for the log messages */
    public static final String LOG_TAG = DBHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "MyDatabase.db";
    public static final String TABLE_NAME = "all_cities";
    public static final String COLUMN_ID = "id";
    public static final String CITY_NAME = "city_name";
    public static final String STATECOUNTY = "state_country";

    public DBHelper(Context context) {
        // Default constructor
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table and columns
        db.execSQL(
                "create table all_cities " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT, city_name TEXT, state_country TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS all_cities");
        onCreate(db);
    }

    boolean insertCityData (String cityName, String statecountry) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("city_name", cityName);
        contentValues.put("state_country", statecountry);
        db.insert("all_cities", null, contentValues);

        return true;
    }

    public ArrayList<Locations> getAllCities(CharSequence s) {

        // Get a string array of all city names to be used for autocomplete functionality
        SQLiteDatabase db = this.getReadableDatabase();

        // Create the query string.
        String sql = "";
        sql += "SELECT * FROM  all_cities";
        sql += " WHERE city_name LIKE '" + s + "%'";
        sql += " ORDER BY id DESC";

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        ArrayList<Locations> allCitiesArray;

        if(cursor.getCount() > 0){

            allCitiesArray = new ArrayList<>();

            while (cursor.moveToNext())
            {
                SpannableString city = new SpannableString(cursor.getString(cursor.getColumnIndex(CITY_NAME)));
                city.setSpan(new ForegroundColorSpan(StaticContext.getAppContext().getColor(R.color.spanstring)),
                        0, s.length(), 0);
                String country = cursor.getString(cursor.getColumnIndex(STATECOUNTY));
                Locations loc = new Locations(city, country);
                allCitiesArray.add(loc);
            }
            cursor.close();
            return allCitiesArray;
        }
        else{
            cursor.close();
            return new ArrayList<>();
        }
    }

    int numberOfRows(){

        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }


}
