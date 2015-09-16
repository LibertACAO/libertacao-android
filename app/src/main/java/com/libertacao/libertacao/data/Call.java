package com.libertacao.libertacao.data;

import android.databinding.BindingAdapter;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseObject;

@ParseClassName("Call")
public class Call extends ParseObject {
    public Call(){

    }

    @SuppressWarnings("unused")
    public String getDescription() {
        return getString("description");
    }

    public String getShortText() {
        return getString("shortText");
    }

    public String getTitle() {
        return getString("title");
    }

    @SuppressWarnings("unused")
    public ParseFile getImageFile(){
        return getParseFile("image");
    }

    @SuppressWarnings("unused")
    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
        // TODO: add in fragment a map - http://stackoverflow.com/questions/26562848/how-to-integrate-google-maps-in-android-app
        // http://stackoverflow.com/questions/26174527/android-mapview-in-fragment
    }

    // Used by DataBinding library. TODO: This should ideally be in a separated ViewModel file
    @SuppressWarnings("unused")
    @BindingAdapter({"bind:imageFile"})
    public static void loadImage(ParseImageView imageView, ParseFile imageFile) {
        if (imageFile != null) {
            imageView.setParseFile(imageFile);
            imageView.loadInBackground();
        }
    }
}
