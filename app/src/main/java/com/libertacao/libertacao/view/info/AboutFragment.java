package com.libertacao.libertacao.view.info;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
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

public class AboutFragment extends Fragment {

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.inject(this, layout);
        return layout;
    }

    @OnClick(R.id.like_our_page_in_facebook)
    public void likeOurPage() {
        Intent intent = newFacebookIntent(getActivity().getPackageManager(), "https://www.facebook.com/libertacaoapp");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), getString(R.string.error_start_activity), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Intent to open the official Facebook app. If the Facebook app is not installed then the
     * default web browser will be used.
     * <p/>
     * </br></br>Example usage:</br>
     * <code>newFacebookIntent(context.getPackageManager(), "https://www.facebook.com/JRummyApps");</code>
     *
     * @param pm  Instance of the {@link PackageManager}.
     * @param url The full URL to the Facebook page or profile.
     * @return An intent that will open the Facebook page/profile.
     */
    private static Intent newFacebookIntent(PackageManager pm, @SuppressWarnings("SameParameterValue") String url) {
        Uri uri;
        try {
            pm.getPackageInfo("com.facebook.katana", 0);
            // http://stackoverflow.com/a/24547437/1048340
            uri = Uri.parse("fb://facewebmodal/f?href=" + url);
        } catch (PackageManager.NameNotFoundException e) {
            uri = Uri.parse(url);
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }
}
