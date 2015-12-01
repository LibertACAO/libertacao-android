package com.libertacao.libertacao.view.info;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.libertacao.libertacao.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class OtherAppsFragment extends Fragment {

    public static OtherAppsFragment newInstance() {
        return new OtherAppsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_other_apps, container, false);
        ButterKnife.inject(this, layout);
        return layout;
    }

    @OnClick(R.id.beVegButton)
    public void beVeg() {
        final String appPackageName = "br.com.bevegapp";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            } catch(ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), getString(R.string.error_start_activity), Toast.LENGTH_LONG).show();
            }
        }
    }
}
