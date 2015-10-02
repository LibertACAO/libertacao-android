package com.libertacao.libertacao.view.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.util.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.ui.ParseLoginFragmentBase;
import com.parse.ui.ParseOnLoadingListener;
import com.parse.ui.ParseOnLoginSuccessListener;

/**
 * Fragment for the user signup screen.
 */
public class ParseSignupFragment extends ParseLoginFragmentBase implements OnClickListener {
    public static final String USERNAME = "com.parse.ui.ParseSignupFragment.USERNAME";
    public static final String PASSWORD = "com.parse.ui.ParseSignupFragment.PASSWORD";

    private EditText usernameField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private EditText emailField;
    private ParseOnLoginSuccessListener onLoginSuccessListener;

    private static final String LOG_TAG = "ParseSignupFragment";
    private static final int DEFAULT_MIN_PASSWORD_LENGTH = 6;

    public static ParseSignupFragment newInstance(Bundle configOptions, String username, String password) {
        ParseSignupFragment signupFragment = new ParseSignupFragment();
        Bundle args = new Bundle(configOptions);
        args.putString(ParseSignupFragment.USERNAME, username);
        args.putString(ParseSignupFragment.PASSWORD, password);
        signupFragment.setArguments(args);
        return signupFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Bundle args = getArguments();

        String username = args.getString(USERNAME);
        String password = args.getString(PASSWORD);

        View v = inflater.inflate(R.layout.com_parse_ui_parse_signup_fragment, parent, false);
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.background, (ImageView) v.findViewById(R.id.background_image_view),
                ViewUtils.getFadeInDisplayImageOptions());
        usernameField = (EditText) v.findViewById(R.id.signup_username_input);
        passwordField = (EditText) v.findViewById(R.id.signup_password_input);
        confirmPasswordField = (EditText) v.findViewById(R.id.signup_confirm_password_input);
        emailField = (EditText) v.findViewById(R.id.signup_email_input);
        Button createAccountButton = (Button) v.findViewById(R.id.create_account);

        usernameField.setText(username);
        passwordField.setText(password);

        createAccountButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ParseOnLoginSuccessListener) {
            onLoginSuccessListener = (ParseOnLoginSuccessListener) activity;
        } else {
            throw new IllegalArgumentException(
                    "Activity must implemement ParseOnLoginSuccessListener");
        }

        if (activity instanceof ParseOnLoadingListener) {
            onLoadingListener = (ParseOnLoadingListener) activity;
        } else {
            throw new IllegalArgumentException(
                    "Activity must implemement ParseOnLoadingListener");
        }
    }

    @Override
    public void onClick(View v) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String passwordAgain = confirmPasswordField.getText().toString();

        String email = emailField.getText().toString();

        if (username.length() == 0) {
            showToast(R.string.com_parse_ui_no_username_toast);
        } else if (password.length() == 0) {
            showToast(R.string.com_parse_ui_no_password_toast);
        } else if (password.length() < DEFAULT_MIN_PASSWORD_LENGTH) {
            showToast(getResources().getQuantityString(
                    R.plurals.com_parse_ui_password_too_short_toast,
                    DEFAULT_MIN_PASSWORD_LENGTH, DEFAULT_MIN_PASSWORD_LENGTH));
        } else if (passwordAgain.length() == 0) {
            showToast(R.string.com_parse_ui_reenter_password_toast);
        } else if (!password.equals(passwordAgain)) {
            showToast(R.string.com_parse_ui_mismatch_confirm_password_toast);
            confirmPasswordField.selectAll();
            confirmPasswordField.requestFocus();
        } else if (email.length() == 0) {
            showToast(R.string.com_parse_ui_no_email_toast);
        } else {
            ParseUser user = new ParseUser();

            // Set standard fields
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

            loadingStart();
            user.signUpInBackground(new SignUpCallback() {

                @Override
                public void done(ParseException e) {
                    if (isActivityDestroyed()) {
                        return;
                    }

                    if (e == null) {
                        loadingFinish();
                        signupSuccess();
                    } else {
                        loadingFinish();
                        debugLog(getString(R.string.com_parse_ui_login_warning_parse_signup_failed) +
                                e.toString());
                        switch (e.getCode()) {
                            case ParseException.INVALID_EMAIL_ADDRESS:
                                showToast(R.string.com_parse_ui_invalid_email_toast);
                                break;
                            case ParseException.USERNAME_TAKEN:
                                showToast(R.string.com_parse_ui_username_taken_toast);
                                break;
                            case ParseException.EMAIL_TAKEN:
                                showToast(R.string.com_parse_ui_email_taken_toast);
                                break;
                            default:
                                showToast(R.string.com_parse_ui_signup_failed_unknown_toast);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    private void signupSuccess() {
        onLoginSuccessListener.onLoginSuccess();
    }
}
