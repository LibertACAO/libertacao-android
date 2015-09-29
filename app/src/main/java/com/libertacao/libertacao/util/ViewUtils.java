package com.libertacao.libertacao.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.libertacao.libertacao.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

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

    public static void setSwipeColorSchemeResources(SwipeRefreshLayout swipeLayout) {
        swipeLayout.setColorSchemeResources(R.color.holo_blue_bright,
                R.color.holo_green_light,
                R.color.holo_orange_light,
                R.color.holo_red_light);
    }

    // Display image options
    private static DisplayImageOptions displayImageOptions;
    public static DisplayImageOptions getFadeInDisplayImageOptions(){
        if(displayImageOptions == null){
            displayImageOptions = new DisplayImageOptions.Builder().displayer(new FadeInBitmapDisplayer(500)).build();
        }
        return displayImageOptions;
    }
}
