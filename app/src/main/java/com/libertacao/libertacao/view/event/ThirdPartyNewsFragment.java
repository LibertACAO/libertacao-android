package com.libertacao.libertacao.view.event;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.manager.ConnectionManager;
import com.libertacao.libertacao.manager.SyncManager;
import com.libertacao.libertacao.util.SnackbarUtils;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.view.customviews.EmptyRecyclerView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ThirdPartyNewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    /**
     * Interface elements
     */
    @InjectView(R.id.swipe_container) SwipeRefreshLayout mSwipeLayout;
    @InjectView(R.id.event_recycler_view) EmptyRecyclerView mRecyclerView;
    @InjectView(R.id.empty_event_list_textview) TextView mEmptyTextView;

    public static ThirdPartyNewsFragment newInstance() {
        return new ThirdPartyNewsFragment();
    }


    public ThirdPartyNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_third_party_news, container, false);
        ButterKnife.inject(this, layout);
        setupRecyclerView();
        return layout;
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

}
