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
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.util.Validator;
import com.libertacao.libertacao.util.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ui.ParseLoginConfig;
import com.parse.ui.ParseLoginFragmentBase;
import com.parse.ui.ParseOnLoadingListener;
import com.parse.ui.ParseOnLoginSuccessListener;

import org.json.JSONObject;

/**
 * Fragment for the user login screen.
 */
public class ParseLoginFragment extends ParseLoginFragmentBase {
    public interface ParseLoginFragmentListener {
        void onSignUpClicked(String username, String password);
        void onLoginHelpClicked();
    }

    private static final String USER_OBJECT_NAME_FIELD = "name";

    private EditText usernameField;
    private EditText passwordField;
    private TextView parseLoginHelpButton;
    private Button parseLoginButton;
    private Button parseSignupButton;
    private Button facebookLoginButton;
    private ParseLoginFragmentListener loginFragmentListener;
    private ParseOnLoginSuccessListener onLoginSuccessListener;

    public static ParseLoginFragment newInstance(Bundle configOptions) {
        ParseLoginFragment loginFragment = new ParseLoginFragment();
        loginFragment.setArguments(configOptions);
        return loginFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(com.parse.ui.R.layout.com_parse_ui_parse_login_fragment, parent, false);
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.background, (ImageView) v.findViewById(R.id.background_image_view),
                ViewUtils.getFadeInDisplayImageOptions());
        usernameField = (EditText) v.findViewById(com.parse.ui.R.id.login_username_input);
        passwordField = (EditText) v.findViewById(com.parse.ui.R.id.login_password_input);
        parseLoginHelpButton = (Button) v.findViewById(com.parse.ui.R.id.parse_login_help);
        parseLoginButton = (Button) v.findViewById(com.parse.ui.R.id.parse_login_button);
        parseSignupButton = (Button) v.findViewById(com.parse.ui.R.id.parse_signup_button);
        facebookLoginButton = (Button) v.findViewById(com.parse.ui.R.id.facebook_login);

        setUpParseLoginAndSignup();
        setUpFacebookLogin();
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof ParseLoginFragmentListener) {
            loginFragmentListener = (ParseLoginFragmentListener) activity;
        } else {
            throw new IllegalArgumentException("Activity must implemement ParseLoginFragmentListener");
        }

        if (activity instanceof ParseOnLoginSuccessListener) {
            onLoginSuccessListener = (ParseOnLoginSuccessListener) activity;
        } else {
            throw new IllegalArgumentException("Activity must implemement ParseOnLoginSuccessListener");
        }

        if (activity instanceof ParseOnLoadingListener) {
            onLoadingListener = (ParseOnLoadingListener) activity;
        } else {
            throw new IllegalArgumentException("Activity must implemement ParseOnLoadingListener");
        }
    }

    private boolean validate() {
        boolean ret;
        ret = Validator.validate(usernameField, true);
        ret = Validator.validate(passwordField, ret);
        return ret;
    }

    private void setUpParseLoginAndSignup() {
        parseLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validate()){
                    return;
                }

                loadingStart(true);
                ParseUser.logInInBackground(usernameField.getText().toString(),
                        passwordField.getText().toString(),
                        new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (isActivityDestroyed()) {
                            return;
                        }

                        if (user != null) {
                            loadingFinish();
                            loginSuccess();
                        } else {
                            loadingFinish();
                            if (e != null) {
                                if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                                    showToast(R.string.invalidCredentials);
                                    passwordField.setText("");
                                    passwordField.requestFocus();
                                } else {
                                    showToast(R.string.unknownLoginError);
                                }
                            }
                        }
                    }
                });
            }
        });

        parseSignupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();

                loginFragmentListener.onSignUpClicked(username, password);
            }
        });

        parseLoginHelpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFragmentListener.onLoginHelpClicked();
            }
        });
    }


    private LogInCallback facebookLoginCallbackV4 = new LogInCallback() {
        @Override
        public void done(ParseUser user, ParseException e) {
            if (isActivityDestroyed()) {
                return;
            }

            if (user == null) {
                loadingFinish();
                if (e != null) {
                    showToast(R.string.facebookLoginError);
                }
            } else if (user.isNew()) {
                GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject fbUser, GraphResponse response) {
                                ParseUser parseUser = ParseUser.getCurrentUser();
                                if (fbUser != null && parseUser != null && fbUser.optString("name").length() > 0) {
                                    parseUser.put(USER_OBJECT_NAME_FIELD, fbUser.optString("name"));
                                    parseUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                debugLog(getString(
                                                        com.parse.ui.R.string.com_parse_ui_login_warning_facebook_login_user_update_failed) +
                                                        e.toString());
                                            }
                                            loginSuccess();
                                        }
                                    });
                                }
                                loginSuccess();
                            }
                        }
                ).executeAsync();
            } else {
                loginSuccess();
            }
        }
    };

    private void setUpFacebookLogin() {
        facebookLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingStart(false); // Facebook login pop-up already has a spinner
                ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(),
                        ParseLoginConfig.fromBundle(getArguments(), getActivity()).getFacebookLoginPermissions(),
                        facebookLoginCallbackV4);
            }
        });
    }

    private void loginSuccess() {
        onLoginSuccessListener.onLoginSuccess();
    }

}
