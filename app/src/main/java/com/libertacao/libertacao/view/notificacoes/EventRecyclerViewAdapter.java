package com.libertacao.libertacao.view.notificacoes;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.databinding.RowNotificacaoBinding;
import com.libertacao.libertacao.view.customviews.OrmliteCursorRecyclerViewAdapter;
import com.libertacao.libertacao.viewmodel.EventDataModel;

public class EventRecyclerViewAdapter extends OrmliteCursorRecyclerViewAdapter<Event, EventRecyclerViewAdapter.EventViewHolder> {
    public EventRecyclerViewAdapter(Context context) {
        super(context);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowNotificacaoBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.row_event,
                parent,
                false);
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, final Event event) {
        holder.bindRepository(event);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        final RowNotificacaoBinding binding;

        public EventViewHolder(RowNotificacaoBinding binding) {
            super(binding.cardView);
            this.binding = binding;
        }

        void bindRepository(Event event) {
            if (binding.getEventDataModel() == null) {
                binding.setEventDataModel(new EventDataModel(itemView.getContext(), event));
            } else {
                binding.getEventDataModel().setEvent(event);
            }
        }
    }
}
