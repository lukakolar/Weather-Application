package com.lukakolar.weatherapplication.Activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private TextView temperatureView;
    private TextView humidityView;
    private TextView descriptionView;
    private CityWeatherObject currentCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info);

        // Layout
        mainLayout = (LinearLayout) findViewById(R.id.activity_city_info);
        temperatureView = (TextView) findViewById(R.id.activity_city_info_temperature);
        humidityView = (TextView) findViewById(R.id.activity_city_info_humidity);
        descriptionView = (TextView) findViewById(R.id.activity_city_info_description);

        // Get data for views
        if (savedInstanceState != null) {
            CityWeatherObject city = savedInstanceState.getParcelable(Constants
                    .CITY_WEATHER_OBJECT);
            updateInfo(city);
        } else {
            Intent intent = getIntent();
            CityWeatherObject city = intent.getExtras().getParcelable(Constants
                    .CITY_WEATHER_OBJECT);
            updateInfo(city);
        }

        // SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id
                .activity_city_info_swipe_refresh_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton
                .getInstance(this);
        w.openDatabase();

        // Performs updates via API and listens for callbacks regarding updates
        swipeRefreshLayoutUpdater = new SwipeRefreshLayoutUpdater(this, swipeRefreshLayout, new
                SwipeRefreshLayoutCallbacksInterface() {
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
                    if (city.id.equals(currentCity.id)) {
                        updateInfo(city);
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
        WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton
                .getInstance(this);
        w.close();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(Constants.CITY_WEATHER_OBJECT, currentCity);

        super.onSaveInstanceState(savedInstanceState);
    }

    // Updates all views
    private void updateInfo(CityWeatherObject city) {
        currentCity = new CityWeatherObject(city);

        String degrees_celsius = getResources().getString(R.string.degrees_celsius);
        String percent = getResources().getString(R.string.percent);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(city.name);
        }

        if (city.description == null) {
            String refreshToGetTemperature = getResources().getString(R.string
                    .activity_main_refresh_to_get_temperature);
            temperatureView.setText(refreshToGetTemperature);
        } else {
            temperatureView.setText(city.temperature + " " + degrees_celsius);
            humidityView.setText(city.humidity + " " + percent);
            descriptionView.setText(city.description);
        }
    }

    private void showSnackbarNoInternetConnection() {
        String networkUnavailable = getResources().getString(R.string
                .activity_main_network_unavailable);
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
        String errorFetchingData = getResources().getString(R.string
                .activity_main_error_fetching_data);
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
