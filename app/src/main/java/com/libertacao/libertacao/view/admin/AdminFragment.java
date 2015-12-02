package com.libertacao.libertacao.view.admin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.view.event.EditEventActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdminFragment extends Fragment {

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_event_menu, menu);
        inflater.inflate(R.menu.send_push_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_event:
                startActivity(EditEventActivity.newIntent(getContext()));
                return true;
            case R.id.menu_send_push:
                startActivity(SendPushActivity.newIntent(getContext()));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_admin, container, false);
        ButterKnife.inject(this, layout);
        return layout;
    }

    @OnClick(R.id.create_new_event_button)
    public void createNewEvent() {
        startActivity(EditEventActivity.newIntent(getContext()));
    }

    @OnClick(R.id.send_push_button)
    public void sendNewPush() {
        startActivity(SendPushActivity.newIntent(getContext()));
    }
}
