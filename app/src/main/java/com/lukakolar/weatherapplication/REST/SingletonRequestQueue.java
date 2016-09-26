package com.lukakolar.weatherapplication.REST;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

class SingletonRequestQueue {
    private static SingletonRequestQueue mInstance;
    private RequestQueue mRequestQueue;
    private Context mCtx;

    private SingletonRequestQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    static synchronized SingletonRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SingletonRequestQueue(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}