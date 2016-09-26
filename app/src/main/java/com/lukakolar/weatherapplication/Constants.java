package com.lukakolar.weatherapplication;

public class Constants {
    public static final String CITIES_DATABASE_PATH = "/data/data/com.lukakolar" +
            ".weatherapplication/databases/";
    public static final int CITIES_DATABASE_VERSION = 1;
    public static final String CITIES_DATABASE_NAME = "cities.db";
    public static final String CITIES_DATABASE_FULL_PATH = CITIES_DATABASE_PATH +
            CITIES_DATABASE_NAME;
    public static final String CITIES_DATABASE_TABLE_NAME = "cities";
    public static final String CITIES_DATABASE_FIELD_ID = "id";
    public static final String CITIES_DATABASE_FIELD_NAME = "name";
    public static final String VOLLEY_REQUEST_TAG = "weather";
    public static final String CITY_WEATHER_OBJECT = "cityWeatherObject";
    public static final int ADD_CITY_REQUEST = 3;
    public static final int PERMISSION_REQUEST_INTERNET = 1;
    public static final int PERMISSION_REQUEST_ACCESS_NETWORK_STATE = 2;
    public static final int WEATHER_UPDATES_DATABASE_VERSION = 1;
    public static final String WEATHER_UPDATES_DATABASE_NAME = "WeatherUpdates";
    public static final String WEATHER_UPDATES_DATABASE_TABLE_NAME = "weather_updates";
    public static final String WEATHER_UPDATES_DATABASE_FIELD_ID = "id";
    public static final String WEATHER_UPDATES_DATABASE_FIELD_NAME = "name";
    public static final String WEATHER_UPDATES_DATABASE_FIELD_TEMPERATURE = "temperature";
    public static final String WEATHER_UPDATES_DATABASE_FIELD_HUMIDITY = "humidity";
    public static final String WEATHER_UPDATES_DATABASE_FIELD_DESCRIPTION = "description";
    public static final String WEATHER_UPDATES_DATABASE_INDEX = "name_index";
}
