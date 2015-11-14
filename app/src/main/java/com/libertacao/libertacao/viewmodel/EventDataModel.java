package com.libertacao.libertacao.viewmodel;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;

import com.libertacao.libertacao.BR;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.persistence.DatabaseHelper;
import com.libertacao.libertacao.util.MyDateUtils;
import com.libertacao.libertacao.util.MyImageLoader;
import com.libertacao.libertacao.util.ShareUtils;
import com.libertacao.libertacao.view.event.EventDetailActivity;
import com.parse.ParseObject;

/**
 * This class is the ViewModel of the MVVM architecture pattern, representing an Event
 */
@SuppressWarnings("unused")
public class EventDataModel extends BaseObservable {
    /**
     * Hold on to activity
     */
    protected final Activity activity;

    /**
     * Related event. This has all data that we need.
     */
    protected Event event;

    /**
     * Image view of this event
     */
    private ImageView eventImageView;

    /**
     * Complete constructor to build an EventDataModel
     * @param activity given activity
     * @param event related event
     * @param eventImageView image view of this event
     */
    public EventDataModel(@NonNull Activity activity, @NonNull Event event, @Nullable ImageView eventImageView) {
        this.activity = activity;
        this.event = event;
        this.eventImageView = eventImageView;
    }

    /**
     * Standard constructor to build an EventDataModel
     * @param activity given activity
     * @param event related event
     */
    public EventDataModel(@NonNull Activity activity, @NonNull Event event){
        this(activity, event, null);
    }

    /**
     * Provides title attribute
     * @return event title
     */
    public String getTitle() {
        return event.getTitle();
    }

    /**
     * Provides description attribute
     * @return event description
     */
    public String getDescription() {
        return event.getDescription();
    }

    /**
     * States if this event is enabled or not
     * @return if event is enabled or not
     */
    public boolean isEnabled() {
        return event.isEnabled();
    }

    /**
     * Provides event image URL
     * @return event image URL
     */
    @Bindable
    public String getImage() {
        return event.getImage();
    }

    /**
     * Returns if the image is visible or gone
     * @return View.VISIBLE if there is an image; View.GONE otherwise so the view does not take space
     */
    public int isImageVisible() {
        return event.hasImage() ? View.VISIBLE : View.GONE;
    }

    /**
     * Provides event date
     * @return event date
     */
    public String getDate() {
        if(event.hasInitialDate()) {
            if (event.hasEndDate()) {
                // Has initial and end date
                if(MyDateUtils.isSameDay(event.getInitialDate(), event.getEndDate())) {
                    // If both dates are from the same day
                    return String.format(activity.getString(R.string.eventInitialAndEndDateSameDay),
                            MyDateUtils.getDateMonthHourMinuteDateToUser(event.getInitialDate()),
                            MyDateUtils.getHourMinuteDateToUser(event.getEndDate()));
                } else {
                    // If dates are from different days
                    return String.format(activity.getString(R.string.eventInitialAndEndDateDifferentDay),
                            MyDateUtils.getDayMonthDateToUser(event.getInitialDate()),
                            MyDateUtils.getDayMonthDateToUser(event.getEndDate()));
                }
            } else {
                // Has only initial date
                if(DateUtils.isToday(event.getInitialDate().getTime())) {
                    // If it is today, use prettytime to give a relative time
                    return String.format(activity.getString(R.string.eventInitialDateToday),
                            MyDateUtils.getDateMonthHourMinuteDateToUser(event.getInitialDate()),
                            MyDateUtils.getPrettyTime().format(event.getInitialDate()));
                } else {
                    // If it is not today, only show the date
                    return MyDateUtils.getDateMonthHourMinuteDateToUser(event.getInitialDate());
                }
            }
        } else {
            return "";
        }
    }

    /**
     * Provides event location summary
     * @return event location summary
     */
    public String getLocationSummary() {
        return event.getLocationSummary();
    }

    /**
     * Returns if the location summary is visible or gone
     * @return View.VISIBLE if there is a location summary; View.GONE otherwise so the view does not take space
     */
    public int isLocationSummaryVisible() {
        return TextUtils.isEmpty(event.getLocationSummary())? View.GONE : View.VISIBLE;
    }

    /**
     * Provides event location description
     * @return event location description
     */
    public String getLocationDescription() {
        return event.getLocationDescription();
    }

    /**
     * Returns if the location description is visible or gone
     * @return View.VISIBLE if there is a location description; View.GONE otherwise so the view does not take space
     */
    public int isLocationDescriptionVisible() {
        return TextUtils.isEmpty(event.getLocationDescription())? View.GONE : View.VISIBLE;
    }

    /**
     * Returns if the location is visible or gone
     * @return View.VISIBLE if there is a latitude and a longitude; View.GONE otherwise so the view does not take space
     */
    public int isLocationVisible() {
        return (event.hasLocation())? View.VISIBLE : View.GONE;
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
     * Set a new event image view. It allows recycling EventDataModels within the RecyclerView adapter.
     * @param eventImageView new event image view
     */
    public void setEventImageView(ImageView eventImageView) {
        this.eventImageView = eventImageView;
    }

    /**
     * Load image into imageView
     * @param imageView target
     * @param image source
     */
    @BindingAdapter({"bind:image"})
    public static void loadImage(@NonNull ImageView imageView, @Nullable String image) {
        MyImageLoader.getInstance().displayEventImage(image, imageView);
    }

    /**
     * Called when user pressed this event
     * @param view target
     */
    public void onEventClick(View view){
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (event.hasImage() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, eventImageView, "eventImage");
            activity.startActivity(EventDetailActivity.newIntent(activity, event), options.toBundle());
        } else {
            activity.startActivity(EventDetailActivity.newIntent(activity, event));
        }
    }

    /**
     * Called when user shared this event
     * @param view target
     */
    public void onShareClick(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, ShareUtils.getEventShareText(event));
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }
    /**
     * Called when user clicked on the Going text view
     * @param view target
     */
    public void onGoingClick(View view){
        ParseObject parseEvent = new ParseObject(Event.EVENT);
        parseEvent.setObjectId(event.getObjectId());
        parseEvent.increment(Event.GOING);
        parseEvent.saveInBackground();
        event.setIsGoing(true);
        event.incrementGoing();
        DatabaseHelper.getHelper(activity).getEventIntegerRuntimeExceptionDao().update(event);
        notifyPropertyChanged(BR.goingVisible);
        notifyPropertyChanged(BR.goingNumber);
        notifyPropertyChanged(BR.numberGoingVisible);
    }

    /**
     * Returns if going text view is visible or gone
     * @return View.VISIBLE if user is not going; View.GONE otherwise so the view does not take space
     */
    @Bindable
    public int getGoingVisible() {
        return (event.isGoing())? View.GONE : View.VISIBLE;
    }

    /**
     * Provides event location summary
     * @return event location summary
     */
    @Bindable
    public String getGoingNumber() {
        return String.format(activity.getString(R.string.goingNumber),String.valueOf(event.getGoing()));
    }

    /**
     * Returns if number going text view is visible or gone
     * @return View.VISIBLE if there is at least one user going; View.GONE otherwise so the view does not take space
     */
    @Bindable
    public int getNumberGoingVisible() {
        return (event.hasGoing())? View.VISIBLE : View.GONE;
    }
}
