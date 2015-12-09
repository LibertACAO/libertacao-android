package com.libertacao.libertacao.view.event;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.databinding.ActivityEditEventBinding;
import com.libertacao.libertacao.manager.LoginManager;
import com.libertacao.libertacao.manager.ParserDtoManager;
import com.libertacao.libertacao.persistence.DatabaseHelper;
import com.libertacao.libertacao.util.DataUtils;
import com.libertacao.libertacao.util.ImageUtils;
import com.libertacao.libertacao.util.Validator;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.viewmodel.EditEventDataModel;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class EditEventActivity extends AppCompatActivity implements EditEventDataModel.OnSelectImageClick, EditEventDataModel.OnPickEventPlaceClick {
    public static final int PLACE_PICKER_REQUEST_CODE = 1;
    public static final String EVENT_ID = "EVENT_ID";
    private EditEventDataModel editEventDataModel;

    @InjectView(R.id.appbar) AppBarLayout appbarLayout;
    @InjectView(R.id.event_detail_type_spinner) Spinner typeSpinner;
    @InjectView(R.id.notificacao_detail_title) EditText titleEditText;
    @InjectView(R.id.notificacao_detail_description) EditText descriptionEditText;
    @InjectView(R.id.edit_event_checkbox_specific_location) CheckBox specificLocationCheckbox;
    @InjectView(R.id.notification_location_summary) EditText locationSummaryEditText;
    @InjectView(R.id.notification_location_description) EditText locationDescriptionEditText;
    @InjectView(R.id.event_link_url) EditText linkUrlEditText;
    @InjectView(R.id.event_link_text) EditText linkTextEditText;

    public static Intent newIntent(@NonNull Context context) {
        return newIntent(context, null);
    }

    public static Intent newIntent(@NonNull Context context, @Nullable Event event) {
        Intent intent = new Intent(context, EditEventActivity.class);
        if(event != null) {
            intent.putExtra(EVENT_ID, event.getId());
        }
        return intent;
    }

    public EditEventActivity(){
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int eventId = getIntent().getIntExtra(EVENT_ID, -1);
        Event event = DatabaseHelper.getHelper(this).getEventIntegerRuntimeExceptionDao().queryForId(eventId);
        if(event == null) {
            event = new Event();
        }

        // TODO: Titulo e descricao sao apagados depois de escolher o local do evento!
        ActivityEditEventBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_event);
        editEventDataModel = new EditEventDataModel(this, this, this, event);
        binding.setEditEventDataModel(editEventDataModel);
        ViewUtils.setHomeAsUpEnabled(this);
        ButterKnife.inject(this);
        setupTypesSpinner();
        appbarLayout.setExpanded(false);

        if(LoginManager.getInstance().isAdmin()) {
            setTitle(R.string.createEdit);
        } else {
            setTitle(R.string.suggestEvent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_event_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_save_event:
                saveEvent();
                return true;
            case android.R.id.home:
                // Without this the back button doesn't work
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.save_event_button)
    public void saveEventButtonClicked() {
        saveEvent();
    }

    /**
     * Helper methods
     */

    private void setupTypesSpinner() {
        CharSequence[] eventTypes = Event.getEventTypes(false);
        List<String> eventTypesString = new ArrayList<>(eventTypes.length + 1);
        eventTypesString.add(getString(R.string.selectEventType));
        for (CharSequence eventType : eventTypes) {
            eventTypesString.add(eventType.toString());
        }
        ArrayAdapter<String> types = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypesString);
        types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(types);
        typeSpinner.setSelection(editEventDataModel.getEvent().getType());
    }

    /**
     * Select image
     */

    @Override
    public void onSelectImageClick() {
        ImageUtils.selectImage(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                LatLng latLng = place.getLatLng();
                if(latLng != null) {
                    editEventDataModel.getEvent().setLatitude(latLng.latitude);
                    editEventDataModel.getEvent().setLongitude(latLng.longitude);
                }
                if(place.getName() != null) {
                    editEventDataModel.getEvent().setLocationSummary(place.getName().toString());
                }
                if(place.getAddress() != null) {
                    editEventDataModel.getEvent().setLocationDescription(place.getAddress().toString());
                }
                if(place.getWebsiteUri() != null) {
                    editEventDataModel.getEvent().setLinkUrl(place.getWebsiteUri().toString());
                    editEventDataModel.getEvent().setLinkText(getString(R.string.linkTextDefaultPlaceURL));
                }
                editEventDataModel.notifyChange();
            }
        } else {
            String filePath = ImageUtils.onActivityResult(this, requestCode, resultCode, data);
            if (filePath != null) {
                editEventDataModel.setEventLocalImage(filePath);
                Toast.makeText(this, getString(R.string.photoSelectedSuccessfully), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Validate fields from Form
     * @return if all required fields are present and all fields contain valid values
     */

    private boolean validate() {
        boolean ret;
        ret = Validator.validate(titleEditText, true);
        ret = Validator.validate(descriptionEditText, ret);
        return ret;
    }

    /**
     * Save Event
     */
    private void saveEvent() {
        // Valide EditTexts
        if(!validate()) {
            return;
        }

        // Validate event type
        final int selectedType = typeSpinner.getSelectedItemPosition();
        if(selectedType == 0) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.selectAnEventType))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(getString(android.R.string.ok), null)
                    .show();
            return;
        }

        // Validate initial date
        final Calendar initialDateCalendar = editEventDataModel.getInitialDateCalendar();
        if(initialDateCalendar == null) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.selectADate))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(getString(android.R.string.ok), null)
                    .show();
            return;
        }

        // Validate links
        if(TextUtils.isEmpty(linkUrlEditText.getText().toString()) && !TextUtils.isEmpty(linkTextEditText.getText().toString())) {
            linkUrlEditText.setError(getString(R.string.typeALink));
            linkUrlEditText.requestFocus();
            return;
        }

        // Proceed to event creation
        final ProgressDialog pd;
        if(LoginManager.getInstance().isAdmin()) {
            pd = ViewUtils.showProgressDialog(this, getString(R.string.savingEvent), false);
        } else {
            pd = ViewUtils.showProgressDialog(this, getString(R.string.suggestingEvent), false);
        }

        ParseObject parseObject = ParserDtoManager.getParseObjectFrom(editEventDataModel.getEvent());
        parseObject.put(Event.TYPE, selectedType);

        if(specificLocationCheckbox.isChecked()) {
            if(editEventDataModel.getEvent().getLatitude() != Event.INVALID_LOCATION && editEventDataModel.getEvent().getLongitude() != Event.INVALID_LOCATION) {
                ParseGeoPoint location = new ParseGeoPoint(editEventDataModel.getEvent().getLatitude(), editEventDataModel.getEvent().getLongitude());
                parseObject.put(Event.LOCATION, location);
            }
        }
        parseObject.put(Event.INITIAL_DATE, initialDateCalendar.getTime());
        Calendar endDateCalendar = editEventDataModel.getEndDateCalendar();
        if(endDateCalendar != null) {
            parseObject.put(Event.END_DATE, endDateCalendar.getTime());
        }
        parseObject.put(Event.LINK_URL, linkUrlEditText.getText().toString());
        parseObject.put(Event.LINK_TEXT, linkTextEditText.getText().toString());

        if(editEventDataModel.getEventLocalImage() != null) {
            try {
                byte[] image = DataUtils.getSmallFileData(editEventDataModel.getEventLocalImage());
                ParseFile file = new ParseFile("picture.jpg", image);
                file.saveInBackground();
                parseObject.put(Event.IMAGE, file);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
            }
        }

        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ViewUtils.hideProgressDialog(pd);
                if(e != null) {
                    Timber.d("Error when saving event: " + e.getLocalizedMessage());
                    if (LoginManager.getInstance().isAdmin()) {
                        Toast.makeText(EditEventActivity.this, EditEventActivity.this.getString(R.string.eventSavedError), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(EditEventActivity.this, EditEventActivity.this.getString(R.string.eventSuggestedError), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (LoginManager.getInstance().isAdmin()) {
                        Toast.makeText(EditEventActivity.this, EditEventActivity.this.getString(R.string.eventSavedSuccessfully), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(EditEventActivity.this, EditEventActivity.this.getString(R.string.eventSuggestedSuccessfully), Toast.LENGTH_LONG).show();
                    }
                    finish();
                }
            }
        });
    }


    /**
     * Pick place
     * https://medium.com/@hitherejoe/exploring-play-services-place-picker-autocomplete-150809f739fe
     */
    @Override
    public void onPickEventPlaceClick() {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        if(editEventDataModel.getEvent().hasLocation()) {
            double longitude = editEventDataModel.getEvent().getLongitude();
            double latitude = editEventDataModel.getEvent().getLatitude();
            double offset = 0.01;
            LatLng southwest = new LatLng(latitude - offset, longitude - offset);
            LatLng northeast = new LatLng(latitude + offset, longitude + offset);
            intentBuilder.setLatLngBounds(new LatLngBounds(southwest, northeast));
        }
        try {
            startActivityForResult(intentBuilder.build(this), PLACE_PICKER_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
        }
    }
}
