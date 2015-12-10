package com.libertacao.libertacao.view.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.persistence.DatabaseHelper;
import com.libertacao.libertacao.util.Validator;
import com.libertacao.libertacao.util.ViewUtils;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class SendPushActivity extends AppCompatActivity {
    public static final int PLACE_PICKER_REQUEST_CODE = 1;

    @InjectView(R.id.send_push_only_local_checkbox) CheckBox onlyLocalCheckbox;
    @InjectView(R.id.send_push_title_edit_text) EditText titleEditText;
    @InjectView(R.id.send_push_message_edit_text) EditText messageEditText;
    @InjectView(R.id.send_push_uri_edit_text) EditText uriEditText;
    @InjectView(R.id.send_push_event_spinner) Spinner eventSpinner;
    @InjectView(R.id.toWhoThisPushWillBeSent) TextView toWhoThisPushWillBeSent;

    @Nullable private LatLng latLng;

    @Nullable  private List<Event> events;

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, SendPushActivity.class);
    }

    public SendPushActivity(){
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_push);
        ViewUtils.setHomeAsUpEnabled(this);
        ButterKnife.inject(this);
        setupSpinner();
    }

    private void setupSpinner() {
        events = DatabaseHelper.getHelper(this).getEventIntegerRuntimeExceptionDao().queryForEq(Event.ENABLED, true);
        if (events != null) {
            events.add(0, new Event("", getString(R.string.selectAnEventForThisPushOptional), null, Event.INVALID_LOCATION, Event.INVALID_LOCATION, null,
                    null, null, new Date(), null, -1, false, -1, null, null, null));
            ArrayAdapter<Event> adapter =  new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, events);
            eventSpinner.setAdapter(adapter);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                latLng = place.getLatLng();
                if(latLng != null) {
                    toWhoThisPushWillBeSent.setText(String.format(getString(R.string.latitudeLongitudePlaceholder), latLng.latitude, latLng.longitude));
                }
            }
        }
    }

    @OnClick(R.id.pickPushLocationButton)
    public void pickPushLocationButtonClicked() {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(intentBuilder.build(this), PLACE_PICKER_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
        }
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

    @OnClick(R.id.send_push_button)
    public void sendPush() {
        // Valide EditTexts
        if (!validate()) {
            return;
        }

        if(onlyLocalCheckbox.isChecked() || latLng != null) {
            actuallySendPush();
        } else {
            new android.app.AlertDialog.Builder(this)
                    .setMessage(getString(R.string.areYouSureYouWantToSendPush))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            actuallySendPush();
                        }
                    })
                    .setNegativeButton(getString(android.R.string.cancel), null)
                    .show();
        }
    }

    private void actuallySendPush() {
        final ProgressDialog pd = ViewUtils.showProgressDialog(this, getString(R.string.sendingPush), false);

        HashMap<String, Object> params = new HashMap<>();
        String title = titleEditText.getText().toString();
        if(!TextUtils.isEmpty(title)) {
            params.put("title", title);
        }
        params.put("message", messageEditText.getText().toString());

        String uri = uriEditText.getText().toString();
        if(!TextUtils.isEmpty(uri)) {
            params.put("uri", uri);
        }
        params.put("userObjectId", ParseUser.getCurrentUser().getObjectId());

        final boolean onlyToMe = onlyLocalCheckbox.isChecked();
        if(onlyToMe) {
            params.put("recipientId", ParseUser.getCurrentUser().getObjectId());
        }

        int selectedItemPosition = eventSpinner.getSelectedItemPosition();
        if(events != null && selectedItemPosition > 0 && selectedItemPosition <= events.size()){
            String eventObjectId = events.get(selectedItemPosition).getObjectId();
            params.put("eventObjectId", eventObjectId);
        }

        if(latLng != null) {
            params.put("latitude", latLng.latitude);
            params.put("longitude", latLng.longitude);
        }

        ParseCloud.callFunctionInBackground("sendPushToLocation", params, new FunctionCallback<String>() {
            public void done(String success, ParseException e) {
                ViewUtils.hideProgressDialog(pd);
                if (e != null) {
                    Timber.d("Error when sending push: " + e.getLocalizedMessage());
                    String toastText = TextUtils.isEmpty(e.getLocalizedMessage()) ?
                            SendPushActivity.this.getString(R.string.pushSendError) : e.getLocalizedMessage();
                    Toast.makeText(SendPushActivity.this, toastText, Toast.LENGTH_SHORT).show();
                } else {
                    String toastText = TextUtils.isEmpty(success) ? SendPushActivity.this.getString(R.string.pushSendSuccess) : success;
                    Toast.makeText(SendPushActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    if(!onlyToMe) {
                        onlyLocalCheckbox.setChecked(true);
                        titleEditText.setText("");
                        messageEditText.setText("");
                        uriEditText.setText("");
                    }
                }
            }
        });
    }
}
