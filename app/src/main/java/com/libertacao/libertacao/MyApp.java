package com.libertacao.libertacao;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.squareup.leakcanary.LeakCanary;

public class MyApp extends Application {
    public static boolean IS_DEBUG = false;

    // App context
    private static Context context;

    public static Context getAppContext() {
        return MyApp.context;
    }

    public void onCreate() {
        super.onCreate();
        // TODO: add Fabric.with(this, new Crashlytics());
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
                    .penaltyDeath()
                    .build());
        }

        // TODO - ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));

        setupParse();
    }

    private void setupParse() {
        Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseUser.enableAutomaticUser();
        ParseUser.getCurrentUser().increment("RunCount");
        ParseUser.getCurrentUser().saveInBackground();
    }
}
