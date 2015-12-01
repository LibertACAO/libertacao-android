package com.libertacao.libertacao.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.DataConfig;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.manager.UserManager;
import com.libertacao.libertacao.util.MyDateUtils;
import com.libertacao.libertacao.view.event.EventFragment;
import com.libertacao.libertacao.view.event.EventFragmentBase;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final int LAST_SYNCED_THRESHOLD_IN_MINUTES = -30; // time that instances have before being deleted by next sync

    private static final int DATABASE_VERSION = 7;
    private static final String TAG = "DatabaseHelper";
    private Dao<Event, Integer> eventIntegerDao = null;
    private RuntimeExceptionDao<Event, Integer> eventIntegerRuntimeExceptionDao = null;

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getHelper(@NonNull Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(@NonNull Context context) {
        super(context, context.getPackageName(), null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Event.class);
        } catch (SQLException e) {
            Log.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");

            TableUtils.dropTable(connectionSource, Event.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Event, Integer> getEventIntegerDao() throws SQLException {
        if (eventIntegerDao == null) {
            eventIntegerDao = getDao(Event.class);
        }
        return eventIntegerDao;
    }

    public RuntimeExceptionDao<Event, Integer> getEventIntegerRuntimeExceptionDao() {
        if (eventIntegerRuntimeExceptionDao == null) {
            eventIntegerRuntimeExceptionDao = getRuntimeExceptionDao(Event.class);
        }
        return eventIntegerRuntimeExceptionDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        eventIntegerDao = null;
        eventIntegerRuntimeExceptionDao = null;
    }

    public void clearAll() {
        getEventIntegerRuntimeExceptionDao().delete(getEventIntegerRuntimeExceptionDao().queryForAll());
    }

    /**
     * Utility methods
     */

    public void createIfNotExists(@NonNull Event object) {
        List<Event> objects = getEventIntegerRuntimeExceptionDao().queryForEq(DataConfig.OBJECT_ID, object.getObjectId());
        if (objects.isEmpty()) {
            getEventIntegerRuntimeExceptionDao().create(object);
        } else {
            object.setId(objects.get(0).getId());
            object.setIsGoing(objects.get(0).isGoing());
            getEventIntegerRuntimeExceptionDao().update(object);
        }
    }

    public void deleteEventsNotRecentlySynced() {
        Calendar sometimeInThePast = Calendar.getInstance();
        sometimeInThePast.add(Calendar.MINUTE, LAST_SYNCED_THRESHOLD_IN_MINUTES);
        try {
            DeleteBuilder<Event, Integer> deleteBuilder = getEventIntegerRuntimeExceptionDao().deleteBuilder();
            deleteBuilder.where().lt(DataConfig.LAST_SYNCED, sometimeInThePast.getTime());
            int delete = deleteBuilder.delete();
            Log.d(TAG, "Deleted " + delete + " rows in deleteEventsNotRecentlySynced");
        } catch (SQLException e) {
            Log.e(TAG, "Exception when deleteEventsNotRecentlySynced: " + e.getLocalizedMessage());
        }
    }

    /**
     * Prepared queries
     */

    public PreparedQuery<Event> getEventPreparedQuery(int selectedCategory, int selectedOrderBy) throws SQLException {
        QueryBuilder<Event, Integer> queryBuilder = getEventIntegerRuntimeExceptionDao().queryBuilder();

        if(selectedCategory == EventFragmentBase.ADMIN) {
            queryBuilder.where().eq(Event.ENABLED, false);
            return queryBuilder.prepare();
        }

        // Where
        Where<Event, Integer> where = queryBuilder.where();

        // There is not an end date and the initial date is greater than yesterday
        where.isNull(Event.END_DATE);
        where.gt(Event.INITIAL_DATE, MyDateUtils.getYesterdayDate());
        where.and(2);

        // OR
        // There is an end date and the end date is greater than yesterday
        where.isNotNull(Event.END_DATE);
        where.gt(Event.END_DATE, MyDateUtils.getYesterdayDate());
        where.and(2);

        where.or(2);

        // Check selected filter and add clauses accordingly
        if(selectedCategory != EventFragmentBase.ALL) {
            // Add near me events
            LatLng currentLatLng = UserManager.getInstance().getCurrentLatLng();
            if(currentLatLng != null) {
                // Filter by near me

                // Event doesn't have a location (latitude/longitude)
                where.eq(Event.LATITUDE, Event.INVALID_LOCATION);
                where.eq(Event.LONGITUDE, Event.INVALID_LOCATION);
                where.or(2);

                // OR
                // Is near me
                where.gt(Event.LATITUDE, currentLatLng.latitude - DataConfig.LOCATION_OFFSET);
                where.lt(Event.LATITUDE, currentLatLng.latitude + DataConfig.LOCATION_OFFSET);
                where.gt(Event.LONGITUDE, currentLatLng.longitude - DataConfig.LOCATION_OFFSET);
                where.lt(Event.LONGITUDE, currentLatLng.longitude + DataConfig.LOCATION_OFFSET);
                where.and(4);

                where.or(2);

                // And with already existing where
                where.and(2);

                Timber.d("Filtered by near me (" + currentLatLng.latitude + "," + currentLatLng.longitude + ")");
            } else {
                Timber.d("Could not use 'Near me' filter because user doesn't have a valid location");
            }
        }

        if(selectedCategory >= EventFragmentBase.EVENT && selectedCategory <= EventFragmentBase.OTHERS) {
            where.eq(Event.TYPE, selectedCategory);
            where.and(2);
        }

        // Does not query not enabled events
        where.eq(Event.ENABLED, true);
        where.and(2);

        queryBuilder.setWhere(where);

        // Order by
        if(selectedOrderBy == EventFragment.END_DATE) {
            queryBuilder.orderBy(Event.END_DATE, true);
        } else if(selectedOrderBy == EventFragment.LAST_UPDATED) {
            queryBuilder.orderBy(Event.UPDATED_AT, false);
        } else if(selectedOrderBy == EventFragment.MOST_POPULAR) {
            queryBuilder.orderBy(Event.GOING, false);
        }

        return queryBuilder.prepare();
    }
}
