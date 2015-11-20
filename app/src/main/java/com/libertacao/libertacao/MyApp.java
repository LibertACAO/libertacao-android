package com.libertacao.libertacao;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.libertacao.libertacao.view.event.EditEventActivity;
import com.libertacao.libertacao.view.event.EventDetailActivity;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.squareup.leakcanary.AndroidExcludedRefs;
import com.squareup.leakcanary.DisplayLeakService;
import com.squareup.leakcanary.ExcludedRefs;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.leakcanary.ServiceHeapDumpListener;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

// TODO: add custom notification
// FIXME: melhorar navigation drawer layout
public class MyApp extends Application {
    // App context
    private static Context context;

    public static Context getAppContext() {
        return MyApp.context;
    }

    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            refWatcher = installLeakCanary();
        } else {
            Fabric.with(this, new Crashlytics());
        }
        MyApp.context = getApplicationContext();

        if(BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        setupParse();
    }

    private void setupParse() {
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
        ParseFacebookUtils.initialize(this);
    }

    /**
     * LeakCanary config
     * We need to set this manually while Google Play Services is not fixed.
     * This affects EventDetailActivity and EditEventActivity, so they are skipped in the process.
     * We also need to keep a reference to RefWatcher; if not, it would be deallocated and we would not receive any leaks.
     */

    @SuppressWarnings("unused")
    public static RefWatcher getRefWatcher(Context context) {
        MyApp application = (MyApp) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

    protected RefWatcher installLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        } else {
            ExcludedRefs excludedRefs = AndroidExcludedRefs.createAppDefaults().build();
            LeakCanary.enableDisplayLeakActivity(this);
            ServiceHeapDumpListener heapDumpListener = new ServiceHeapDumpListener(this, DisplayLeakService.class);
            final RefWatcher refWatcher = LeakCanary.androidWatcher(this, heapDumpListener, excludedRefs);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                    }

                    @Override
                    public void onActivityStarted(Activity activity) {

                    }

                    @Override
                    public void onActivityResumed(Activity activity) {

                    }

                    @Override
                    public void onActivityPaused(Activity activity) {

                    }

                    @Override
                    public void onActivityStopped(Activity activity) {

                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                    }

                    public void onActivityDestroyed(Activity activity) {
                        if (activity instanceof EventDetailActivity || activity instanceof EditEventActivity) {
                            return;
                        }
                        refWatcher.watch(activity);
                    }
                });
            }
            return refWatcher;
        }
    }
}
