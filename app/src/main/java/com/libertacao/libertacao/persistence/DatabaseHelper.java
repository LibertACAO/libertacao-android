package com.libertacao.libertacao.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.DataConfig;
import com.libertacao.libertacao.data.Event;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    public static final int LAST_SYNCED_THRESHOLD_IN_MINUTES = -30; // time that instances have before being deleted by next sync

    private static final int DATABASE_VERSION = 1;
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

    public PreparedQuery<Event> getEventPreparedQuery(int selectedFilter) throws SQLException {
        QueryBuilder<Event, Integer> queryBuilder = getEventIntegerRuntimeExceptionDao().queryBuilder();

        if(selectedFilter != 0) {
            queryBuilder.where().eq(Event.TYPE, selectedFilter);
        }
        queryBuilder.orderBy(Event.INITIAL_DATE, true);

        // TODO: add where clause to only filter events from today to future
        // TODO: add something including location (whereNear?) - https://parse.com/docs/android/guide#geopoints
        return queryBuilder.prepare();
    }
}
