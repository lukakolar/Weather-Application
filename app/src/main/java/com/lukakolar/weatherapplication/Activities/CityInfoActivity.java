package com.lukakolar.weatherapplication.Activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lukakolar.weatherapplication.Constants;
import com.lukakolar.weatherapplication.Databases.WeatherUpdatesDatabaseHandlerSingleton;
import com.lukakolar.weatherapplication.Entity.CityWeatherObject;
import com.lukakolar.weatherapplication.R;
import com.lukakolar.weatherapplication.REST.SwipeRefreshLayoutCallbacksInterface;
import com.lukakolar.weatherapplication.REST.SwipeRefreshLayoutUpdater;

import java.util.List;

public class CityInfoActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayoutUpdater swipeRefreshLayoutUpdater;
    private LinearLayout mainLayout;
    private Integer currentCityId;
    private TextView temperatureView;
    private TextView humidityView;
    private TextView descriptionView;
    private String temperatureString;
    private String humidityString;
    private String descriptionString;
    private String nameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info);

        temperatureView = (TextView) findViewById(R.id.activity_city_info_temperature);
        humidityView = (TextView) findViewById(R.id.activity_city_info_humidity);
        descriptionView = (TextView) findViewById(R.id.activity_city_info_description);

        if (savedInstanceState != null) {
            String name = savedInstanceState
                    .getString(Constants.WEATHER_UPDATES_DATABASE_FIELD_NAME);
            String temperature = savedInstanceState
                    .getString(Constants.WEATHER_UPDATES_DATABASE_FIELD_TEMPERATURE);
            String humidity = savedInstanceState
                    .getString(Constants.WEATHER_UPDATES_DATABASE_FIELD_HUMIDITY);
            String description = savedInstanceState
                    .getString(Constants.WEATHER_UPDATES_DATABASE_FIELD_DESCRIPTION);
            Integer id = savedInstanceState
                    .getInt(Constants.WEATHER_UPDATES_DATABASE_FIELD_ID);
            updateInfo(id, name, temperature, humidity, description);
        } else {
            Intent intent = getIntent();
            Integer id = intent.getIntExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_ID, 0);
            String name = intent.getStringExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_NAME);
            String temperature = intent.getStringExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_TEMPERATURE);
            String humidity = intent.getStringExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_HUMIDITY);
            String description = intent.getStringExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_DESCRIPTION);
            updateInfo(id, name, temperature, humidity, description);
        }

        // SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_city_info_swipe_refresh_layout);

        // Other
        mainLayout = (LinearLayout) findViewById(R.id.activity_city_info);

    }

    @Override
    protected void onResume() {
        super.onResume();

        WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton.getInstance(this);
        w.openDatabase();

        swipeRefreshLayoutUpdater = new SwipeRefreshLayoutUpdater(this, swipeRefreshLayout, new SwipeRefreshLayoutCallbacksInterface() {
            @Override
            public void onConnectionUnavailable() {
                showSnackbarNoInternetConnection();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponseSuccess(List<CityWeatherObject> result) {
                swipeRefreshLayout.setRefreshing(false);
                for (int i = 0; i < result.size(); i++) {
                    CityWeatherObject city = result.get(i);
                    if (city.id.equals(currentCityId)) {
                        updateInfo(city.id, city.name, city.temperature, city.humidity, city.description);
                    }
                }
            }

            @Override
            public void onResponseError() {
                swipeRefreshLayout.setRefreshing(false);
                showSnackbarErrorFetchingData();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton.getInstance(this);
        w.close();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(Constants.WEATHER_UPDATES_DATABASE_FIELD_NAME, nameString);
        savedInstanceState.putString(Constants.WEATHER_UPDATES_DATABASE_FIELD_TEMPERATURE, temperatureString);
        savedInstanceState.putString(Constants.WEATHER_UPDATES_DATABASE_FIELD_HUMIDITY, humidityString);
        savedInstanceState.putString(Constants.WEATHER_UPDATES_DATABASE_FIELD_DESCRIPTION, descriptionString);
        savedInstanceState.putInt(Constants.WEATHER_UPDATES_DATABASE_FIELD_ID, currentCityId);

        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateInfo(Integer id, String name, String temperature, String humidity, String description) {
        String degrees_celsius = getResources().getString(R.string.degrees_celsius);
        String percent = getResources().getString(R.string.percent);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Log.d("actionbar", name);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(name);
        }

        if (description == null) {
            String refreshToGetTemperature = getResources().getString(R.string.activity_main_refresh_to_get_temperature);
            temperatureView.setText(refreshToGetTemperature);
        } else {
            temperatureView.setText(temperature  + " " + degrees_celsius);
            humidityView.setText(humidity + " " + percent);
            descriptionView.setText(description);
        }

        currentCityId = id;
        nameString = name;
        temperatureString = temperature;
        humidityString = humidity;
        descriptionString = description;
    }

    private void showSnackbarNoInternetConnection() {
        String networkUnavailable = getResources().getString(R.string.activity_main_network_unavailable);
        String ok = getResources().getString(R.string.ok);

        Snackbar snackbar = Snackbar
                .make(mainLayout, networkUnavailable, Snackbar.LENGTH_LONG)
                .setAction(ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
        snackbar.show();
    }

    private void showSnackbarErrorFetchingData() {
        String errorFetchingData = getResources().getString(R.string.activity_main_error_fetching_data);
        String retry = getResources().getString(R.string.retry);

        Snackbar snackbar = Snackbar
                .make(mainLayout, errorFetchingData, Snackbar.LENGTH_LONG)
                .setAction(retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        swipeRefreshLayoutUpdater.requestUpdate();
                    }
                });
        snackbar.show();
    }
}
