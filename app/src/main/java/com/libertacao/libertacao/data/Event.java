package com.libertacao.libertacao.data;

import android.support.annotation.NonNull;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.libertacao.libertacao.MyApp;
import com.libertacao.libertacao.R;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.Date;

public class Event {
    public static final String EVENT = "Event";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LOCATION_SUMMARY = "locationSummary";
    public static final String LOCATION_DESCRIPTION = "locationDescription";
    public static final String LOCATION = "location";
    public static final int INVALID_LOCATION = -1;
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String IMAGE = "image";
    public static final String TYPE = "type";
    public static final String ENABLED = "enabled";
    public static final String INITIAL_DATE = "initialDate";
    public static final String END_DATE = "endDate";
    public static final String GOING = "going";
    public static final String LINK_URL = "linkUrl";
    public static final String LINK_TEXT = "linkText";
    public static final String UPDATED_AT = "updatedAt";

    @DatabaseField(generatedId = true, columnName = "_id")
    private int id;

    @DatabaseField
    private String objectId;

    @DatabaseField
    private String title;

    @DatabaseField
    private String description;

    // Location
    @DatabaseField
    private double latitude;

    @DatabaseField
    private double longitude;

    @DatabaseField
    private String locationSummary;

    @DatabaseField
    private String locationDescription;

    @DatabaseField
    private String image;

    @DatabaseField
    private Date initialDate;

    @DatabaseField
    private Date endDate;

    // 1 - Feira, 2 - Vaquinha, 3 - Outro
    @DatabaseField
    private int type;

    @DatabaseField
    private boolean enabled;

    @DatabaseField
    private boolean isGoing;

    @DatabaseField
    private long going;

    @DatabaseField
    private String linkUrl;

    @DatabaseField
    private String linkText;

    @DatabaseField
    private Date updatedAt;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date lastSynced;

    public Event(){

    }

    public Event(@NonNull String objectId, @NonNull String title, String description, double latitude, double longitude,
                 String locationSummary, String locationDescription, String image, @NonNull Date initialDate, Date endDate, int type,
                 boolean enabled, long going, String linkUrl, String linkText, Date updatedAt) {
        this.objectId = objectId;
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationSummary = locationSummary;
        this.locationDescription = locationDescription;
        this.image = image;
        this.initialDate = initialDate;
        this.endDate = endDate;
        this.type = type;
        this.lastSynced = new Date();
        this.enabled = enabled;
        this.isGoing = false;
        this.going = going;
        this.linkUrl = linkUrl;
        this.linkText = linkText;
        this.updatedAt = updatedAt;
    }

    public static Event getEvent(@NonNull ParseObject object){
        ParseGeoPoint location = object.getParseGeoPoint(LOCATION);
        ParseFile parseFile = object.getParseFile(IMAGE);
        return new Event(object.getObjectId(),
                object.getString(TITLE),
                object.getString(DESCRIPTION),
                location != null? location.getLatitude() : INVALID_LOCATION,
                location != null? location.getLongitude() : INVALID_LOCATION,
                object.getString(LOCATION_SUMMARY),
                object.getString(LOCATION_DESCRIPTION),
                parseFile != null? parseFile.getUrl() : null,
                object.getDate(INITIAL_DATE),
                // If end date is null, set it as initial date, so we can use it in our order by
                object.getDate(END_DATE) != null? object.getDate(END_DATE) : object.getDate(INITIAL_DATE),
                object.getInt(TYPE),
                object.getBoolean(ENABLED),
                (object.getNumber(GOING) != null)? object.getNumber(GOING).longValue() : 0,
                object.getString(LINK_URL),
                object.getString(LINK_TEXT),
                object.getUpdatedAt()
        );
    }

    public boolean isSynced() {
        return id != 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasImage() {
        return image != null;
    }

    public String getImage() {
        return image;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean hasLocation() {
        return latitude != INVALID_LOCATION && longitude != INVALID_LOCATION;
    }

    public String getLocationSummary() {
        return locationSummary;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public Date getInitialDate() {
        return initialDate;
    }

    public boolean hasInitialDate() {
        return initialDate != null;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    // It only has an end date if it is different from initial date (because if end date is null, we set it equals to initial date)
    public boolean hasEndDate() {
        return endDate != null && !endDate.equals(initialDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getType() {
        return type;
    }

    /**
     * Get a string array of available event types
     * @param isPlural indicating if requested strings should be pluralized or not
     * @return string array containing available event types
     */
    public static CharSequence[] getEventTypes(boolean isPlural) {
        int quantity = isPlural? 2 : 1;
        return new CharSequence[]{MyApp.getAppContext().getResources().getQuantityString(R.plurals.event, quantity),
                MyApp.getAppContext().getResources().getQuantityString(R.plurals.vakinha, quantity),
                MyApp.getAppContext().getResources().getQuantityString(R.plurals.petition, quantity),
                MyApp.getAppContext().getResources().getQuantityString(R.plurals.protest, quantity),
                MyApp.getAppContext().getResources().getQuantityString(R.plurals.singleNew, quantity),
                MyApp.getAppContext().getResources().getQuantityString(R.plurals.variado, quantity)};
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Number getGoing() {
        return going;
    }

    public boolean hasGoing() {
        return going != 0;
    }

    public boolean isGoing() {
        return isGoing;
    }

    public void setIsGoing(boolean isGoing) {
        this.isGoing = isGoing;
    }

    public void incrementGoing() {
        this.going++;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public String getLinkText() {
        return linkText;
    }

    @Override
    public String toString() {
        return title;
    }
}
