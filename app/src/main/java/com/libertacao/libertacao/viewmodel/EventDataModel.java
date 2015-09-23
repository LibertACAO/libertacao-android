package com.libertacao.libertacao.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.view.notificacoes.EventDetail;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * This class is the ViewModel of the MVVM architecture pattern. It provides data for row_notificao.xml layout.
 */
public class EventDataModel extends BaseObservable {
    /**
     * Hold on context to perform start activities
     */
    private Context context;

    /**
     * Related event. This has all data that we need.
     */
    private Event event;

    /**
     * Standard constructor to build an EventDataModel
     * @param context given context
     * @param event related event
     */
    public EventDataModel(@NonNull Context context, @NonNull Event event){
        this.context = context;
        this.event = event;
    }

    /**
     * Provides title attribute
     * @return event title
     */
    public String getTitle() {
        return event.getTitle();
    }

    /**
     * Provides subtitle attribute
     * @return event subtitle
     */
    public String getSubtitle() {
        return event.getSubtitle();
    }

    /**
     * Provides description attribute
     * @return event description
     */
    public String getDescription() {
        return event.getDescription();
    }

    /**
     * Provides event image URL
     * @return event image URL
     */
    public String getImage() {
        return event.getImage();
    }

    /**
     * Set a new event to this EventDataModel. It allows recycling EventDataModels within the RecyclerView adapter.
     * This takes advantage of the BaseObservable superclass to call notifyChange to update UI accordingly. Easy :)
     * @param event new event
     */
    public void setEvent(Event event) {
        this.event = event;
        notifyChange();
    }

    /**
     * Following methods are all used by DataBinding library
     */

    /**
     * Load image into imageView
     * @param imageView target
     * @param image source
     */
    @SuppressWarnings("unused")
    @BindingAdapter({"bind:image"})
    public static void loadImage(@NonNull ImageView imageView, @Nullable String image) {
        if (image != null) {
            ImageLoader.getInstance().displayImage(image, imageView);
        }
    }

    /**
     * Called when user pressed this event
     * @param view target
     */
    @SuppressWarnings("unused")
    public void onEventClick(View view){
        Intent intent = new Intent(context, EventDetail.class);
        intent.putExtra(EventDetail.EVENT_ID, event.getId());
        context.startActivity(intent);
    }

    /**
     * Called when user shared this event
     * @param view target
     */
    @SuppressWarnings("unused")
    public void onShareClick(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, event.getTitle() + " - " + event.getDescription()); // TODO: define a better text
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }
}
