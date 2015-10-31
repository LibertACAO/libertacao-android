package com.libertacao.libertacao.view.admin;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.databinding.ActivityEditEventBinding;
import com.libertacao.libertacao.manager.LoginManager;
import com.libertacao.libertacao.persistence.DatabaseHelper;
import com.libertacao.libertacao.util.ImageUtils;
import com.libertacao.libertacao.util.ViewUtils;
import com.libertacao.libertacao.viewmodel.EditEventDataModel;

import timber.log.Timber;

public class EditEventActivity extends AppCompatActivity implements EditEventDataModel.OnSelectImageClick {
    public static final String EVENT_ID = "EVENT_ID";
    private EditEventDataModel editEventDataModel;

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
                Timber.d(editEventDataModel.getEvent().getTitle());
                return true;
            case android.R.id.home:
                // Without this the back button doesn't work
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        String filePath = ImageUtils.onActivityResult(this, requestCode, resultCode, data);
        if(filePath != null){
            editEventDataModel.setEventLocalImage(filePath);
            Toast.makeText(this, "Foto selecionada com sucesso!", Toast.LENGTH_SHORT).show();
        }
    }
}
