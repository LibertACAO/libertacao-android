package com.libertacao.libertacao.util;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

@SuppressWarnings("SameParameterValue")
public class CrashlyticsUtils {
    public static void log(String TAG, String message) {
        Exception e = new Exception(TAG + " - " + message);
        if(Fabric.isInitialized()) {
            Crashlytics.logException(e);
        }
        Log.w(TAG, e.getLocalizedMessage());
    }

    public static void log(String TAG, Exception e) {
        if(Fabric.isInitialized()) {
            Crashlytics.logException(e);
        }
        Log.w(TAG, e.getLocalizedMessage());
    }

    public static void log(String TAG, OutOfMemoryError e) {
        if(Fabric.isInitialized()) {
            Crashlytics.logException(e);
        }
        Log.w(TAG, e.getLocalizedMessage());
    }
}
