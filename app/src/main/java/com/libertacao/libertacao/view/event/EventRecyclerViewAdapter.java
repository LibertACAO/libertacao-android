package com.libertacao.libertacao.view.event;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.data.Event;
import com.libertacao.libertacao.databinding.RowEventBinding;
import com.libertacao.libertacao.view.customviews.OrmliteCursorRecyclerViewAdapter;
import com.libertacao.libertacao.viewmodel.EventDataModel;

public class EventRecyclerViewAdapter extends OrmliteCursorRecyclerViewAdapter<Event, EventRecyclerViewAdapter.EventViewHolder> {
    public EventRecyclerViewAdapter(Context context) {
        super(context);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowEventBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.row_event,
                parent,
                false);
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, final Event event) {
        holder.bindEvent(event);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        final RowEventBinding binding;

        public EventViewHolder(RowEventBinding binding) {
            super(binding.cardView);
            this.binding = binding;
        }

        void bindEvent(Event event) {
            if (binding.getEventDataModel() == null) {
                binding.setEventDataModel(new EventDataModel((Activity)itemView.getContext(), event, binding.notificacaoImage));
            } else {
                binding.getEventDataModel().setEvent(event);
                binding.getEventDataModel().setEventImageView(binding.notificacaoImage);
            }
        }
    }
}
