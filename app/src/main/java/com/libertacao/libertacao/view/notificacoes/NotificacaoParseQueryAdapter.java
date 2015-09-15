package com.libertacao.libertacao.view.notificacoes;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Call;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

public class NotificacaoParseQueryAdapter extends ParseQueryAdapter<Call> {

    public NotificacaoParseQueryAdapter(Context context, QueryFactory<Call> queryFactory) {
        super(context, queryFactory);
    }

    @Override
    public View getItemView(final Call object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.row_notificacao, null);
        }
        super.getItemView(object, v, parent);

        ParseImageView notificacao_image = (ParseImageView) v.findViewById(R.id.notificacao_image);
        ParseFile imageFile = object.getParseFile("image");
        if (imageFile != null) {
            notificacao_image.setParseFile(imageFile);
            notificacao_image.loadInBackground();
        }

        // Just hold title String attribute to onClick listener
        final String title = object.getTitle();

        TextView notificacaoTitleView = (TextView) v.findViewById(R.id.notificacao_title);
        notificacaoTitleView.setText(title);
        TextView notificacaoSubtitleTextView = (TextView) v.findViewById(R.id.notificacao_subtitle);
        notificacaoSubtitleTextView.setText(object.getShortText());
        Button btn_share_notification = (Button) v.findViewById(R.id.btn_share_notification);
        btn_share_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, title); // TODO: define a better text
                sendIntent.setType("text/plain");
                getContext().startActivity(sendIntent);
            }
        });
        return v;
    }
}
