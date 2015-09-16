package com.libertacao.libertacao.manager;

import android.content.Context;

public class EventManager {
    private static EventManager ourInstance = new EventManager();

    public static EventManager getInstance() {
        return ourInstance;
    }

    private EventManager() {
    }

    public void sync(final Context context){
        // TODO
    }
}
