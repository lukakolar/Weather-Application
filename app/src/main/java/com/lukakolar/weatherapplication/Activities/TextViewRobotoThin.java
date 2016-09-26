package com.lukakolar.weatherapplication.Activities;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewRobotoThin extends TextView {
    public TextViewRobotoThin(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createFont();
    }

    public TextViewRobotoThin(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }

    public TextViewRobotoThin(Context context) {
        super(context);
        createFont();
    }

    public void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Thin.ttf");
        setTypeface(font);
    }
}
