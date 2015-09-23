package com.libertacao.libertacao.data;

import android.support.annotation.NonNull;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.Date;

public class Event {
    public static final String EVENT = "Event";

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
    // TODO: add in fragment a map - http://stackoverflow.com/questions/26562848/how-to-integrate-google-maps-in-android-app
    // http://stackoverflow.com/questions/26174527/android-mapview-in-fragment
    @DatabaseField
    private double latitude;

    @DatabaseField
    private double longitude;

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
                 String locationDescription, String image, @NonNull Date initialDate, Date endDate, int type) {
        this.objectId = objectId;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationDescription = locationDescription;
        this.image = image;
        this.initialDate = initialDate;
        this.endDate = endDate;
        this.type = type;
        this.lastSynced = new Date();
    }

    public static Event getEvent(@NonNull ParseObject object){
        ParseGeoPoint location = object.getParseGeoPoint("location");
        return new Event(object.getObjectId(),
                object.getString("title"),
                object.getString("subtitle"),
                object.getString("description"),
                location.getLatitude(),
                location.getLongitude(),
                object.getString("locationDescription"),
                object.getParseFile("image").getUrl(),
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

    public String getImage() {
        return image;
    }
}
