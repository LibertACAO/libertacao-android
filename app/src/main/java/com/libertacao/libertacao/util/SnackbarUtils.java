package com.libertacao.libertacao.util;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.libertacao.libertacao.R;

public class SnackbarUtils {
    public static void showDefaultSnackbar(View layout, String message){
        Snackbar snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackBarView.setBackgroundColor(Color.GRAY);
        snackbar.show();
    }

    public static void showNoInternetConnectionSnackbar(Context context, View layout){
        showDefaultSnackbar(layout, context.getString(R.string.no_internet_connection));
    }

    public static void showCriticalErrorSnackbar(Context context, View layout){
        showDefaultSnackbar(layout, context.getString(R.string.critical_error));
    }
}
