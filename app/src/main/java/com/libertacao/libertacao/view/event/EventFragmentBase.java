package com.libertacao.libertacao.view.event;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.support.OrmLiteCursorLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.event.SyncedEvent;
import com.libertacao.libertacao.manager.ConnectionManager;
import com.libertacao.libertacao.manager.SyncManager;
import com.libertacao.libertacao.persistence.DatabaseHelper;
import com.libertacao.libertacao.util.SnackbarUtils;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.view.customviews.EmptyRecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.SQLException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class EventFragmentBase extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // Category
    public static final String CATEGORY = "CATEGORY";
    public static final int ALL = 0;
    public static final int EVENT = 1;
    public static final int VAKINHAS = 2;
    public static final int PETITIONS = 3;
    public static final int PROTEST = 4;
    public static final int COMMUNITY_NEWS = 5;
    public static final int OTHERS = 6;
    public static final int NEAR_ME = 7;
    public static final int THIRD_PARTY_NEWS = 8;
    public static final int ADMIN = 9;

    @IntDef({ALL, EVENT, VAKINHAS, PETITIONS, PROTEST, COMMUNITY_NEWS, OTHERS, NEAR_ME, THIRD_PARTY_NEWS, ADMIN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Category {}

    /*@Category*/ private int selectedCategory = NEAR_ME;

    // Order by
    // TODO: this should be in EventFragment to be shared between fragments
    public static final int ORDER_BY_INITIAL_DATE = 0;
    public static final int ORDER_BY_LAST_UPDATED = 1;
    public static final int ORDER_BY_MOST_POPULAR = 2;
    public static final int DEFAULT_ORDER_BY_FILTER = ORDER_BY_INITIAL_DATE;
    private int selectedOrderBy = DEFAULT_ORDER_BY_FILTER;

    private boolean loaderInitied = false;

    /**
     * Interface elements
     */
    @InjectView(R.id.swipe_container) SwipeRefreshLayout mSwipeLayout;
    @InjectView(R.id.event_recycler_view) EmptyRecyclerView mRecyclerView;
    @InjectView(R.id.empty_event_list_textview) TextView mEmptyTextView;

    public static EventFragmentBase newInstance(@Category int selectedFilter) {
        EventFragmentBase eventFragmentBase = new EventFragmentBase();
        Bundle args = new Bundle();
        args.putInt(CATEGORY, selectedFilter);
        eventFragmentBase.setArguments(args);
        return eventFragmentBase;
    }

    public EventFragmentBase() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_events_base, container, false);
        Bundle args = getArguments();
        selectedCategory = args.getInt(CATEGORY, NEAR_ME);
        ButterKnife.inject(this, layout);
        setupRecyclerView();
        EventBus.getDefault().register(this);
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_order_by_event:
                CharSequence[] orderByItems = new CharSequence[3];
                orderByItems[0] = getString(R.string.orderByInitialDate);
                orderByItems[1] = getString(R.string.orderByLastUpdated);
                orderByItems[2] = getString(R.string.orderByMostPopular);
                AlertDialog.Builder orderByBuilder = new AlertDialog.Builder(getContext());
                orderByBuilder.setTitle(getString(R.string.orderBy));
                orderByBuilder.setSingleChoiceItems(orderByItems, selectedOrderBy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedOrderBy = which;
                        setupAdapterAndLoader();
                        dialog.dismiss();
                    }
                });
                orderByBuilder.setNegativeButton(getString(android.R.string.cancel), null);
                orderByBuilder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper UI methods
     */

    private void setupRecyclerView() {
        // Configure Swipe Refresh component
        mSwipeLayout.setOnRefreshListener(this);
        ViewUtils.setSwipeColorSchemeResources(mSwipeLayout);

        // Configure Recycler View
        // Changes in Recycler View content does not change it itself
        mRecyclerView.setEmptyView(mEmptyTextView);

        // Linear Vertical layout (like old ListView)
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        setupAdapterAndLoader();
    }

    private void setupAdapterAndLoader() {
        // Create and set adapter (data source)
        final EventRecyclerViewAdapter mAdapter = new EventRecyclerViewAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        final PreparedQuery<Event> preparedQuery;
        try {
            final Dao<Event, Integer> eventIntegerDao = DatabaseHelper.getHelper(getContext()).getEventIntegerDao();
            preparedQuery = DatabaseHelper.getHelper(getContext()).getEventPreparedQuery(selectedCategory, selectedOrderBy);

            // Using LoaderManager to change cursor when some data change in database
            LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                    return new OrmLiteCursorLoader<>(EventFragmentBase.this.getContext(), eventIntegerDao, preparedQuery);
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    mAdapter.changeCursor(cursor, ((OrmLiteCursorLoader<Event>) loader).getQuery());
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    mAdapter.changeCursor(null, null);
                }
            };

            if(!loaderInitied) {
                getLoaderManager().initLoader(0, null, loaderCallbacks);
                loaderInitied = true;
            } else {
                getLoaderManager().restartLoader(0, null, loaderCallbacks);
            }
        } catch (SQLException e) {
            ViewUtils.showCriticalErrorMessageAndLogToCrashlytics(EventFragmentBase.this.getContext(), mRecyclerView, "EventFragment", e);
        }
    }

    /**
     * Required by SwipeRefreshLayout.OnRefreshListener interface. It is called when user manually refreshes RecyclerView.
     */

    @Override
    public void onRefresh() {
        if (ConnectionManager.getInstance().isOnline(getActivity())) {
            SyncManager.getInstance().sync(getContext());
        } else {
            SnackbarUtils.showNoInternetConnectionSnackbar(getActivity(), mSwipeLayout);
            mSwipeLayout.setRefreshing(false);
        }
    }

    /**
     * Events
     */

    @SuppressWarnings("unused")
    public void onEventMainThread(SyncedEvent event) {
        mSwipeLayout.setRefreshing(false);
    }
}
