package com.libertacao.libertacao.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

public class ConnectionManager {
    private static final ConnectionManager ourInstance = new ConnectionManager();

    public static ConnectionManager getInstance() {
        return ourInstance;
    }

    public boolean isOnline(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    private static boolean shouldAddHttp(String url) {
        return (url != null) && !(url.startsWith(ConnectionManager.HTTP) && url.startsWith(ConnectionManager.HTTPS));
    }

    public static String getUrlWithHttp(String url) {
        return shouldAddHttp(url)? (HTTP + url) : url;
    }
}
