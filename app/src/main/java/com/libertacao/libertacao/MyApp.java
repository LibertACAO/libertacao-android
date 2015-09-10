package com.libertacao.libertacao;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.libertacao.libertacao.data.Call;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.leakcanary.LeakCanary;
import io.fabric.sdk.android.Fabric;

public class MyApp extends Application {
    public static boolean IS_DEBUG = false;

    // App context
    private static Context context;

    public static Context getAppContext() {
        return MyApp.context;
    }

    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        LeakCanary.install(this);
        MyApp.context = getApplicationContext();
        if (context.getPackageName().endsWith(".debug")) {
            IS_DEBUG = true;
        }

        if(IS_DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }

        setupParse();
    }

    private void setupParse() {
        ParseObject.registerSubclass(Call.class);
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseUser.enableAutomaticUser();
        ParseUser.getCurrentUser().increment("RunCount");
        ParseUser.getCurrentUser().saveInBackground();
    }
}
