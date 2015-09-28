package com.libertacao.libertacao.view.perfil;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.view.main.MainActivity;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

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
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getContext().getString(R.string.performingLogout));
        pd.setCancelable(false);
        pd.show();

        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                pd.dismiss();
                pd.cancel();
                if(e != null) {
                    Toast.makeText(getContext(), getContext().getString(R.string.performedLogoutError), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.performedLogoutSuccess), Toast.LENGTH_SHORT).show();
                    ((MainActivity)getActivity()).updateDrawer();
                    ((MainActivity)getActivity()).openDrawer();
                }
            }
        });
    }
}
