package com.mad.declutter.model;

import com.mad.declutter.Constants;
import java.util.Map;

public class UserData {
    private String mToken;
    private long mUserId;
    private String mScreenName;
    private String mTokenSecret;
    private boolean mAuthenticated;

    public UserData(Map<String, ?> sharedPrefs) {
        for (Map.Entry<String, ?> entry : sharedPrefs.entrySet()) {
            Object entryValue = entry.getValue();

            switch (entry.getKey()) {
                case Constants.PREF_USER_ID:
                    this.mUserId = (long) entryValue;
                    break;
                case Constants.PREF_SCREEN_NAME:
                    this.mScreenName = entryValue.toString();
                case Constants.PREF_OAUTH_TOKEN:
                    this.mToken = entryValue.toString();
                    break;
                case Constants.PREF_OAUTH_SECRET:
                    this.mTokenSecret = entryValue.toString();
                    break;
                case Constants.PREF_AUTHENTICATED:
                    this.mAuthenticated = (boolean) entryValue;
                    break;
            }
        }
    }

    public UserData(long userId, String screenName, String token, String tokenSecret, boolean authenticated) {
        this.mToken = token;
        this.mUserId = userId;
        this.mScreenName = screenName;
        this.mTokenSecret = tokenSecret;
        this.mAuthenticated = authenticated;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long userId) {
        this.mUserId = userId;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public void setScreenName(String screenName) {
        this.mScreenName = screenName;
    }

    public String getTokenSecret() {
        return mTokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.mTokenSecret = tokenSecret;
    }

    public boolean isAuthenticated() {
        return mAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.mAuthenticated = authenticated;
    }
}
