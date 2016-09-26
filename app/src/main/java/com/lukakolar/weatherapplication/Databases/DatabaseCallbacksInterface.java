package com.lukakolar.weatherapplication.Databases;

import com.lukakolar.weatherapplication.Entity.CityWeatherObject;

import java.util.List;

public interface DatabaseCallbacksInterface {
    void onEntriesRetrieved(List<CityWeatherObject> result);
    void onDatabaseOpened();
}
