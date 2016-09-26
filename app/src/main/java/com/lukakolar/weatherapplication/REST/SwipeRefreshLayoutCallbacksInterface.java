package com.lukakolar.weatherapplication.REST;

import com.lukakolar.weatherapplication.Entity.CityWeatherObject;

import java.util.List;

public interface SwipeRefreshLayoutCallbacksInterface {
    void onConnectionUnavailable();
    void onResponseSuccess(List<CityWeatherObject> result);
    void onResponseError();
}
