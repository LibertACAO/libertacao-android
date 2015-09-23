package com.libertacao.libertacao.view.notificacoes;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.databinding.ActivityNotificacaoDetailBinding;
import com.libertacao.libertacao.persistence.DatabaseHelper;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.viewmodel.EventDataModel;

public class EventDetail extends AppCompatActivity {
    public static final String EVENT_ID = "EVENT_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int eventId = getIntent().getIntExtra(EVENT_ID, -1);
        if (eventId == -1){
            notFoundEvent();
        } else {
            Event event = DatabaseHelper.getHelper(this).getEventIntegerRuntimeExceptionDao().queryForId(eventId);
            if(event != null){
                ActivityNotificacaoDetailBinding binding = DataBindingUtil.setContentView(EventDetail.this, R.layout.activity_notificacao_detail);
                binding.setEventDataModel(new EventDataModel(event));
                ViewUtils.setHomeAsUpEnabled(EventDetail.this);
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
