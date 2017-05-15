package com.mad.declutter.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import twitter4j.auth.AccessToken;

public class Session {
    private String mToken;
    private long mUserId;
    private String mScreenName;
    private String mTokenSecret;
    private boolean mAuthenticated;
    private AccessToken mAccessToken;

    // Shared Preferences
    private static SharedPreferences sSharedPreferences;

    private static final String PREF_NAME = "declutter_twitter";
    private static final String PREF_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_USER_ID = "user_id";
    private static final String PREF_SCREEN_NAME = "screen_name";
    private static final String PREF_AUTHENTICATED = "isAuthenticated";

    public Session(Context context) {
        sSharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        Map<String, ?> sharedPrefs = sSharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : sharedPrefs.entrySet()) {
            Object entryValue = entry.getValue();

            switch (entry.getKey()) {
                case PREF_USER_ID:
                    this.mUserId = (long) entryValue;
                    break;
                case PREF_SCREEN_NAME:
                    this.mScreenName = entryValue.toString();
                    break;
                case PREF_OAUTH_TOKEN:
                    this.mToken = entryValue.toString();
                    break;
                case PREF_OAUTH_SECRET:
                    this.mTokenSecret = entryValue.toString();
                    break;
                case PREF_AUTHENTICATED:
                    this.mAuthenticated = (boolean) entryValue;
                    break;
            }
        }

        // Set access token
        this.setAccessToken(this.mToken, this.mTokenSecret);
    }

    public Session(Context context, long userId, String screenName, String token, String tokenSecret, boolean authenticated) {
        this.mToken = token;
        this.mUserId = userId;
        this.mScreenName = screenName;
        this.mTokenSecret = tokenSecret;
        this.mAuthenticated = authenticated;

        // Set access token
        this.setAccessToken(this.mToken, this.mTokenSecret);

        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean(PREF_AUTHENTICATED, authenticated);
        editor.putString(PREF_SCREEN_NAME, screenName);
        editor.putLong(PREF_USER_ID, userId);
        editor.putString(PREF_OAUTH_TOKEN, token);
        editor.putString(PREF_OAUTH_SECRET, tokenSecret);
        editor.apply();
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

    public AccessToken getAccessToken() {
        return this.mAccessToken;
    }

    private void setAccessToken(String token, String tokenSecret) {
        this.mAccessToken = new AccessToken(token, tokenSecret);
    }

    public static boolean hasAuthenticated(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, 0);

        return sharedPref.getBoolean(PREF_AUTHENTICATED, false);
    }

    public void clear() {
        // Clear the shared preferences
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
