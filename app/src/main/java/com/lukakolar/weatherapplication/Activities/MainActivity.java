package com.lukakolar.weatherapplication.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.lukakolar.weatherapplication.Constants;
import com.lukakolar.weatherapplication.Databases.WeatherUpdatesDatabaseHandlerSingleton;
import com.lukakolar.weatherapplication.Entity.CityWeatherObject;
import com.lukakolar.weatherapplication.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private TextView textNoItems;
    private CoordinatorLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addCity = new Intent(MainActivity.this, AddCityActivity.class);
                startActivityForResult(addCity, Constants.ADD_CITY_REQUEST);
            }
        });

        // RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(this, new ArrayList<CityWeatherObject>());
        recyclerView.setAdapter(recyclerViewAdapter);

        // Other
        textNoItems = (TextView) findViewById(R.id.text_no_items);
        mainLayout = (CoordinatorLayout) findViewById(R.id.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton.getInstance(this);
        w.openDatabase();
        List<CityWeatherObject> values = w.getSavedCities();
        recyclerViewAdapter = new RecyclerViewAdapter(this, values);
        recyclerView.setAdapter(recyclerViewAdapter);
        checkIfEmpty();
    }

    @Override
    protected void onPause() {
        super.onPause();
        WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton.getInstance(this);
        w.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton.getInstance(this);
        w.openDatabase();
        if (requestCode == Constants.ADD_CITY_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                String name = data.getStringExtra(Constants.CITIES_DATABASE_FIELD_NAME);
                Integer id = data.getIntExtra(Constants.CITIES_DATABASE_FIELD_ID, 0);
                createEntry(new CityWeatherObject(id, name, null, null, null));
            }
        }
    }

    void onCitySelected(CityWeatherObject item) {
        Intent intent = new Intent(this, CityInfoActivity.class);
        intent.putExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_ID, item.id);
        intent.putExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_NAME, item.name);
        intent.putExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_TEMPERATURE, item.temperature);
        intent.putExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_HUMIDITY, item.humidity);
        intent.putExtra(Constants.WEATHER_UPDATES_DATABASE_FIELD_DESCRIPTION, item.description);
        startActivity(intent);
    }

    private void checkIfEmpty() {
        int length = recyclerViewAdapter.getItemCount();
        if (length > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            textNoItems.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            textNoItems.setVisibility(View.VISIBLE);
        }
    }

    private void createEntry(CityWeatherObject item) {
        WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton.getInstance(this);
        boolean already_exists = w.checkIfEntryExists(item);
        if (already_exists) {
            showSnackbarCityAlreadyExists(item.name);
        } else {
            w.createEntry(item);
            recyclerViewAdapter.add(item);
            checkIfEmpty();
        }
    }

    private void showSnackbarCityAlreadyExists(String name) {
        String place = getResources().getString(R.string.activity_main_place);
        String alreadyExists = getResources().getString(R.string.activity_main_already_exists);
        String ok = getResources().getString(R.string.ok);

        Snackbar.make(mainLayout, place + " " + name + " " + alreadyExists, Snackbar.LENGTH_LONG)
                .setAction(ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                }).show();

    }
}
