package com.libertacao.libertacao.view.event;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.Toast;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.databinding.ActivityEventDetailBinding;
import com.libertacao.libertacao.manager.LoginManager;
import com.libertacao.libertacao.persistence.DatabaseHelper;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.view.admin.EditEventActivity;
import com.libertacao.libertacao.view.customviews.WorkaroundMapFragment;
import com.libertacao.libertacao.view.map.MapFragment;
import com.libertacao.libertacao.viewmodel.EventDataModel;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EventDetailActivity extends AppCompatActivity {
    private static final String EVENT_ID = "EVENT_ID";
    private Event event;

    @InjectView(R.id.event_detail_scroll_view) ScrollView scrollView;

    public static Intent newIntent(Context context, Event event){
        Intent intent = new Intent(context, EventDetailActivity.class);
        intent.putExtra(EVENT_ID, event.getId());
        return intent;
    }

    public EventDetailActivity(){
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int eventId = getIntent().getIntExtra(EVENT_ID, -1);
        if (eventId == -1){
            notFoundEvent();
        } else {
            event = DatabaseHelper.getHelper(this).getEventIntegerRuntimeExceptionDao().queryForId(eventId);
            if(event != null){
                ActivityEventDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail);
                binding.setEventDataModel(new EventDataModel(this, event));
                ViewUtils.setHomeAsUpEnabled(this);
                ButterKnife.inject(this);

                FragmentManager fragmentManager = getSupportFragmentManager();
                MapFragment mapFragment = MapFragment.newInstance(event);
                fragmentManager.beginTransaction().replace(R.id.event_map, mapFragment).commit();
                mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        scrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });
            } else {
                notFoundEvent();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(LoginManager.getInstance().isAdmin()) {
            getMenuInflater().inflate(R.menu.admin_event_detail_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_edit_event:
                startActivity(EditEventActivity.newIntent(this, event));
                return true;
            case R.id.menu_delete_event:
                deleteEvent();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper methods to setup UI
     */

    private void notFoundEvent() {
        Toast.makeText(getBaseContext(), getString(R.string.eventNotFound), Toast.LENGTH_LONG).show();
        finish();
    }

    private void deleteEvent() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.deleteConfirm))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog pd = ViewUtils.showProgressDialog(EventDetailActivity.this, getString(R.string.deletingEvent), false);
                        ParseObject eventParseObject = new ParseObject(Event.EVENT);
                        eventParseObject.setObjectId(event.getObjectId());
                        eventParseObject.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                ViewUtils.hideProgressDialog(pd);
                                Toast.makeText(EventDetailActivity.this,
                                        EventDetailActivity.this.getString(R.string.eventDeletedSuccessfully),
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
