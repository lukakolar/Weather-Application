package com.lukakolar.weatherapplication.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.lukakolar.weatherapplication.Constants;
import com.lukakolar.weatherapplication.Databases.CitiesDatabaseHandler;
import com.lukakolar.weatherapplication.Databases.DatabaseCallbacksInterface;
import com.lukakolar.weatherapplication.Entity.CityWeatherObject;
import com.lukakolar.weatherapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCityActivity extends AppCompatActivity {
    private boolean queryRunning = false;
    private String currentEnteredText = "";
    private Map<String, CityWeatherObject> nameToId = new HashMap<>();
    private CustomAutoCompleteTextView customAutoCompleteTextView;
    private ArrayAdapter<String> suggestionsAdapter;
    private CitiesDatabaseHandler citiesDatabaseHandler;
    private List<String> currentSuggestions;
    private RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        // ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // AutoComplete
        currentSuggestions = new ArrayList<>();
        customAutoCompleteTextView = (CustomAutoCompleteTextView)
                findViewById(R.id.customAutoCompleteTextView);
        customAutoCompleteTextView
                .addTextChangedListener(new CustomAutoCompleteTextChangedListener(this));
        suggestionsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, currentSuggestions);
        customAutoCompleteTextView.setAdapter(suggestionsAdapter);

        // MainLayout
        mainLayout = (RelativeLayout) findViewById(R.id.activity_add_city);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Creates loading popup when database is copying or opening
        final ProgressDialog progressDialog = createProgressDialog();

        // Handles callbacks from database handler
        citiesDatabaseHandler = new CitiesDatabaseHandler(AddCityActivity.this, new DatabaseCallbacksInterface() {
            @Override
            public void onEntriesRetrieved(List<CityWeatherObject> suggestions) {
                currentSuggestions.clear();
                for (CityWeatherObject suggestion : suggestions) {
                    currentSuggestions.add(suggestion.name);
                    nameToId.put(suggestion.name, suggestion);
                }
                suggestionsAdapter.notifyDataSetChanged();
                suggestionsAdapter = new ArrayAdapter<>(AddCityActivity.this,
                        android.R.layout.simple_dropdown_item_1line, currentSuggestions);
                customAutoCompleteTextView.setAdapter(suggestionsAdapter);
                queryRunning = false;
            }

            @Override
            public void onDatabaseOpened() {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        citiesDatabaseHandler.close();
    }


    private ProgressDialog createProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }


    void showSuggestions(final String searchTerm){
        currentEnteredText = searchTerm;
        if (!queryRunning && citiesDatabaseHandler != null && citiesDatabaseHandler.isDatabaseOpen()) {
            queryRunning = true;
            citiesDatabaseHandler.getSuggestions(searchTerm);
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showSuggestions(currentEnteredText);
                }
            }, 200);
        }
    }

    public void addCity(View v) {
        View view = this.getCurrentFocus();
        if (view != null) {
            // Hide keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        CityWeatherObject obj = nameToId.get(currentEnteredText);
        if (obj == null) {
            String ok = getResources().getString(R.string.ok);
            String youCannotSelectThisPlace =
                    getResources().getString(R.string.activity_add_city_you_cannot_select_this_place);

            Snackbar.make(mainLayout, youCannotSelectThisPlace, Snackbar.LENGTH_LONG)
                    .setAction(ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).show();
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Constants.CITIES_DATABASE_FIELD_NAME, obj.name);
            returnIntent.putExtra(Constants.CITIES_DATABASE_FIELD_ID, obj.id);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }
    }
}
