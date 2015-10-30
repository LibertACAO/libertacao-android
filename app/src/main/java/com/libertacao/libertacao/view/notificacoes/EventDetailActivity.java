package com.libertacao.libertacao.view.notificacoes;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.databinding.ActivityNotificacaoDetailBinding;
import com.libertacao.libertacao.persistence.DatabaseHelper;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.view.map.MapFragment;
import com.libertacao.libertacao.viewmodel.EventDataModel;

public class EventDetailActivity extends AppCompatActivity {
    private static final String EVENT_ID = "EVENT_ID";

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
            Event event = DatabaseHelper.getHelper(this).getEventIntegerRuntimeExceptionDao().queryForId(eventId);
            if(event != null){
                ActivityNotificacaoDetailBinding binding = DataBindingUtil.setContentView(EventDetailActivity.this, R.layout.activity_event_detail);
                binding.setEventDataModel(new EventDataModel(this, event));
                ViewUtils.setHomeAsUpEnabled(EventDetailActivity.this);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.event_map, MapFragment.newInstance(event)).commit();
            } else {
                notFoundEvent();
            }
        }
    }

    /**
     * Helper methods to setup UI
     */

    private void notFoundEvent() {
        Toast.makeText(getBaseContext(), getString(R.string.eventNotFound), Toast.LENGTH_LONG).show();
        finish();
    }
}
