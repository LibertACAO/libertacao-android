package com.libertacao.libertacao.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Call")
public class Call extends ParseObject {
    public Call(){

    }

    public String getDescription() {
        return getString("description");
    }

    public String getShortText() {
        return getString("shortText");
    }

    public String getTitle() {
        return getString("title");
    }
}
