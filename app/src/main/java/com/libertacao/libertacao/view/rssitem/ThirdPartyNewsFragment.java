package com.libertacao.libertacao.view.rssitem;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.databinding.RowRssItemBinding;
import com.libertacao.libertacao.manager.ConnectionManager;
import com.libertacao.libertacao.rss.RssItem;
import com.libertacao.libertacao.rss.RssReader;
import com.libertacao.libertacao.util.SnackbarUtils;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.view.customviews.ArrayAdapter;
import com.libertacao.libertacao.view.customviews.EmptyRecyclerView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

// TODO: add swipe refresh layout
public class ThirdPartyNewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    /**
     * Interface elements
     */
    @InjectView(R.id.swipe_container) SwipeRefreshLayout mSwipeLayout;
    @InjectView(R.id.event_recycler_view) EmptyRecyclerView mRecyclerView;
    @InjectView(R.id.empty_event_list_textview) TextView mEmptyTextView;
    ArrayAdapter<RssItem, RssItemViewHolder> adapter;

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
        // Configure adapter
        adapter = new ArrayAdapter<RssItem, RssItemViewHolder>(new ArrayList<RssItem>()) {
            @Override
            public RssItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                RowRssItemBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.row_rss_item,
                        parent,
                        false);
                return new RssItemViewHolder(binding);
            }

            @Override
            public void onBindViewHolder(RssItemViewHolder holder, int position) {
                holder.bindRssItem(getItem(position));
            }
        };

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

        new GetRssFeed().execute("http://feeds.feedburner.com/Anda-AgnciaDeNotciasDeDireitosAnimais?format=xml");
    }

    private class GetRssFeed extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                RssReader rssReader = new RssReader(params[0]);
                for (RssItem item : rssReader.getItems()) {
                    adapter.add(item);
                }
            } catch (Exception e) {
                Timber.e("Error Parsing Data: " + e.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(adapter);
//            mSwipeLayout.setRefreshing(false);
        }
    }

    /**
     * Required by SwipeRefreshLayout.OnRefreshListener interface. It is called when user manually refreshes RecyclerView.
     */

    @Override
    public void onRefresh() {
//        mSwipeLayout.setRefreshing(false);
        if (ConnectionManager.getInstance().isOnline(getActivity())) {
            new GetRssFeed().execute("http://feeds.feedburner.com/Anda-AgnciaDeNotciasDeDireitosAnimais?format=xml");
        } else {
            SnackbarUtils.showNoInternetConnectionSnackbar(getActivity(), mSwipeLayout);
            mSwipeLayout.setRefreshing(false);
        }
    }

}
