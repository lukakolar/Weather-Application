package com.lukakolar.weatherapplication.Activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lukakolar.weatherapplication.Constants;
import com.lukakolar.weatherapplication.R;

public class CityInfoActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout mainLayout;
    private Integer currentCityId;
    private TextView temperatureView;
    private TextView humidityView;
    private TextView descriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info);

        Intent intent = getIntent();
        Integer id = intent.getIntExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_ID, 0);
        String name = intent.getStringExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_NAME);
        String temperature = intent.getStringExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_TEMPERATURE);
        String humidity = intent.getStringExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_HUMIDITY);
        String description = intent.getStringExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_DESCRIPTION);

        currentCityId = id;

        temperatureView = (TextView) findViewById(R.id.activity_city_info_temperature);
        humidityView = (TextView) findViewById(R.id.activity_city_info_humidity);
        descriptionView = (TextView) findViewById(R.id.activity_city_info_description);

        updateInfo(temperature, humidity, description);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Log.d("actionbar", name);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(name);

        }

        // SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_city_info_swipe_refresh_layout);

        // Other
        mainLayout = (LinearLayout) findViewById(R.id.activity_city_info);

    }

    private void updateInfo(String temperature, String humidity, String description) {
        String degrees_celsius = getResources().getString(R.string.degrees_celsius);
        String percent = getResources().getString(R.string.percent);

        if (description == null) {
            String refreshToGetTemperature = getResources().getString(R.string.activity_main_refresh_to_get_temperature);
            temperatureView.setText(refreshToGetTemperature);
        } else {
            temperatureView.setText(temperature  + " " + degrees_celsius);
            humidityView.setText(humidity + " " + percent);
            descriptionView.setText(description);
        }
    }
}
