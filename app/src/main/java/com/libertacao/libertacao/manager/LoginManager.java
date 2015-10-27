package com.libertacao.libertacao.manager;

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

    // TODO: this is only temporary. We need to find a way to do this with roles!
    public boolean isAdmin() {
        if(isLoggedIn()) {
            String objectId = ParseUser.getCurrentUser().getObjectId();
            return objectId.equals("dpbBxLtkMY") || objectId.equals("4r1i9gZkYD");
        } else {
            return false;
        }
    }
}
