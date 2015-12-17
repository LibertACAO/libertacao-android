package com.libertacao.libertacao.push;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.libertacao.libertacao.MyApp;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.persistence.UserPreferences;
import com.libertacao.libertacao.view.event.EventDetailActivity;
import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;

import timber.log.Timber;

public class LibertacaoPushBroadcastReceiver extends BroadcastReceiver {
    // TODO: group notifications
    // TODO: add default actions, like going
    // TODO: add delete actions, like hide notifications today

    /**
     * The name of the Intent extra which contains the JSON payload of the Notification.
     */
    public static final String KEY_PUSH_DATA = "com.parse.Data";

    /**
     * The name of the Intent fired when a push has been received.
     */
    public static final String ACTION_PUSH_RECEIVE = "com.parse.push.intent.RECEIVE";

    /**
     * The name of the Intent fired when a notification has been opened.
     */
    public static final String ACTION_PUSH_OPEN = "com.parse.push.intent.OPEN";

    /**
     * The name of the Intent fired when a notification has been dismissed.
     */
    public static final String ACTION_PUSH_DELETE = "com.parse.push.intent.DELETE";

    protected static final int SMALL_NOTIFICATION_MAX_CHARACTER_LIMIT = 38;

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        switch (intentAction) {
            case ACTION_PUSH_RECEIVE:
                onPushReceive(context, intent);
                break;
            case ACTION_PUSH_DELETE:
                onPushDismiss(context, intent);
                break;
            case ACTION_PUSH_OPEN:
                onPushOpen(context, intent);
                break;
        }
    }

    protected void onPushReceive(Context context, Intent intent) {
        if (!UserPreferences.isNotificationEnabled()) {
            return;
        }

        JSONObject pushData = null;
        try {
            pushData = new JSONObject(intent.getStringExtra(KEY_PUSH_DATA));
        } catch (JSONException | NullPointerException e) {
            Timber.e("Unexpected exception when receiving push data: " + e);
        }

        // If the push data includes an action string, that broadcast intent is fired.
        String action = null;
        if (pushData != null) {
            action = pushData.optString("action", null);
        }
        if (action != null) {
            Bundle extras = intent.getExtras();
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtras(extras);
            broadcastIntent.setAction(action);
            broadcastIntent.setPackage(context.getPackageName());
            context.sendBroadcast(broadcastIntent);
        }

        Notification notification = getNotification(context, intent);

        if (notification != null) {
            PushNotificationManager.getInstance().showNotification(context, notification);
        }
    }

    protected void onPushDismiss(Context context, Intent intent) {
        // do nothing
    }

    protected void onPushOpen(Context context, Intent intent) {
        // Send a Parse Analytics "push opened" event
        ParseAnalytics.trackAppOpenedInBackground(intent);

        String uriString = null;
        String eventObjectId = null;
        try {
            JSONObject pushData = new JSONObject(intent.getStringExtra(KEY_PUSH_DATA));
            uriString = pushData.optString("uri", null);
            eventObjectId = pushData.optString("eventObjectId", null);
        } catch (JSONException e) {
            Timber.e("Unexpected JSONException when receiving push data: " + e);
        }

        Class<? extends Activity> cls = getActivity(context, intent);
        Intent activityIntent;
        if (uriString != null) {
            activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        } else if(!TextUtils.isEmpty(eventObjectId)) {
            activityIntent = new Intent(EventDetailActivity.newIntent(context, eventObjectId));
        } else {
            activityIntent = new Intent(context, cls);
        }

        activityIntent.putExtras(intent.getExtras());
    /*
      In order to remove dependency on android-support-library-v4
      The reason why we differentiate between versions instead of just using context.startActivity
      for all devices is because in API 11 the recommended conventions for app navigation using
      the back key changed.
     */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilderHelper.startActivities(context, cls, activityIntent);
        } else {
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(activityIntent);
        }
    }

    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            return null;
        }
        String className = launchIntent.getComponent().getClassName();
        Class<? extends Activity> cls = null;
        try {
            cls = (Class<? extends Activity>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            // do nothing
        }
        return cls;
    }

    protected int getSmallIconId(Context context, Intent intent) {
        return R.mipmap.ic_launcher;
    }

    protected Bitmap getLargeIcon(Context context, Intent intent) {
        return null;
    }

    private JSONObject getPushData(Intent intent) {
        try {
            return new JSONObject(intent.getStringExtra(KEY_PUSH_DATA));
        } catch (JSONException e) {
            Timber.e("Unexpected JSONException when receiving push data: " + e);
            return null;
        }
    }

    protected Notification getNotification(Context context, Intent intent) {
        JSONObject pushData = getPushData(intent);
        if (pushData == null || (!pushData.has("alert") && !pushData.has("title"))) {
            return null;
        }

        String title = pushData.optString("title", MyApp.getAppContext().getString(R.string.app_name));
        String alert = pushData.optString("alert", "Notification received.");
        String tickerText = String.format(Locale.getDefault(), "%s: %s", title, alert);

        Bundle extras = intent.getExtras();

        Random random = new Random();
        int contentIntentRequestCode = random.nextInt();
        int deleteIntentRequestCode = random.nextInt();

        // Security consideration: To protect the app from tampering, we require that intent filters
        // not be exported. To protect the app from information leaks, we restrict the packages which
        // may intercept the push intents.
        String packageName = context.getPackageName();

        Intent contentIntent = new Intent(ParsePushBroadcastReceiver.ACTION_PUSH_OPEN);
        contentIntent.putExtras(extras);
        contentIntent.setPackage(packageName);

        Intent deleteIntent = new Intent(ParsePushBroadcastReceiver.ACTION_PUSH_DELETE);
        deleteIntent.putExtras(extras);
        deleteIntent.setPackage(packageName);

        PendingIntent pContentIntent = PendingIntent.getBroadcast(context, contentIntentRequestCode,
                contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pDeleteIntent = PendingIntent.getBroadcast(context, deleteIntentRequestCode,
                deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // The purpose of setDefaults(Notification.DEFAULT_ALL) is to inherit notification properties
        // from system defaults
        NotificationCompat.Builder parseBuilder = new NotificationCompat.Builder(context);
        parseBuilder.setContentTitle(title)
                .setContentText(alert)
                .setTicker(tickerText)
                .setSmallIcon(this.getSmallIconId(context, intent))
                .setLargeIcon(this.getLargeIcon(context, intent))
                .setContentIntent(pContentIntent)
                .setDeleteIntent(pDeleteIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);
        if (alert != null
                && alert.length() > SMALL_NOTIFICATION_MAX_CHARACTER_LIMIT) {
            parseBuilder.setStyle(new NotificationCompat.Builder.BigTextStyle().bigText(alert));
        }
        return parseBuilder.build();
    }
}
