package com.libertacao.libertacao.view.login;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ImageView;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.util.MyImageLoader;
import com.libertacao.libertacao.util.ViewUtils;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ui.ParseOnLoadingListener;
import com.parse.ui.ParseOnLoginSuccessListener;

// FUTURE: add scrolling animated background (second release)
public class ParseLoginActivity extends FragmentActivity implements
        ParseLoginFragment.ParseLoginFragmentListener,
        ParseLoginHelpFragment.ParseOnLoginHelpSuccessListener,
        ParseOnLoginSuccessListener, ParseOnLoadingListener {

    private ProgressDialog progressDialog;
    private Bundle configOptions;

    // Although Activity.isDestroyed() is in API 17, we implement it anyways for older versions.
    private boolean destroyed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Combine options from incoming intent and the activity metadata
        configOptions = getMergedOptions();

        // Show the login form
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, ParseLoginFragment.newInstance(configOptions)).commit();
        }

        MyImageLoader.getInstance().displayImage("drawable://" + R.drawable.background, (ImageView) findViewById(R.id.background_image_view),
                ViewUtils.getFadeInDisplayImageOptions());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDark));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        destroyed = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Required for making Facebook login work
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Called when the user clicked the sign up button on the login form.
     */
    @Override
    public void onSignUpClicked(String username, String password) {
        // Show the signup form, but keep the transaction on the back stack
        // so that if the user clicks the back button, they are brought back
        // to the login form.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, ParseSignupFragment.newInstance(configOptions, username, password));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Called when the user clicked the log in button on the login form.
     */
    @Override
    public void onLoginHelpClicked() {
        // Show the login help form for resetting the user's password.
        // Keep the transaction on the back stack so that if the user clicks
        // the back button, they are brought back to the login form.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, ParseLoginHelpFragment.newInstance(configOptions));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Called when the user successfully completes the login help flow.
     */
    @Override
    public void onLoginHelpSuccess() {
        // Display the login form, which is the previous item onto the stack
        getSupportFragmentManager().popBackStackImmediate();
    }

    /**
     * Called when the user successfully logs in or signs up.
     */
    @Override
    public void onLoginSuccess() {
        // This default implementation returns to the parent activity with
        // RESULT_OK.
        // You can change this implementation if you want a different behavior.
        setResult(RESULT_OK);
        finish();
    }

    /**
     * Called when we are in progress retrieving some data.
     *
     * @param showSpinner Whether to show the loading dialog.
     */
    @Override
    public void onLoadingStart(boolean showSpinner) {
        if (showSpinner) {
            progressDialog = ProgressDialog.show(this, null,
                    getString(com.parse.ui.R.string.com_parse_ui_progress_dialog_text), true, false);
        }
    }

    /**
     * Called when we are finished retrieving some data.
     */
    @Override
    public void onLoadingFinish() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * @see android.app.Activity#isDestroyed()
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean isDestroyed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return super.isDestroyed();
        }
        return destroyed;
    }

    private Bundle getMergedOptions() {
        // Read activity metadata from AndroidManifest.xml
        ActivityInfo activityInfo = null;
        try {
            activityInfo = getPackageManager().getActivityInfo(
                    this.getComponentName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            if (Parse.getLogLevel() <= Parse.LOG_LEVEL_ERROR &&
                    Log.isLoggable("ParseLoginActivity", Log.WARN)) {
                Log.w("ParseLoginActivity", e.getMessage());
            }
        }

        // The options specified in the Intent (from ParseLoginBuilder) will
        // override any duplicate options specified in the activity metadata
        Bundle mergedOptions = new Bundle();
        if (activityInfo != null && activityInfo.metaData != null) {
            mergedOptions.putAll(activityInfo.metaData);
        }
        if (getIntent().getExtras() != null) {
            mergedOptions.putAll(getIntent().getExtras());
        }

        return mergedOptions;
    }
}
