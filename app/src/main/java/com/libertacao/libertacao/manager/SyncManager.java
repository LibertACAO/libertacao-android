package com.libertacao.libertacao.manager;

import android.content.Context;
import android.support.annotation.NonNull;

public class SyncManager {
    private static final SyncManager ourInstance = new SyncManager();

    public static SyncManager getInstance() {
        return ourInstance;
    }

    private SyncManager() {
    }

    public void sync(@NonNull final Context context){
        EventManager.getInstance().sync(context);
    }
}
