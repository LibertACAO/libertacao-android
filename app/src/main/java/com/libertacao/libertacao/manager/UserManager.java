package com.libertacao.libertacao.manager;

import com.google.android.gms.maps.model.LatLng;
import com.libertacao.libertacao.view.event.EventFirstLocationEncountered;

import de.greenrobot.event.EventBus;

public class UserManager {
    private LatLng currentLatLng;

    private static UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
    }

    public LatLng getCurrentLatLng() {
        return currentLatLng;
    }

    public void setCurrentLatLng(LatLng currentLatLng) {
        if(this.currentLatLng == null) {
            // TODO: send an event via eventBus, in case when creating filter "near me" we didn't have yet the current location
            EventBus.getDefault().post(new EventFirstLocationEncountered());
        }
        this.currentLatLng = currentLatLng;
    }
}
