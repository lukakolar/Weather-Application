package com.lukakolar.weatherapplication.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lukakolar.weatherapplication.R;

public class AddCityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void addCity(View v) {
        View view = this.getCurrentFocus();
        if (view != null) {
            // Hide keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        EditText text = (EditText) findViewById(R.id.activity_add_city_text_view);
        if (text != null) {
            String cityName = text.getText().toString();
            if (!cityName.equals("")) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("name", cityName);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        }
    }
}
