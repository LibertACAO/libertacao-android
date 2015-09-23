package com.libertacao.libertacao.view.notificacoes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.view.customviews.OrmliteCursorRecyclerViewAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EventRecyclerViewAdapter extends OrmliteCursorRecyclerViewAdapter<Event, EventRecyclerViewAdapter.ViewHolder> {
    private Callback callback;

    public EventRecyclerViewAdapter(Context context) {
        super(context);
    }

    public void setCallback(Callback callback){
        this.callback = callback;
    }

    @Override
    public EventRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notificacao, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final Event event) {
        // Just hold title String attribute to onClick listener
        final String title = event.getTitle();

        holder.title.setText(title);
        holder.subtitle.setText(event.getSubtitle());
        ImageLoader.getInstance().displayImage(event.getImage(), holder.image);

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null) {
                    callback.onItemClick(event);
                }
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView image;
        public final TextView title;
        public final TextView subtitle;
        public final Button share;

        public ViewHolder(View drawerView) {
            super(drawerView);
            image = (ImageView) drawerView.findViewById(R.id.notificacao_image);
            title = (TextView) drawerView.findViewById(R.id.notificacao_title);
            subtitle = (TextView) drawerView.findViewById(R.id.notificacao_subtitle);
            share = (Button) drawerView.findViewById(R.id.btn_share_notification);
        }
    }

    public interface Callback {
        void onItemClick(Event event);
    }
}
