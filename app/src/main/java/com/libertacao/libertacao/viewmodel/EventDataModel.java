package com.libertacao.libertacao.viewmodel;

import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.libertacao.libertacao.data.Event;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EventDataModel {
    private Event event;

    public EventDataModel(@NonNull Event event){
        this.event = event;
    }

    public String getTitle() {
        return event.getTitle();
    }

    public String getSubtitle() {
        return event.getSubtitle();
    }

    public String getDescription() {
        return event.getDescription();
    }

    public String getImage() {
        return event.getImage();
    }

    // Used by DataBinding library
    @SuppressWarnings("unused")
    @BindingAdapter({"bind:image"})
    public static void loadImage(@NonNull ImageView imageView, @Nullable String image) {
        if (image != null) {
            ImageLoader.getInstance().displayImage(image, imageView);
        }
    }
}
