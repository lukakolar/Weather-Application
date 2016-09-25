package com.lukakolar.weatherapplication.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.lukakolar.weatherapplication.Constants;
import com.lukakolar.weatherapplication.Entity.CityWeatherObject;
import com.lukakolar.weatherapplication.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private TextView textNoItems;

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

        checkIfEmpty();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ADD_CITY_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                String name = data.getStringExtra("name");
                recyclerViewAdapter.add(new CityWeatherObject(null, name, null, null, null));
                checkIfEmpty();
            }
        }
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
}
