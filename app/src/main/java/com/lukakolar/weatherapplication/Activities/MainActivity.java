package com.lukakolar.weatherapplication.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.lukakolar.weatherapplication.Constants;
import com.lukakolar.weatherapplication.Databases.WeatherUpdatesDatabaseHandlerSingleton;
import com.lukakolar.weatherapplication.Entity.CityWeatherObject;
import com.lukakolar.weatherapplication.R;
import com.lukakolar.weatherapplication.REST.SwipeRefreshLayoutCallbacksInterface;
import com.lukakolar.weatherapplication.REST.SwipeRefreshLayoutUpdater;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private TextView textNoItems;
    private CoordinatorLayout mainLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayoutUpdater swipeRefreshLayoutUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addCity = new Intent(MainActivity.this, AddCityActivity.class);
                    startActivityForResult(addCity, Constants.ADD_CITY_REQUEST);
                }
            });
        }

        // RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.activity_main_recycler_view);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                CityWeatherObject item = (CityWeatherObject) viewHolder.itemView.getTag();
                showSnackbarUndo(item);
                WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton
                        .getInstance(MainActivity.this);
                w.deleteEntry(item);
                recyclerViewAdapter.remove(position);
                checkIfEmpty();
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id
                .activity_main_swipe_refresh_layout);

        // Other
        textNoItems = (TextView) findViewById(R.id.activity_main_text_no_items);
        mainLayout = (CoordinatorLayout) findViewById(R.id.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton
                .getInstance(this);
        w.openDatabase();
        List<CityWeatherObject> values = w.getSavedCities();
        recyclerViewAdapter = new RecyclerViewAdapter(this, values);
        recyclerView.setAdapter(recyclerViewAdapter);

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
                        recyclerViewAdapter.refresh(result);
                    }

                    @Override
                    public void onResponseError() {
                        swipeRefreshLayout.setRefreshing(false);
                        showSnackbarErrorFetchingData();
                    }
                });
        checkIfEmpty();
    }

    @Override
    protected void onPause() {
        super.onPause();
        WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton
                .getInstance(this);
        w.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton
                .getInstance(this);
        w.openDatabase();
        if (requestCode == Constants.ADD_CITY_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                CityWeatherObject result = data.getExtras().getParcelable(Constants
                        .CITY_WEATHER_OBJECT);
                createEntry(result);
            }
        }
    }

    // Called from RecyclerViewAdapter
    void onCitySelected(CityWeatherObject item) {
        Intent intent = new Intent(this, CityInfoActivity.class);
        intent.putExtra(Constants.CITY_WEATHER_OBJECT, item);
        startActivity(intent);
    }

    // Sets appropriate layout if there are no elements in the list
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

    // Creates a row in the list for newly added city
    private void createEntry(CityWeatherObject item) {
        WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton
                .getInstance(this);
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

    private void showSnackbarUndo(final CityWeatherObject item) {
        String place = getResources().getString(R.string.activity_main_place);
        String deleted = getResources().getString(R.string.activity_main_deleted);
        String undo = getResources().getString(R.string.undo);

        Snackbar.make(mainLayout, place + " " + item.name + " " + deleted, Snackbar.LENGTH_LONG)
                .setAction(undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createEntry(item);
                    }
                }).show();
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
