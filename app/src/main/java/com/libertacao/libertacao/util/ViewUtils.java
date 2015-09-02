package com.libertacao.libertacao.util;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.libertacao.libertacao.R;

public class ViewUtils {
    public static void setHomeAsUpEnabled(AppCompatActivity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }
}
