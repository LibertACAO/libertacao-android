package com.libertacao.libertacao.view.notificacoes;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Call;
import com.libertacao.libertacao.databinding.ActivityNotificacaoDetailBinding;
import com.libertacao.libertacao.util.ViewUtils;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class NotificacaoDetail extends AppCompatActivity {
    public static final String NOTIFICACAO_OBJECT_ID = "NOTIFICACAO_OBJECT_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String notificacaoObjectId = getIntent().getStringExtra(NOTIFICACAO_OBJECT_ID);
        if(TextUtils.isEmpty(notificacaoObjectId)){
            //TODO: show an error message
        } else {
            // TODO: find a way to not query again parse... we already have this object locally :(
            ParseQuery<Call> query = new ParseQuery<>("Call");
            query.whereEqualTo("objectId", notificacaoObjectId);
            query.getFirstInBackground(new GetCallback<Call>() {
                @Override
                public void done(Call object, ParseException e) {
                    ActivityNotificacaoDetailBinding binding = DataBindingUtil.setContentView(NotificacaoDetail.this, R.layout.activity_notificacao_detail);
                    binding.setNotification(object);
                    ViewUtils.setHomeAsUpEnabled(NotificacaoDetail.this);
                }
            });

        }
    }
}
