package com.libertacao.libertacao.data;

import android.support.annotation.NonNull;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.Date;

public class Event {
    public static final String EVENT = "Event";
    public static final String TYPE = "type";
    public static final String INITIAL_DATE = "initialDate";
    public static final String END_DATE = "endDate";

    @DatabaseField(generatedId = true, columnName = "_id")
    private int id;

    @DatabaseField
    private String objectId;

    @DatabaseField
    private String title;

    @DatabaseField
    private String subtitle;

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

    // 1 - Feira, 2 - Vaquinha
    @DatabaseField
    private int type;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date lastSynced;

    public Event(){

    }

    public Event(@NonNull String objectId, @NonNull String title, String subtitle, String description, double latitude, double longitude,
                 String locationSummary, String locationDescription, String image, @NonNull Date initialDate, Date endDate, int type) {
        this.objectId = objectId;
        this.title = title;
        this.subtitle = subtitle;
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
    }

    public static Event getEvent(@NonNull ParseObject object){
        ParseGeoPoint location = object.getParseGeoPoint("location");
        ParseFile parseFile = object.getParseFile("image");
        return new Event(object.getObjectId(),
                object.getString("title"),
                object.getString("subtitle"),
                object.getString("description"),
                location != null? location.getLatitude() : -1,
                location != null? location.getLongitude() : -1,
                object.getString("locationSummary"),
                object.getString("locationDescription"),
                parseFile != null? parseFile.getUrl() : null,
                object.getDate("initialDate"),
                object.getDate("endDate"),
                object.getInt("type"));
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

    public String getSubtitle() {
        return subtitle;
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

    public Date getEndDate() {
        return endDate;
    }

    public boolean hasEndDate() {
        return endDate != null;
    }
}
