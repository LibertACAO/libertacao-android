package com.libertacao.libertacao.view.rssitem;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import com.libertacao.libertacao.databinding.RowRssItemBinding;
import com.libertacao.libertacao.rss.RssItem;
import com.libertacao.libertacao.viewmodel.RssItemDataModel;

public class RssItemViewHolder extends RecyclerView.ViewHolder {
    final RowRssItemBinding binding;

    public RssItemViewHolder(RowRssItemBinding binding) {
        super(binding.cardView);
        this.binding = binding;
    }

    void bindRssItem(RssItem rssItem) {
        if (binding.getRssItemDataModel() == null) {
            binding.setRssItemDataModel(new RssItemDataModel((Activity)itemView.getContext(), rssItem));
        } else {
            binding.getRssItemDataModel().setRssItem(rssItem);
        }
    }
}