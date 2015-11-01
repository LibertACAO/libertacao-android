package com.libertacao.libertacao.viewmodel;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.libertacao.libertacao.BR;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.util.MyDateUtils;

import java.util.Calendar;
import java.util.Locale;

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
     * Store selected event initial date
     */
    private Calendar initialDateCalendar;

    /**
     * Store selected event end date
     */
    private Calendar endDateCalendar;

    /**
     * Complete constructor to build an EventDataModel
     * @param context given context
     * @param onSelectImageClickListener listener to be called when user has clicked on Select image
     * @param event related event
     */
    public EditEventDataModel(@NonNull Context context, @NonNull OnSelectImageClick onSelectImageClickListener, @NonNull Event event) {
        super(context, event);
        this.onSelectImageClickListener = onSelectImageClickListener;
        initialDateCalendar = Calendar.getInstance(new Locale("pt", "BR"));
        if(event.hasInitialDate()) {
            initialDateCalendar.setTime(event.getInitialDate());
        }
        endDateCalendar = Calendar.getInstance(new Locale("pt", "BR"));
        if(event.hasEndDate()) {
            endDateCalendar.setTime(event.getEndDate());
        }
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
     * Called when user clicked on the image
     * @param view target
     */
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
    @Bindable
    public String getInitialDate() {
        if(event.hasInitialDate()) {
            return MyDateUtils.getDayMonthDateToUser(event.getInitialDate());
        } else {
            return "";
        }
    }

    /**
     * Provides event initial hour
     * @return event initial hour
     */
    @Bindable
    public String getInitialHour() {
        if(event.hasInitialDate()) {
            return MyDateUtils.getHourMinuteDateToUser(event.getInitialDate());
        } else {
            return "";
        }
    }

    /**
     * Provides event end date
     * @return event end date
     */
    @Bindable
    public String getEndDate() {
        if(event.hasEndDate()) {
            return MyDateUtils.getDayMonthDateToUser(event.getEndDate());
        } else {
            return "";
        }
    }

    /**
     * Provides event end hour
     * @return event end hour
     */
    @Bindable
    public String getEndHour() {
        if(event.hasEndDate()) {
            return MyDateUtils.getHourMinuteDateToUser(event.getEndDate());
        } else {
            return "";
        }
    }

    /**
     * Listener to initial date dialog
     */
    private DatePickerDialog.OnDateSetListener onInitialDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            initialDateCalendar.set(Calendar.YEAR, year);
            initialDateCalendar.set(Calendar.MONTH, monthOfYear);
            initialDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            event.setInitialDate(initialDateCalendar.getTime());
            notifyPropertyChanged(BR.initialDate);
        }
    };

    /**
     * Called when user clicked on initial date EditText
     * @param view target
     */
    public void onInitialDateClick(View view){
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, onInitialDateSetListener,
                initialDateCalendar.get(Calendar.YEAR), initialDateCalendar.get(Calendar.MONTH),
                initialDateCalendar.get(Calendar.DAY_OF_MONTH));
        if (event.hasEndDate() && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            datePickerDialog.getDatePicker().setMaxDate(endDateCalendar.getTimeInMillis());
        }
        datePickerDialog.show();
    }

    /**
     * Listener to initial hour dialog
     */
    private TimePickerDialog.OnTimeSetListener onInitialHourTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            initialDateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            initialDateCalendar.set(Calendar.MINUTE, minute);
            event.setInitialDate(initialDateCalendar.getTime());
            notifyPropertyChanged(BR.initialHour);
        }
    };

    /**
     * Called when user clicked on initial date EditText
     * @param view target
     */
    public void onInitialHourClick(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, onInitialHourTimeSetListener,
                initialDateCalendar.get(Calendar.HOUR_OF_DAY), initialDateCalendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    /**
     * Listener to end date dialog
     */
    private DatePickerDialog.OnDateSetListener onEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            endDateCalendar.set(Calendar.YEAR, year);
            endDateCalendar.set(Calendar.MONTH, monthOfYear);
            endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            event.setEndDate(endDateCalendar.getTime());
            notifyPropertyChanged(BR.endDate);
        }
    };

    /**
     * Called when user clicked on end date EditText
     * @param view target
     */
    public void onEndDateClick(View view){
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, onEndDateSetListener,
                endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH),
                endDateCalendar.get(Calendar.DAY_OF_MONTH));
        if (event.hasEndDate() && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            datePickerDialog.getDatePicker().setMinDate(initialDateCalendar.getTimeInMillis());
        }
        datePickerDialog.show();
    }

    /**
     * Listener to end hour dialog
     */
    private TimePickerDialog.OnTimeSetListener onEndHourTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            endDateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            endDateCalendar.set(Calendar.MINUTE, minute);
            event.setEndDate(endDateCalendar.getTime());
            notifyPropertyChanged(BR.endHour);
        }
    };

    /**
     * Called when user clicked on end hour EditText
     * @param view target
     */
    public void onEndHourClick(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, onEndHourTimeSetListener,
                endDateCalendar.get(Calendar.HOUR_OF_DAY), endDateCalendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }
}
