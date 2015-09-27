package com.libertacao.libertacao.manager;

import com.parse.ParseUser;

public class LoginManager {
    private static LoginManager ourInstance = new LoginManager();

    public static LoginManager getInstance() {
        return ourInstance;
    }

    private LoginManager() {
    }

    public boolean isLoggedIn(){
        return ParseUser.getCurrentUser() != null;
    }
}
