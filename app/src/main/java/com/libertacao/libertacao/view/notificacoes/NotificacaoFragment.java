package com.libertacao.libertacao.view.notificacoes;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.support.OrmLiteCursorLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.persistence.DatabaseHelper;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.view.customviews.EmptyRecyclerView;
import com.libertacao.libertacao.view.customviews.RecyclerItemClickListener;

import java.sql.SQLException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NotificacaoFragment extends Fragment {
    private static final String TAG = "NotificacaoFragment";
    /**
     * Interface elements
     */
    @InjectView(R.id.event_recycler_view) EmptyRecyclerView mRecyclerView;
    @InjectView(R.id.empty_event_list_textview) TextView mEmptyTextView;

    public static NotificacaoFragment newInstance() {
        return new NotificacaoFragment();
    }

    public NotificacaoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.inject(this, layout);
        setupRecyclerView();
        return layout;
    }

    public void setupRecyclerView(){
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setEmptyView(mEmptyTextView);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        final EventRecyclerViewAdapter mAdapter = new EventRecyclerViewAdapter(getContext());
        mAdapter.setCallback(new EventRecyclerViewAdapter.Callback() {
            @Override
            public void onItemClick(Event event) {
                // TODO: not working, appears that onItemTouchListener is stoling this event :/
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, event.getTitle()); // TODO: define a better text
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getActivity(), EventDetail.class);
                        intent.putExtra(EventDetail.EVENT_ID, mAdapter.getTypedItem(position).getId());
                        startActivity(intent);
                    }
                })
        );

        final PreparedQuery<Event> preparedQuery;
        try {
            final Dao<Event, Integer> eventIntegerDao = DatabaseHelper.getHelper(getContext()).getEventIntegerDao();
            preparedQuery = DatabaseHelper.getHelper(getContext()).getEventPreparedQuery();

            LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                    return new OrmLiteCursorLoader<>(NotificacaoFragment.this.getContext(), eventIntegerDao, preparedQuery);
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

            getLoaderManager().initLoader(0, null, loaderCallbacks);
        } catch (SQLException e) {
            ViewUtils.showCriticalErrorMessageAndLogToCrashlytics(NotificacaoFragment.this.getContext(), mRecyclerView, TAG, e);
        }
    }
}
