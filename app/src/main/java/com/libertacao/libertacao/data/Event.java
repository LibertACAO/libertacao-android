package com.libertacao.libertacao.data;

import com.j256.ormlite.field.DatabaseField;

public class Event {
    @DatabaseField(generatedId = true, columnName = "_id")
    private int id;

    @DatabaseField
    private String objectId;
}
