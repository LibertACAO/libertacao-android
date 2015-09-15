package com.libertacao.libertacao.data;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Call")
public class Call extends ParseObject {
    public Call(){

    }

    public String getDescription() {
        return getString("description");
    }

    public String getShortText() {
        return getString("shortText");
    }

    public String getTitle() {
        return getString("title");
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
        // TODO: add in fragment a map - http://stackoverflow.com/questions/26562848/how-to-integrate-google-maps-in-android-app
        // http://stackoverflow.com/questions/26174527/android-mapview-in-fragment
    }
}
