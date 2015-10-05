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

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.util.Validator;
import com.libertacao.libertacao.util.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.ui.ParseLoginFragmentBase;
import com.parse.ui.ParseOnLoadingListener;

/**
 * Fragment for the login help screen for resetting the user's password.
 */
public class ParseLoginHelpFragment extends ParseLoginFragmentBase implements OnClickListener {

    public interface ParseOnLoginHelpSuccessListener {
        void onLoginHelpSuccess();
    }

    private TextView instructionsTextView;
    private EditText emailField;
    private Button submitButton;
    private boolean emailSent = false;
    private ParseOnLoginHelpSuccessListener onLoginHelpSuccessListener;

    public static ParseLoginHelpFragment newInstance(Bundle configOptions) {
        ParseLoginHelpFragment loginHelpFragment = new ParseLoginHelpFragment();
        loginHelpFragment.setArguments(configOptions);
        return loginHelpFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.com_parse_ui_parse_login_help_fragment, parent, false);
        ImageLoader.getInstance().displayImage("drawable://" + com.libertacao.libertacao.R.drawable.background, (ImageView) v.findViewById(com.libertacao.libertacao.R.id.background_image_view),
                ViewUtils.getFadeInDisplayImageOptions());
        instructionsTextView = (TextView) v.findViewById(R.id.login_help_instructions);
        emailField = (EditText) v.findViewById(R.id.login_help_email_input);
        submitButton = (Button) v.findViewById(R.id.login_help_submit);

        submitButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof ParseOnLoadingListener) {
            onLoadingListener = (ParseOnLoadingListener) activity;
        } else {
            throw new IllegalArgumentException("Activity must implemement ParseOnLoadingListener");
        }

        if (activity instanceof ParseOnLoginHelpSuccessListener) {
            onLoginHelpSuccessListener = (ParseOnLoginHelpSuccessListener) activity;
        } else {
            throw new IllegalArgumentException("Activity must implemement ParseOnLoginHelpSuccessListener");
        }
    }

    private boolean validate() {
        return Validator.validate(emailField, true);
    }

    @Override
    public void onClick(View v) {
        if(!validate()) {
            return;
        }

        if (!emailSent) {
            loadingStart();
            ParseUser.requestPasswordResetInBackground(emailField.getText().toString(),
                    new RequestPasswordResetCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (isActivityDestroyed()) {
                                return;
                            }

                            loadingFinish();
                            if (e == null) {
                                instructionsTextView.setText(R.string.helpSuccess);
                                emailField.setVisibility(View.INVISIBLE);
                                submitButton.setText(R.string.helpBackToLogin);
                                emailSent = true;
                            } else {
                                if (e.getCode() == ParseException.INVALID_EMAIL_ADDRESS ||
                                        e.getCode() == ParseException.EMAIL_NOT_FOUND) {
                                    showToast(R.string.invalidEmail);
                                } else {
                                    showToast(R.string.unknownHelpError);
                                }
                            }
                        }
                    });
        } else {
            onLoginHelpSuccessListener.onLoginHelpSuccess();
        }
    }
}
