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
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;

// TODO: add custom notification
// TODO: melhorar navigation drawer layout
// TODO: add something including location (whereNear?) - https://parse.com/docs/android/guide#geopoints
public class MyApp extends Application {
    // App context
    private static Context context;

    public static Context getAppContext() {
        return MyApp.context;
    }

    public void onCreate() {
        super.onCreate();
        if(!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        LeakCanary.install(this);
        MyApp.context = getApplicationContext();

        if(BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
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
        ParseFacebookUtils.initialize(this);

        if(LoginManager.getInstance().isLoggedIn()) {
            ParseGeoPoint.getCurrentLocationInBackground(10 * 1000, //ms
                    new LocationCallback() {
                        @Override
                        public void done(ParseGeoPoint geoPoint, ParseException e) {
                            // TODO: not sure if this is working
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
