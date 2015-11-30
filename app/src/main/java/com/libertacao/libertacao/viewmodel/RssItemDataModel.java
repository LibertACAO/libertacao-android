package com.libertacao.libertacao.viewmodel;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.rss.RssItem;

/**
 * This class is the ViewModel of the MVVM architecture pattern, representing a RssItem
 */
@SuppressWarnings("unused")
public class RssItemDataModel extends BaseObservable {
    /**
     * Hold on to activity
     */
    protected final Activity activity;

    /**
     * Related new. This has all data that we need.
     */
    protected RssItem rssItem;

    public RssItemDataModel(Activity activity, RssItem rssItem) {
        this.activity = activity;
        this.rssItem = rssItem;
    }

    public String getTitle() {
        return rssItem.getTitle();
    }

    public String getDescription() {
        return rssItem.getDescription();
    }

    public String getPoweredBy() {
        return "Fonte: ANDA";
    }

    public void onRssItemClick(View view){
//        if(!TextUtils.isEmpty(rssItem.getLink())) {
            try {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConnectionManager.getUrlWithHttp(rssItem.getLink())));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.anda.jor.br"));
                activity.startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(activity, activity.getString(R.string.notFoundActivity), Toast.LENGTH_LONG).show();
            }
//        }
    }

    /**
     * Set a new rssItem to this NewDataModel. It allows recycling NewDataModels within the RecyclerView adapter.
     * This takes advantage of the BaseObservable superclass to call notifyChange to update UI accordingly. Easy :)
     * @param rssItem new rssItem
     */
    public void setRssItem(RssItem rssItem) {
        this.rssItem = rssItem;
        notifyChange();
    }
}
