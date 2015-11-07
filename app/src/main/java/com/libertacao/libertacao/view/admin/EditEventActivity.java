package com.libertacao.libertacao.view.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.databinding.ActivityEditEventBinding;
import com.libertacao.libertacao.manager.LoginManager;
import com.libertacao.libertacao.persistence.DatabaseHelper;
import com.libertacao.libertacao.util.DataUtils;
import com.libertacao.libertacao.util.ImageUtils;
import com.libertacao.libertacao.util.Validator;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.view.customviews.WorkaroundMapFragment;
import com.libertacao.libertacao.view.map.MapFragment;
import com.libertacao.libertacao.viewmodel.EditEventDataModel;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EditEventActivity extends AppCompatActivity implements EditEventDataModel.OnSelectImageClick {
    //public static final int PLACE_PICKER_REQUEST_CODE = 1;
    public static final String EVENT_ID = "EVENT_ID";
    private EditEventDataModel editEventDataModel;
    private MapFragment mapFragment;

    @InjectView(R.id.edit_event_scroll_view) ScrollView scrollView;
    @InjectView(R.id.event_detail_type_radio_group) RadioGroup typeRadioGroup;
    @InjectView(R.id.notificacao_detail_title) EditText titleEditText;
    @InjectView(R.id.notificacao_detail_description) EditText descriptionEditText;
    @InjectView(R.id.notification_location_summary) EditText locationSummaryEditText;
    @InjectView(R.id.notification_location_description) EditText locationDescriptionEditText;

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

        ActivityEditEventBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_event);
        editEventDataModel = new EditEventDataModel(this, this, event);
        binding.setEditEventDataModel(editEventDataModel);
        ViewUtils.setHomeAsUpEnabled(this);
        ButterKnife.inject(this);
        setupTypeRadioGroup();

        if(LoginManager.getInstance().isAdmin()) {
            setTitle(R.string.createEdit);
        } else {
            setTitle(R.string.suggestEvent);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        mapFragment = MapFragment.newInstance(event, true);
        fragmentManager.beginTransaction().replace(R.id.event_map, mapFragment).commit();
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(LoginManager.getInstance().isAdmin()) {
            getMenuInflater().inflate(R.menu.edit_event_activity_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_save_event:
                saveEvent();
                return true;
            /*case R.id.menu_pick_event_place:
                pickPlace();
                return true;*/
            case android.R.id.home:
                // Without this the back button doesn't work
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper methods
     */

    private void setupTypeRadioGroup() {
        CharSequence[] eventTypes = Event.getEventTypes(false);
        int i = 1;
        for(CharSequence charSequence : eventTypes) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(charSequence);
            typeRadioGroup.addView(radioButton);
            if(i == editEventDataModel.getEvent().getType()) {
                typeRadioGroup.check(radioButton.getId());
            }
            i++;
        }
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
        /*if (requestCode == PLACE_PICKER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        } else {*/
            String filePath = ImageUtils.onActivityResult(this, requestCode, resultCode, data);
            if (filePath != null) {
                editEventDataModel.setEventLocalImage(filePath);
                Toast.makeText(this, getString(R.string.photoSelectedSuccessfully), Toast.LENGTH_SHORT).show();
            }
        /*}*/
    }

    /**
     * Validate fields from Form
     * @return if all required fields are present and all fields contain valid values
     */

    private boolean validate() {
        boolean ret;
        ret = Validator.validate(titleEditText, true);
        ret = Validator.validate(descriptionEditText, ret);
        ViewUtils.getIndexOfRadioGroupSelection(typeRadioGroup);
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
        int selectedType = ViewUtils.getIndexOfRadioGroupSelection(typeRadioGroup);
        if(selectedType == 0) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.selectAnEventType))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(getString(android.R.string.ok), null)
                    .setCancelable(false)
                    .show();
            return;
        }

        // Validate initial date
        Calendar initialDateCalendar = editEventDataModel.getInitialDateCalendar();
        if(initialDateCalendar == null) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.selectADate))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(getString(android.R.string.ok), null)
                    .setCancelable(false)
                    .show();
            return;
        }

        // Proceed to event creation
        final ProgressDialog pd;
        if(LoginManager.getInstance().isAdmin()) {
            pd = ViewUtils.showProgressDialog(this, getString(R.string.savingEvent), false);
        } else {
            pd = ViewUtils.showProgressDialog(this, getString(R.string.suggestingEvent), false);
        }

        ParseObject event = new ParseObject(Event.EVENT);
        if(editEventDataModel.getEvent().isSynced()) {
            event.setObjectId(editEventDataModel.getEvent().getObjectId());
        }
        event.put(Event.TITLE, titleEditText.getText().toString());
        event.put(Event.TYPE, selectedType);
        event.put(Event.DESCRIPTION, descriptionEditText.getText().toString());
        event.put(Event.LOCATION_SUMMARY, locationSummaryEditText.getText().toString());
        event.put(Event.LOCATION_DESCRIPTION, locationDescriptionEditText.getText().toString());
        LatLng selectedLatLng = mapFragment.getSelectedLatLng();
        if(selectedLatLng != null) {
            ParseGeoPoint location = new ParseGeoPoint(selectedLatLng.latitude, selectedLatLng.longitude);
            event.put(Event.LOCATION, location);
        }
        event.put(Event.INITIAL_DATE, initialDateCalendar.getTime());
        Calendar endDateCalendar = editEventDataModel.getEndDateCalendar();
        if(endDateCalendar != null) {
            event.put(Event.END_DATE, endDateCalendar.getTime());
        }
        event.put(Event.ENABLED, false);

        if(editEventDataModel.getEventLocalImage() != null) {
            try {
                byte[] image = DataUtils.readInFile(editEventDataModel.getEventLocalImage());
                ParseFile file = new ParseFile("picture.jpg", image);
                file.saveInBackground();
                event.put(Event.IMAGE, file);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        }

        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ViewUtils.hideProgressDialog(pd);
                if(LoginManager.getInstance().isAdmin()) {
                    Toast.makeText(EditEventActivity.this, EditEventActivity.this.getString(R.string.eventSavedSuccessfully), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditEventActivity.this, EditEventActivity.this.getString(R.string.eventSuggestedSuccessfully), Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    /**
     * Pick place
     */
    /**
     * https://medium.com/@hitherejoe/exploring-play-services-place-picker-autocomplete-150809f739fe
    private void pickPlace() {
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
            Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
        }
    }
     **/
}
