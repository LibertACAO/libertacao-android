package com.libertacao.libertacao.view.notificacoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.libertacao.libertacao.data.Call;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

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
        ParseQueryAdapter<Call> adapter = new NotificacaoParseQueryAdapter(getActivity(), Call.class);
        setListAdapter(adapter);
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
