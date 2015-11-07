package com.libertacao.libertacao.view.event;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.Toast;

import com.j256.ormlite.android.apptools.support.OrmLiteCursorLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.event.SyncedEvent;
import com.libertacao.libertacao.manager.ConnectionManager;
import com.libertacao.libertacao.manager.LoginManager;
import com.libertacao.libertacao.manager.SyncManager;
import com.libertacao.libertacao.persistence.DatabaseHelper;
import com.libertacao.libertacao.util.SnackbarUtils;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.view.admin.EditEventActivity;
import com.libertacao.libertacao.view.customviews.EmptyRecyclerView;

import java.sql.SQLException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class EventFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "NotificacaoFragment";

    // Array to keep track of current filter
    private int selectedFilter = 0;

    private boolean loaderInitied = false;

    /**
     * Interface elements
     */
    @InjectView(R.id.swipe_container) SwipeRefreshLayout mSwipeLayout;
    @InjectView(R.id.event_recycler_view) EmptyRecyclerView mRecyclerView;
    @InjectView(R.id.empty_event_list_textview) TextView mEmptyTextView;

    public static EventFragment newInstance() {
        return new EventFragment();
    }

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_fragment_menu, menu);
        inflater.inflate(R.menu.add_event_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                CharSequence[] eventTypes = Event.getEventTypes(true);
                CharSequence[] items = new CharSequence[eventTypes.length + 1];
                items[0] = getString(R.string.all);
                System.arraycopy(eventTypes, 0, items, 1, eventTypes.length);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.filter));
                builder.setSingleChoiceItems(items, selectedFilter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedFilter = which;
                        setupAdapterAndLoader();
                        setupEmptyText();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getString(android.R.string.cancel), null);
                builder.show();
                return true;
            case R.id.menu_add_event:
                if(LoginManager.getInstance().isLoggedIn()) {
                    startActivity(EditEventActivity.newIntent(getContext()));
                } else {
                    Toast.makeText(getContext(), getString(R.string.mustBeLoggedIn), Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.inject(this, layout);
        mEmptyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFilter != 0) {
                    selectedFilter = 0;
                    setupAdapterAndLoader();
                    setupEmptyText();
                }
            }
        });
        setupRecyclerView();
        EventBus.getDefault().register(this);
        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void setupEmptyText() {
        if(selectedFilter == 0) {
            mEmptyTextView.setText(R.string.empty_event_list);
        } else {
            mEmptyTextView.setText(R.string.empty_event_list_with_filter);
        }
    }

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
            preparedQuery = DatabaseHelper.getHelper(getContext()).getEventPreparedQuery(selectedFilter);

            // Using LoaderManager to change cursor when some data change in database
            LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                    return new OrmLiteCursorLoader<>(EventFragment.this.getContext(), eventIntegerDao, preparedQuery);
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
            ViewUtils.showCriticalErrorMessageAndLogToCrashlytics(EventFragment.this.getContext(), mRecyclerView, TAG, e);
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
