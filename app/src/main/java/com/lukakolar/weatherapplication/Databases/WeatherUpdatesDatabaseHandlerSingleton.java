package com.lukakolar.weatherapplication.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lukakolar.weatherapplication.Constants;
import com.lukakolar.weatherapplication.Entity.CityWeatherObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherUpdatesDatabaseHandlerSingleton extends SQLiteOpenHelper {
    private SQLiteDatabase database;
    private static WeatherUpdatesDatabaseHandlerSingleton instance;

    private WeatherUpdatesDatabaseHandlerSingleton(Context context) {
        super(context.getApplicationContext(), Constants.WEATHER_UPDATES_DATABASE_NAME, null,
                Constants.WEATHER_UPDATES_DATABASE_VERSION);
    }

    public static synchronized WeatherUpdatesDatabaseHandlerSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new WeatherUpdatesDatabaseHandlerSingleton(context.getApplicationContext());
        }
        return instance;
    }

    public void openDatabase() {
        if (database == null || !isDatabaseOpen()) {
            Log.d("database", "OPENING");
            database = getWritableDatabase();
        } else{
            Log.d("database", "OPENED");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String command = "";
        command += "CREATE TABLE " + Constants.WEATHER_UPDATES_DATABASE_TABLE_NAME;
        command += " ( ";
        command += Constants.WEATHER_UPDATES_DATABASE_FIELD_ID + " INTEGER PRIMARY KEY, ";
        command += Constants.WEATHER_UPDATES_DATABASE_FIELD_NAME + " TEXT, ";
        command += Constants.WEATHER_UPDATES_DATABASE_FIELD_TEMPERATURE + " TEXT, ";
        command += Constants.WEATHER_UPDATES_DATABASE_FIELD_HUMIDITY + " TEXT, ";
        command += Constants.WEATHER_UPDATES_DATABASE_FIELD_DESCRIPTION + " TEXT ";
        command += " );";
        db.execSQL(command);

        command = "CREATE INDEX " + Constants.WEATHER_UPDATES_DATABASE_INDEX + " on " +
                Constants.WEATHER_UPDATES_DATABASE_TABLE_NAME + " (" +
                Constants.WEATHER_UPDATES_DATABASE_FIELD_NAME + ");";
        db.execSQL(command);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + Constants.WEATHER_UPDATES_DATABASE_TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }


    @Override
    public synchronized void close() {
        Log.d("database", "close");
        if (database != null)
            database.close();
        super.close();

    }

    private boolean isDatabaseOpen() {
        return database != null && database.isOpen();
    }

    public void createEntry(CityWeatherObject item) {
        if(!checkIfEntryExists(item)){
            ContentValues values = new ContentValues();
            values.put(Constants.WEATHER_UPDATES_DATABASE_FIELD_ID, item.id);
            values.put(Constants.WEATHER_UPDATES_DATABASE_FIELD_NAME, item.name);
            values.put(Constants.WEATHER_UPDATES_DATABASE_FIELD_TEMPERATURE, item.temperature);
            values.put(Constants.WEATHER_UPDATES_DATABASE_FIELD_HUMIDITY, item.humidity);
            values.put(Constants.WEATHER_UPDATES_DATABASE_FIELD_DESCRIPTION, item.description);
            database.insert(Constants.WEATHER_UPDATES_DATABASE_TABLE_NAME, null, values);
        }
    }

    public void updateEntry(CityWeatherObject item) {
        ContentValues args = new ContentValues();

        args.put(Constants.WEATHER_UPDATES_DATABASE_FIELD_HUMIDITY, item.humidity);
        args.put(Constants.WEATHER_UPDATES_DATABASE_FIELD_TEMPERATURE, item.temperature);
        args.put(Constants.WEATHER_UPDATES_DATABASE_FIELD_DESCRIPTION, item.description);
        database.update(Constants.WEATHER_UPDATES_DATABASE_TABLE_NAME, args,
                Constants.WEATHER_UPDATES_DATABASE_FIELD_ID + "=" + item.id, null);
    }

    public boolean checkIfEntryExists(CityWeatherObject item){
        boolean entryExists = false;

        Cursor cursor = database.query(true, Constants.WEATHER_UPDATES_DATABASE_TABLE_NAME,
                new String[] {Constants.WEATHER_UPDATES_DATABASE_FIELD_ID},
                Constants.WEATHER_UPDATES_DATABASE_FIELD_ID + " = " + item.id,
                null, null, null, null, null);


        if(cursor != null && cursor.getCount() > 0) {
            entryExists = true;
        }

        if (cursor != null) {
            cursor.close();
        }
        return entryExists;
    }

    public ArrayList<Integer> getCityIds() {

        ArrayList<Integer> ids = new ArrayList<>();

        Cursor cursor = database.query(true, Constants.WEATHER_UPDATES_DATABASE_TABLE_NAME,
                new String[]{Constants.WEATHER_UPDATES_DATABASE_FIELD_ID},
                null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {

                int id = Integer.parseInt(cursor.getString(cursor
                        .getColumnIndex(Constants.WEATHER_UPDATES_DATABASE_FIELD_ID)));
                ids.add(id);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return ids;
    }

    public List<CityWeatherObject> getSavedCities() {
        List<CityWeatherObject> cities = new ArrayList<>();

        Cursor cursor = database.query(true, Constants.WEATHER_UPDATES_DATABASE_TABLE_NAME,
                null, null, null, null, null, Constants.WEATHER_UPDATES_DATABASE_FIELD_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(Constants.WEATHER_UPDATES_DATABASE_FIELD_NAME));
                Integer id = cursor.getInt(cursor.getColumnIndex(Constants.WEATHER_UPDATES_DATABASE_FIELD_ID));
                String temperature = cursor.getString(cursor.getColumnIndex(Constants.WEATHER_UPDATES_DATABASE_FIELD_TEMPERATURE));
                String humidity = cursor.getString(cursor.getColumnIndex(Constants.WEATHER_UPDATES_DATABASE_FIELD_HUMIDITY));
                String description = cursor.getString(cursor.getColumnIndex(Constants.WEATHER_UPDATES_DATABASE_FIELD_DESCRIPTION));
                cities.add(new CityWeatherObject(id, name, temperature, humidity, description));

            } while (cursor.moveToNext());
        }

        cursor.close();


        return cities;
    }

}
