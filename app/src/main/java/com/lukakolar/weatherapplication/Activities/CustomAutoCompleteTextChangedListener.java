package com.lukakolar.weatherapplication.Activities;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

class CustomAutoCompleteTextChangedListener implements TextWatcher {
    private Context context;

    CustomAutoCompleteTextChangedListener(Context context) {
        this.context = context;
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {
        AddCityActivity AddCityActivity = ((AddCityActivity) context);
        AddCityActivity.showSuggestions(userInput.toString());
    }
}
