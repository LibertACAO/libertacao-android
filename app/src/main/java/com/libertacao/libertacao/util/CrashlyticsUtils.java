package com.libertacao.libertacao.util;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

@SuppressWarnings("SameParameterValue")
public class CrashlyticsUtils {
    public static void log(String TAG, String message) {
        Exception e = new Exception(TAG + " - " + message);
        Crashlytics.logException(e);
        Log.w(TAG, e.getLocalizedMessage());
    }

    public static void log(String TAG, Exception e) {
        Crashlytics.logException(e);
        Log.w(TAG, e.getLocalizedMessage());
    }

    public static void log(String TAG, OutOfMemoryError e) {
        Crashlytics.logException(e);
        Log.w(TAG, e.getLocalizedMessage());
    }
}
