package com.libertacao.libertacao.view.notificacoes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Call;
import com.libertacao.libertacao.util.ViewUtils;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NotificacaoDetail extends AppCompatActivity {
    public static final String NOTIFICACAO_OBJECT_ID = "NOTIFICACAO_OBJECT_ID";

    /**
     * Interface elements
     */
    @InjectView(R.id.notificacao_detail_image) ParseImageView mImageView;
    @InjectView(R.id.notificacao_detail_title) TextView mTitleTextView;
    @InjectView(R.id.notificacao_detail_subtitle) TextView mSubtitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacao_detail);
        ViewUtils.setHomeAsUpEnabled(this);
        ButterKnife.inject(this);
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
                    ParseFile imageFile = object.getParseFile("image");
                    if (imageFile != null) {
                        mImageView.setParseFile(imageFile);
                        mImageView.loadInBackground();
                    }
                    mTitleTextView.setText(object.getTitle());
                    mSubtitleTextView.setText(object.getShortText());
                }
            });

        }
    }
}
