package com.libertacao.libertacao.manager;

import android.support.annotation.Nullable;

import com.libertacao.libertacao.persistence.UserPreferences;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class LoginManager {
    private static final LoginManager ourInstance = new LoginManager();

    public static LoginManager getInstance() {
        return ourInstance;
    }

    private LoginManager() {
    }

    public boolean isLoggedIn(){
        return ParseUser.getCurrentUser() != null;
    }

    // FIXME: this is only temporary. We need to find a way to do this with roles!
    public boolean isAdmin() {
        if(isLoggedIn()) {
            String objectId = ParseUser.getCurrentUser().getObjectId();
            return objectId.equals("dpbBxLtkMY") || objectId.equals("4r1i9gZkYD");
        } else {
            return false;
        }
    }

    public void logout() {
        UserPreferences.clearSharedPreferences();
        UserManager.getInstance().setCurrentLatLng(null);
    }

    @Nullable
    public String getUsername() {
        if(isLoggedIn()) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if(ParseFacebookUtils.isLinked(currentUser)) {
                return currentUser.getString("name");
            } else {
                return currentUser.getUsername();
            }
        } else {
            return null;
        }
    }
}
