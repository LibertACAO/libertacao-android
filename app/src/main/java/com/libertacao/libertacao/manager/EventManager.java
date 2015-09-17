package com.libertacao.libertacao.manager;

import android.content.Context;
import android.util.Log;

import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.persistence.DatabaseHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class EventManager {
    private final static String TAG = "EventManager";

    private static EventManager ourInstance = new EventManager();

    public static EventManager getInstance() {
        return ourInstance;
    }

    private EventManager() {
    }

    public void sync(final Context context){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Event.EVENT);
        //query.whereEqualTo("playerName", "Dan Stemkoski");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> events, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + events.size() + " events");
                    Observable.from(events)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(Schedulers.io())
                            .subscribe(new Subscriber<ParseObject>() {
                                @Override
                                public void onCompleted() {
                                    DatabaseHelper.getHelper(context).deleteEventsNotRecentlySynced();
                                    Log.d(TAG, "Finished processing evets");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d(TAG, "Error while processing evets: " + e.getLocalizedMessage());
                                }

                                @Override
                                public void onNext(ParseObject parseObject) {
                                    DatabaseHelper.getHelper(context).createIfNotExists(Event.getEvent(parseObject));
                                }
                            });
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }
}
