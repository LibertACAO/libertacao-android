package com.libertacao.libertacao.view.contact;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.util.Validator;
import com.libertacao.libertacao.util.ViewUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ContactFragment extends Fragment {
    @InjectView(R.id.contact_email_edit_text) EditText emailEditText;
    @InjectView(R.id.contact_message_edit_text) EditText messageEditText;

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_contact, container, false);
        ButterKnife.inject(this, layout);
        return layout;
    }

    /**
     * Validate fields from Form
     * @return if all required fields are present and all fields contain valid values
     */

    private boolean validate() {
        boolean ret;
        ret = Validator.validate(messageEditText, true);
        return ret;
    }

    @OnClick(R.id.send_message_button)
    public void sendMessage() {
        if(!validate()) {
            return;
        }
        final ProgressDialog pd = ViewUtils.showProgressDialog(getContext(), getString(R.string.savingMessage), false);
        ParseObject contact = new ParseObject("Contact");
        if(!TextUtils.isEmpty(emailEditText.getText().toString())) {
            contact.put("email", emailEditText.getText().toString());
        }
        contact.put("message", messageEditText.getText().toString());

        contact.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ViewUtils.hideProgressDialog(pd);
                Context context = ContactFragment.this.getContext();
                if (e != null) {
                    Toast.makeText(context, context.getString(R.string.messageSavedError), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.messageSavedSuccessfully), Toast.LENGTH_LONG).show();
                    emailEditText.setText("");
                    messageEditText.setText("");
                }
            }
        });
    }
}
