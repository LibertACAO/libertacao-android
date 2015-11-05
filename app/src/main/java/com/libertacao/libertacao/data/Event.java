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
    public static final String IMAGE = "image";
    public static final String TYPE = "type";
    public static final String ENABLED = "enabled";
    public static final String INITIAL_DATE = "initialDate";
    public static final String END_DATE = "endDate";

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

    @SuppressWarnings("FieldCanBeLocal")
    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date lastSynced;

    public Event(){

    }

    public Event(@NonNull String objectId, @NonNull String title, String description, double latitude, double longitude,
                 String locationSummary, String locationDescription, String image, @NonNull Date initialDate, Date endDate, int type,
                 boolean enabled) {
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
    }

    public static Event getEvent(@NonNull ParseObject object){
        ParseGeoPoint location = object.getParseGeoPoint(LOCATION);
        ParseFile parseFile = object.getParseFile(IMAGE);
        return new Event(object.getObjectId(),
                object.getString(TITLE),
                object.getString(DESCRIPTION),
                location != null? location.getLatitude() : -1,
                location != null? location.getLongitude() : -1,
                object.getString(LOCATION_SUMMARY),
                object.getString(LOCATION_DESCRIPTION),
                parseFile != null? parseFile.getUrl() : null,
                object.getDate(INITIAL_DATE),
                object.getDate(END_DATE),
                object.getInt(TYPE),
                object.getBoolean(ENABLED));
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
        return latitude != -1 && longitude != -1;
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

    public boolean hasEndDate() {
        return endDate != null;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getType() {
        return type;
    }

    public static CharSequence[] getEventTypes() {
        return new CharSequence[]{MyApp.getAppContext().getString(R.string.event),
                MyApp.getAppContext().getString(R.string.vaquinha),
                MyApp.getAppContext().getString(R.string.petition),
                MyApp.getAppContext().getString(R.string.protest),
                MyApp.getAppContext().getString(R.string.singleNew),
                MyApp.getAppContext().getString(R.string.variado)};
    }

    public boolean isEnabled() {
        return enabled;
    }
}
