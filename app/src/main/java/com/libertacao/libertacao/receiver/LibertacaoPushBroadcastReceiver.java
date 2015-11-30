package com.libertacao.libertacao.receiver;

import android.content.Context;
import android.content.Intent;

import com.libertacao.libertacao.persistence.UserPreferences;
import com.parse.ParsePushBroadcastReceiver;

public class LibertacaoPushBroadcastReceiver extends ParsePushBroadcastReceiver {
    // TODO: group notifications
    // TODO: add default actions, like going
    // TODO: add delete actions, like hide notifications today
    // TODO: add action to open details activity when receiving a push related to an event

    protected void onPushReceive(Context context, Intent intent) {
        if(UserPreferences.isNotificationEnabled()) {
            super.onPushReceive(context, intent);
        }
    }
}
