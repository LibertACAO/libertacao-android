package com.libertacao.libertacao.manager;

import android.support.annotation.NonNull;

import com.libertacao.libertacao.data.Event;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

// Convert between ParseObjects and our objects
public class ParserDtoManager {

    // Convert Event to ParseObject
    public static ParseObject getParseObjectFrom(@NonNull  Event event) {
        ParseObject parseObject = new ParseObject(Event.EVENT);

        if(event.isSynced()) {
            parseObject.setObjectId(event.getObjectId());
            parseObject.put(Event.ENABLED, event.isEnabled());
        } else {
            parseObject.put(Event.ENABLED, false);
        }
        parseObject.put(Event.TITLE, event.getTitle());
        parseObject.put(Event.DESCRIPTION, event.getDescription());
        if(event.getLocationSummary() != null) {
            parseObject.put(Event.LOCATION_SUMMARY, event.getLocationSummary());
        }
        if(event.getLocationDescription() != null) {
            parseObject.put(Event.LOCATION_DESCRIPTION, event.getLocationDescription());
        }
        return parseObject;
    }

    // Convert ParseObject to Event
    public static Event getEventFromParseObject(@NonNull ParseObject parseObject){
        ParseGeoPoint location = parseObject.getParseGeoPoint(Event.LOCATION);
        ParseFile parseFile = parseObject.getParseFile(Event.IMAGE);
        return new Event(parseObject.getObjectId(),
                parseObject.getString(Event.TITLE),
                parseObject.getString(Event.DESCRIPTION),
                location != null? location.getLatitude() : Event.INVALID_LOCATION,
                location != null? location.getLongitude() : Event.INVALID_LOCATION,
                parseObject.getString(Event.LOCATION_SUMMARY),
                parseObject.getString(Event.LOCATION_DESCRIPTION),
                parseFile != null? parseFile.getUrl() : null,
                parseObject.getDate(Event.INITIAL_DATE),
                // If end date is null, set it as initial date, so we can use it in our order by
                parseObject.getDate(Event.END_DATE) != null? parseObject.getDate(Event.END_DATE) : parseObject.getDate(Event.INITIAL_DATE),
                parseObject.getInt(Event.TYPE),
                parseObject.getBoolean(Event.ENABLED),
                (parseObject.getNumber(Event.GOING) != null)? parseObject.getNumber(Event.GOING).longValue() : 0,
                parseObject.getString(Event.LINK_URL),
                parseObject.getString(Event.LINK_TEXT),
                parseObject.getUpdatedAt()
        );
    }
}
