package com.libertacao.libertacao.view.perfil;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.libertacao.libertacao.R;
import com.parse.ParseUser;

import bolts.Task;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PerfilFragment extends Fragment {
    /**
     * Interface elements
     */
    @InjectView(R.id.profile_username) TextView mProfileUsernameTextView;

    public static PerfilFragment newInstance() {
        return new PerfilFragment();
    }

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_perfil, container, false);
        ButterKnife.inject(this, layout);
        ParseUser currentUser = ParseUser.getCurrentUser();
        mProfileUsernameTextView.setText(currentUser.getUsername());
        return layout;
    }


    @OnClick(R.id.btn_logout)
    public void logout(){
        Task<Void> logoutTask = ParseUser.logOutInBackground();
        // TODO: add onSucess
    }
}
