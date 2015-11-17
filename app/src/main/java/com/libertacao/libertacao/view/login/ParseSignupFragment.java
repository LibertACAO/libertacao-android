package com.libertacao.libertacao.view.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.libertacao.libertacao.MyApp;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.util.Validator;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
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
        ret = Validator.validate(confirmPasswordField, ret);
        ret = Validator.validate(emailField, ret);
        return ret;
    }

    @Override
    public void onClick(View v) {
        if(!validate()){
            return;
        }

        // Check if password match
        final String password = passwordField.getText().toString();
        if(!password.equals(confirmPasswordField.getText().toString())){
            confirmPasswordField.setError(MyApp.getAppContext().getString(R.string.passwordNotMatch));
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return;
        }

        String username = usernameField.getText().toString();
        String email = emailField.getText().toString();
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

                    switch (e.getCode()) {
                        case ParseException.INVALID_EMAIL_ADDRESS:
                            showToast(R.string.invalidEmail);
                            break;
                        case ParseException.USERNAME_TAKEN:
                            showToast(R.string.usernameTaken);
                            break;
                        case ParseException.EMAIL_TAKEN:
                            showToast(R.string.emailTaken);
                            break;
                        default:
                            showToast(R.string.unknownSignupError);
                    }
                }
            }
        });
    }

    private void signupSuccess() {
        onLoginSuccessListener.onLoginSuccess();
    }
}
