package com.libertacao.libertacao.viewmodel;

import android.content.Context;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.view.View;

import com.libertacao.libertacao.BR;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.util.MyDateUtils;

/**
 * This class is the ViewModel of the MVVM architecture pattern, representing an Event edition
 */
@SuppressWarnings("unused")
public class EditEventDataModel extends EventDataModel {

    /**
     * Hold on OnSelectImageClick to delegate when user has clicked on Select image
     */
    private OnSelectImageClick onSelectImageClickListener;

    /**
     * This contains the path to the event local image, in case it was selected a new one (e.g. in the edit event activity)
     */
    private String eventLocalImage;

    /**
     * Complete constructor to build an EventDataModel
     * @param context given context
     * @param onSelectImageClickListener listener to be called when user has clicked on Select image
     * @param event related event
     */
    public EditEventDataModel(@NonNull Context context, @NonNull OnSelectImageClick onSelectImageClickListener, @NonNull Event event) {
        super(context, event);
        this.onSelectImageClickListener = onSelectImageClickListener;
    }

    /**
     * Provides underlying event
     * @return underlying event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Provides event image URL. If it has a local image, return it; otherwise, return event image.
     * @return event image URL
     */
    @Override
    @Bindable
    public String getImage() {
        if(eventLocalImage != null) {
            return "file://" + eventLocalImage;
        } else {
            return event.getImage();
        }
    }

    /**
     * Returns if the image is visible or gone
     * @return View.VISIBLE if there is an image; View.GONE otherwise so the view does not take space
     */
    @Override
    public int isImageVisible() {
        if(eventLocalImage != null) {
            return View.VISIBLE;
        } else {
            return event.hasImage() ? View.VISIBLE : View.GONE;
        }
    }

    /**
     * Called when user clicked on the image (this is only accessible to admin while creating or editing an event)
     * @param view target
     */
    @SuppressWarnings("unused")
    public void onImageClick(View view){
        if(onSelectImageClickListener != null) {
            onSelectImageClickListener.onSelectImageClick();
        }
    }

    /**
     * Interface to communicate when user has clicked to select an image
     */

    public interface OnSelectImageClick {
        void onSelectImageClick();
    }

    /**
     * Set a local image to this event
     * @param eventLocalImage image path
     */
    public void setEventLocalImage(String eventLocalImage) {
        this.eventLocalImage = eventLocalImage;
        notifyPropertyChanged(BR.image);
    }

    /**
     * Provides event initial date
     * @return event initial date
     */
    public String getInitialDate() {
        if(event.hasInitialDate()) {
            return MyDateUtils.getDateMonthHourMinuteDateToUser(event.getInitialDate());
        } else {
            return "";
        }
    }

    /**
     * Provides event end date
     * @return event end date
     */
    public String getEndDate() {
        if(event.hasEndDate()) {
            return MyDateUtils.getDateMonthHourMinuteDateToUser(event.getEndDate());
        } else {
            return "";
        }
    }
}
