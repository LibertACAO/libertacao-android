package com.libertacao.libertacao.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.event.SyncedEvent;
import com.libertacao.libertacao.persistence.DatabaseHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class EventManager {

    private static final EventManager ourInstance = new EventManager();

    public static EventManager getInstance() {
        return ourInstance;
    }

    private EventManager() {
    }

    public void sync(@NonNull final Context context){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Event.EVENT);
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
                                    Timber.d("Finished processing events");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Timber.d("Error while processing events: " + e.getLocalizedMessage());
                                }

                                @Override
                                public void onNext(ParseObject parseObject) {
                                    DatabaseHelper.getHelper(context).createIfNotExists(Event.getEvent(parseObject));
                                }
                            });
                } else {
                    Timber.e("Error: " + e.getMessage());
                }

                EventBus.getDefault().post(new SyncedEvent());
            }
        });
    }
}
