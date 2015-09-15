package com.libertacao.libertacao.view.notificacoes;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
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
    public View getItemView(Call object, View v, ViewGroup parent) {
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

        TextView notificacaoTitleView = (TextView) v.findViewById(R.id.notificacao_title);
        notificacaoTitleView.setText(object.getTitle());
        TextView notificacaoSubtitleTextView = (TextView) v.findViewById(R.id.notificacao_subtitle);
        notificacaoSubtitleTextView.setText(object.getShortText());
        return v;
    }
}
