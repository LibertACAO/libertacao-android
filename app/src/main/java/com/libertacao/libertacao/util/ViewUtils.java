package com.libertacao.libertacao.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.libertacao.libertacao.R;

public class ViewUtils {
    public static void setHomeAsUpEnabled(@NonNull AppCompatActivity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    public static void showCriticalErrorMessageAndLogToCrashlytics(Context context, View layout, String TAG, Exception e) {
        SnackbarUtils.showCriticalErrorSnackbar(context, layout);
        CrashlyticsUtils.log(TAG, e);
    }
}
