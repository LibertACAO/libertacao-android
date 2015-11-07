package com.libertacao.libertacao.manager;

import com.google.android.gms.maps.model.LatLng;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.persistence.UserPreferences;
import com.libertacao.libertacao.event.FirstLocationEncounteredEvent;

import de.greenrobot.event.EventBus;

public class UserManager {
    private LatLng currentLatLng;

    private static UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
        double latitude = UserPreferences.getLatitude();
        double longitude = UserPreferences.getLongitude();
        if(latitude != Event.INVALID_LOCATION && longitude != Event.INVALID_LOCATION) {
            currentLatLng = new LatLng(latitude, longitude);
        }
    }

    public LatLng getCurrentLatLng() {
        return currentLatLng;
    }

    public void setCurrentLatLng(LatLng currentLatLng) {
        if(this.currentLatLng == null) {
            EventBus.getDefault().post(new FirstLocationEncounteredEvent());
        }
        this.currentLatLng = currentLatLng;
        UserPreferences.setLatitude(this.currentLatLng.latitude);
        UserPreferences.setLongitude(this.currentLatLng.longitude);
    }
}
