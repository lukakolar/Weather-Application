package com.lukakolar.weatherapplication.REST;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lukakolar.weatherapplication.Constants;
import com.lukakolar.weatherapplication.Databases.WeatherUpdatesDatabaseHandlerSingleton;
import com.lukakolar.weatherapplication.Entity.CityWeatherObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SwipeRefreshLayoutUpdater {
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConnectivityManager connectivityManager;
    private SwipeRefreshLayoutCallbacksInterface swipeRefreshLayoutCallbacksInterface;

    public SwipeRefreshLayoutUpdater(Context context, SwipeRefreshLayout swipeRefreshLayout,
                                     SwipeRefreshLayoutCallbacksInterface
                                             swipeRefreshLayoutCallbacksInterface) {
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.swipeRefreshLayoutCallbacksInterface = swipeRefreshLayoutCallbacksInterface;
        init();
    }

    private void init() {
        // Permissions
        checkForInternetPermission();
        checkForAccessNetworkState();

        // Connectivity Checking
        connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swipeRefreshLayout.setRefreshing(true);
                        refreshLogic();
                    }
                }
        );
    }

    private boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void checkForInternetPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest
                    .permission.INTERNET}, Constants.PERMISSION_REQUEST_INTERNET);
        }
    }

    private void checkForAccessNetworkState() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest
                    .permission.ACCESS_NETWORK_STATE}, Constants
                    .PERMISSION_REQUEST_ACCESS_NETWORK_STATE);
        }
    }

    private void refreshLogic() {
        if (isNetworkAvailable()) {
            requestUpdate();
        } else {
            swipeRefreshLayoutCallbacksInterface.onConnectionUnavailable();
        }
    }

    public void requestUpdate() {
        swipeRefreshLayout.setRefreshing(true);
        WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton
                .getInstance(context);
        ArrayList<Integer> ids = w.getCityIds();
        String ids_in_string = "";
        for (int i = 0; i < ids.size(); i++) {
            if (i != 0) {
                ids_in_string += ",";
            }
            ids_in_string += ids.get(i).toString();
        }
        String url = "http://api.openweathermap.org/data/2.5/group?id=" + ids_in_string +
                "&units=metric&appid=b57664c26a490d9081628b8d40ee5ef6";

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                WeatherUpdatesDatabaseHandlerSingleton w = WeatherUpdatesDatabaseHandlerSingleton
                        .getInstance(context);

                Log.d("response", response.toString());

                try {
                    Integer number_of_responses = response.getInt("cnt");
                    JSONArray cities = response.getJSONArray("list");
                    for (int i = 0; i < number_of_responses; i++) {
                        JSONObject city = cities.getJSONObject(i);
                        String description = city.getJSONArray("weather").getJSONObject(0)
                                .getString("description");
                        String temperature = String.valueOf(city.getJSONObject("main").getDouble
                                ("temp"));
                        temperature = String.valueOf(Math.round(10 * Double.parseDouble
                                (temperature)) / 10.0);
                        String humidity = String.valueOf(city.getJSONObject("main").getDouble
                                ("humidity"));
                        Integer id = city.getInt("id");
                        String name = city.getString("name");
                        w.updateEntry(new CityWeatherObject(id, name, temperature, humidity,
                                description));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<CityWeatherObject> result = w.getSavedCities();
                swipeRefreshLayoutCallbacksInterface.onResponseSuccess(result);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                swipeRefreshLayoutCallbacksInterface.onResponseError();
            }
        });
        jsObjRequest.setTag(Constants.VOLLEY_REQUEST_TAG);
        SingletonRequestQueue.getInstance(context).addToRequestQueue(jsObjRequest);
    }
}
