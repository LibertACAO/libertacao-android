package com.libertacao.libertacao.util;

import android.widget.ImageView;

import com.libertacao.libertacao.MyApp;
import com.libertacao.libertacao.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MyImageLoader {
    private static MyImageLoader ourInstance = new MyImageLoader();
    private DisplayImageOptions eventOptions;

    public static MyImageLoader getInstance() {
        return ourInstance;
    }

    private MyImageLoader() {
        if(!ImageLoader.getInstance().isInited()) {
            setup();
        }
    }

    private void setup() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MyApp.getAppContext()).defaultDisplayImageOptions
                (defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        eventOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.ic_iconmonstr_picture_multi_2_icon_256)
                .showImageForEmptyUri(R.drawable.ic_iconmonstr_picture_multi_2_icon_256)
                .showImageOnFail(R.drawable.ic_iconmonstr_picture_multi_2_icon_256)
                .build();
    }

    @SuppressWarnings("unused")
    public void displayImage(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView);
    }

    public void displayImage(String uri, ImageView imageView, DisplayImageOptions displayImageOptions) {
        ImageLoader.getInstance().displayImage(uri, imageView, displayImageOptions);
    }

    public void displayEventImage(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView, eventOptions);
    }

    @SuppressWarnings("unused")
    public void clean() {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().clearDiskCache();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().clearMemoryCache();
    }
}
