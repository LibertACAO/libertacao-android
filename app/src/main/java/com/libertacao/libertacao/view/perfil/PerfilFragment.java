package com.libertacao.libertacao.view.perfil;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.manager.LoginManager;
import com.libertacao.libertacao.persistence.UserPreferences;
import com.libertacao.libertacao.view.main.MainActivity;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class PerfilFragment extends Fragment {
    /**
     * Interface elements
     */
    @InjectView(R.id.profile_username) TextView mProfileUsernameTextView;
    @InjectView(R.id.enable_notification_toogle_button) ToggleButton enable_notification_toogle_button;

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
        mProfileUsernameTextView.setText(LoginManager.getInstance().getUsername());
        enable_notification_toogle_button.setChecked(UserPreferences.isNotificationEnabled());
        return layout;
    }


    @OnClick(R.id.btn_logout)
    public void logout() {
        new android.app.AlertDialog.Builder(getContext())
                .setMessage(getString(R.string.areYouSureYouWantToLogout))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actuallyLogout();
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), null)
                .show();
    }

    private void actuallyLogout() {
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
                    LoginManager.getInstance().logout();
                    ((MainActivity) getActivity()).updateDrawer();
                    ((MainActivity)getActivity()).openDrawer();
                    ((MainActivity)getActivity()).setupEventFragment();
                }
            }
        });
    }

    @OnCheckedChanged(R.id.enable_notification_toogle_button)
    public void changeEnableNotificationToggleButton() {
        UserPreferences.setIsNotificationEnabled(enable_notification_toogle_button.isChecked());
    }
}
