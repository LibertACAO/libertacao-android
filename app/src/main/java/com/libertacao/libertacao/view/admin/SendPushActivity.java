package com.libertacao.libertacao.view.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.libertacao.libertacao.R;
import com.libertacao.libertacao.util.Validator;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.view.customviews.WorkaroundMapFragment;
import com.libertacao.libertacao.view.map.MapFragment;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class SendPushActivity extends AppCompatActivity {

    @InjectView(R.id.send_push_scroll_view) ScrollView scrollView;
    @InjectView(R.id.send_push_edit_text) EditText alertText;

    private MapFragment mapFragment;

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
        setupMap();
    }

    private void setupMap() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mapFragment = MapFragment.newInstance(true);
        fragmentManager.beginTransaction().replace(R.id.event_map, mapFragment).commit();
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
    }

    /**
     * Validate fields from Form
     * @return if all required fields are present and all fields contain valid values
     */

    private boolean validate() {
        boolean ret;
        ret = Validator.validate(alertText, true);
        return ret;
    }

    @OnClick(R.id.send_push_button)
    public void sendPush() {
        // Valide EditTexts
        if(!validate()) {
            return;
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("message", alertText.getText().toString());

        LatLng selectedLatLng = mapFragment.getSelectedLatLng();
        if(selectedLatLng != null) {
            params.put("latitude", selectedLatLng.latitude);
            params.put("longitude", selectedLatLng.longitude);
        }

        ParseCloud.callFunctionInBackground("sendPushToLocation", params, new FunctionCallback<String>() {
            public void done(String success, ParseException e) {
                if (e != null) {
                    Timber.d("Error when sending push: " + e.getLocalizedMessage());
                    Toast.makeText(SendPushActivity.this, SendPushActivity.this.getString(R.string.pushSendError), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SendPushActivity.this, SendPushActivity.this.getString(R.string.pushSendSuccess), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
