package com.libertacao.libertacao.util;

import com.libertacao.libertacao.data.Event;

public class ShareUtils {
    public static String getEventShareText(Event event){
        return event.getTitle() + " - " + event.getDescription();
    }
}
