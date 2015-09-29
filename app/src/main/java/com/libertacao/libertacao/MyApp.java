package com.libertacao.libertacao;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.libertacao.libertacao.manager.LoginManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.LocationCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;

// TODO: convert to new layout Sign-up screen
// TODO: add facebook
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
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
    }

    private void setupParse() {
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));

        if(LoginManager.getInstance().isLoggedIn()) {
            ParseGeoPoint.getCurrentLocationInBackground(10 * 1000, //ms
                    new LocationCallback() {
                        @Override
                        public void done(ParseGeoPoint geoPoint, ParseException e) {
                            Log.d("MyApp", "Found geopoint: " + geoPoint);
                            if (geoPoint != null) {
                                ParseUser.getCurrentUser().put("location", geoPoint);
                                ParseUser.getCurrentUser().saveInBackground();
                            }
                        }
                    });
        }
    }
}
