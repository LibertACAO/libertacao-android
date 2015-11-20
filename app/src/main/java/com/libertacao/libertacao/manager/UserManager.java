package com.libertacao.libertacao.manager;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.event.FirstLocationEncounteredEvent;
import com.libertacao.libertacao.persistence.UserPreferences;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class UserManager {
    private static final float DISTANCE_THRESHOLD = 1000; // in meters
    @Nullable private LatLng currentLatLng;

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

    @Nullable
    public LatLng getCurrentLatLng() {
        return currentLatLng;
    }

    public void setCurrentLatLng(@Nullable LatLng currentLatLng) {
        boolean shouldPostEvent = false;
        if(this.currentLatLng == null && currentLatLng != null) {
            shouldPostEvent = true;
        }

        if(currentLatLng == null) {
            UserPreferences.setLatitude(Event.INVALID_LOCATION);
            UserPreferences.setLongitude(Event.INVALID_LOCATION);
        } else {
            if(this.currentLatLng == null ||
                    distance((float)this.currentLatLng.latitude, (float) this.currentLatLng.longitude,
                            (float)currentLatLng.latitude, (float)currentLatLng.longitude) > DISTANCE_THRESHOLD) {
                // If we don't have a current latlng yet, or if the new received distance is from a distance greater than the threshold, update
                // ParseInstallation object and UserPreferences
                UserPreferences.setLatitude(currentLatLng.latitude);
                UserPreferences.setLongitude(currentLatLng.longitude);

                final double previousLatitude = this.currentLatLng != null? this.currentLatLng.latitude : Event.INVALID_LOCATION;
                final double previousLongitude = this.currentLatLng != null? this.currentLatLng.longitude : Event.INVALID_LOCATION;

                ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
                currentInstallation.put("location", new ParseGeoPoint(currentLatLng.latitude, currentLatLng.longitude));
                currentInstallation.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null) {
                            // Something bad occurred. Rollback UserPreferences
                            UserPreferences.setLatitude(previousLatitude);
                            UserPreferences.setLongitude(previousLongitude);
                            Timber.d("Rolling back saved locations in UserPreferences because could not get it saved in Parse.");
                        }
                    }
                });
            }
        }

        this.currentLatLng = currentLatLng;

        if(shouldPostEvent) {
            // Only post event after this..currentLatLng has the right value
            EventBus.getDefault().post(new FirstLocationEncounteredEvent());
        }
    }

    // Code from: http://stackoverflow.com/questions/8832071/how-can-i-get-the-distance-between-two-point-by-latlng
    private float distance (float lat_a, float lng_a, float lat_b, float lng_b ) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion);
    }
}
