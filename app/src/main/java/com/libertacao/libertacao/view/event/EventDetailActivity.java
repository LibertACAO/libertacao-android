package com.libertacao.libertacao.view.event;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.DataConfig;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.databinding.ActivityEventDetailBinding;
import com.libertacao.libertacao.event.SyncedEvent;
import com.libertacao.libertacao.manager.LoginManager;
import com.libertacao.libertacao.manager.SyncManager;
import com.libertacao.libertacao.persistence.DatabaseHelper;
import com.libertacao.libertacao.util.MyImageLoader;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.view.customviews.WorkaroundMapFragment;
import com.libertacao.libertacao.view.map.MapFragment;
import com.libertacao.libertacao.viewmodel.EventDataModel;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class EventDetailActivity extends AppCompatActivity {
    private static final String EVENT_ID = "EVENT_ID";
    private static final String EVENT_OBJECT_ID = "EVENT_OBJECT_ID";
    private Event event;
    private ProgressDialog fetchingEventProgressDialog;

    @InjectView(R.id.event_detail_scroll_view) NestedScrollView scrollView;
    @InjectView(R.id.appbar) AppBarLayout appbarLayout;
    @InjectView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout collapsingToolbarLayout;

    public static Intent newIntent(Context context, Event event){
        Intent intent = new Intent(context, EventDetailActivity.class);
        intent.putExtra(EVENT_ID, event.getId());
        return intent;
    }

    public static Intent newIntent(Context context, String eventObjectId){
        Intent intent = new Intent(context, EventDetailActivity.class);
        intent.putExtra(EVENT_OBJECT_ID, eventObjectId);
        return intent;
    }

    public EventDetailActivity(){
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        int eventId = getIntent().getIntExtra(EVENT_ID, -1);
        if (eventId == -1){
            String eventObjectId = getIntent().getStringExtra(EVENT_OBJECT_ID);
            List<Event> events = DatabaseHelper.getHelper(this).getEventIntegerRuntimeExceptionDao().queryForEq(DataConfig.OBJECT_ID, eventObjectId);
            if(events.isEmpty()) {
                fetchingEventProgressDialog = ViewUtils.showProgressDialog(this, getString(R.string.fetchingEvent), true);
                SyncManager.getInstance().sync(this);
            } else {
                event = events.get(0);
                setupViewWithEvent();
            }
        } else {
            event = DatabaseHelper.getHelper(this).getEventIntegerRuntimeExceptionDao().queryForId(eventId);
            if(event != null){
                setupViewWithEvent();
            } else {
                notFoundEvent();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(LoginManager.getInstance().isAdmin()) {
            getMenuInflater().inflate(R.menu.admin_event_detail_menu, menu);
            menu.findItem(R.id.menu_activate_event).setVisible(!event.isEnabled());
            menu.findItem(R.id.menu_deactivate_event).setVisible(event.isEnabled());
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_edit_event:
                startActivity(EditEventActivity.newIntent(this, event));
                return true;
            case R.id.menu_activate_event:
                activateEvent();
                return true;
            case R.id.menu_deactivate_event:
                deactivateEvent();
                return true;
            case R.id.menu_delete_event:
                deleteEvent();
                return true;
            case android.R.id.home:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    boolean fullyExpanded = (appbarLayout.getHeight() - appbarLayout.getBottom()) == 0;
                    if(fullyExpanded) {
                        finishAfterTransition();
                        return true;
                    }
                }
                break;
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

    private void setupViewWithEvent() {
        ActivityEventDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail);
        binding.setEventDataModel(new EventDataModel(this, event));
        ViewUtils.setHomeAsUpEnabled(this);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        ButterKnife.inject(this);

        if(!event.hasImage()) {
            appbarLayout.setExpanded(false);
        }

        setupMapFragment();
        setToolbarAndStatusColorAccordingToEventImage();
    }

    private void activateEvent() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.activate))
                .setMessage(getString(R.string.activateConfirm))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog pd = ViewUtils.showProgressDialog(EventDetailActivity.this, getString(R.string.activatingEvent), false);
                        ParseObject eventParseObject = new ParseObject(Event.EVENT);
                        eventParseObject.setObjectId(event.getObjectId());
                        eventParseObject.put(Event.ENABLED, true);
                        eventParseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {ViewUtils.hideProgressDialog(pd);
                                Toast.makeText(EventDetailActivity.this,
                                        EventDetailActivity.this.getString(R.string.eventActivatedSuccessfully),
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deactivateEvent() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.deactivate))
                .setMessage(getString(R.string.deactivateConfirm))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog pd = ViewUtils.showProgressDialog(EventDetailActivity.this, getString(R.string.deactivatingEvent), false);
                        ParseObject eventParseObject = new ParseObject(Event.EVENT);
                        eventParseObject.setObjectId(event.getObjectId());
                        eventParseObject.put(Event.ENABLED, false);
                        eventParseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {ViewUtils.hideProgressDialog(pd);
                                Toast.makeText(EventDetailActivity.this,
                                        EventDetailActivity.this.getString(R.string.eventDeactivatedSuccessfully),
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
                                DatabaseHelper.getHelper(EventDetailActivity.this).getEventIntegerRuntimeExceptionDao().delete(event);
                                ViewUtils.hideProgressDialog(pd);
                                Toast.makeText(EventDetailActivity.this,
                                        EventDetailActivity.this.getString(R.string.eventDeletedSuccessfully),
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setupMapFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment = MapFragment.newInstance(event);
        fragmentManager.beginTransaction().replace(R.id.event_map, mapFragment).commit();
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
    }

    private void setToolbarAndStatusColorAccordingToEventImage() {
        if(event.hasImage()) {
            MyImageLoader.getInstance().getImageLoader().loadImage(event.getImage(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    Palette.from(loadedImage).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            int darkVibrantColor = palette.getDarkVibrantColor(getResources().getColor(R.color.primaryDark));
                            int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.primary));
                            collapsingToolbarLayout.setContentScrimColor(vibrantColor);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Window window = EventDetailActivity.this.getWindow();
                                window.setStatusBarColor(darkVibrantColor);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * Events
     */

    @SuppressWarnings("unused")
    public void onEventMainThread(SyncedEvent syncedEvent) {
        if(fetchingEventProgressDialog != null) {
            String eventObjectId = getIntent().getStringExtra(EVENT_OBJECT_ID);
            List<Event> events = DatabaseHelper.getHelper(this).getEventIntegerRuntimeExceptionDao().queryForEq(DataConfig.OBJECT_ID, eventObjectId);
            if(events.isEmpty()) {
                notFoundEvent();
            } else {
                event = events.get(0);
                setupViewWithEvent();
            }
            ViewUtils.hideProgressDialog(fetchingEventProgressDialog);
        }
    }
}
