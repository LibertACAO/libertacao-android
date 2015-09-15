package com.libertacao.libertacao.view.notificacoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Call;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.List;

public class NotificacaoFragment extends ListFragment {

    public static NotificacaoFragment newInstance() {
        return new NotificacaoFragment();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NotificacaoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ParseQueryAdapter<Call> adapter = new NotificacaoParseQueryAdapter(getContext(), new ParseQueryAdapter.QueryFactory<Call>() {
            public ParseQuery<Call> create() {
                ParseQuery<Call> query = new ParseQuery<>("Call");
                //query.whereGreaterThanOrEqualTo("createdAt", new Date()); // TODO: should be a field indicating the due date
                // TODO: add something including location (whereNear?) - https://parse.com/docs/android/guide#geopoints
                query.orderByDescending("createdAt");
                return query;
            }
        });

        setListAdapter(adapter);
        adapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Call>() {
            @Override
            public void onLoading() {
                setEmptyText(getText(R.string.loading_notification_list));
            }

            @Override
            public void onLoaded(List<Call> objects, Exception e) {
                setEmptyText(getText(R.string.empty_notification_list));
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getActivity(), NotificacaoDetail.class);
        ParseObject parseObject = (ParseObject)getListAdapter().getItem(position);
        intent.putExtra(NotificacaoDetail.NOTIFICACAO_OBJECT_ID, parseObject.getObjectId());
        startActivity(intent);
    }
}
