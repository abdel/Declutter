/*
 * Copyright (C) 2017 Abdelrahman Ahmed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mad.declutter.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import twitter4j.auth.AccessToken;

/**
 * The Session class is a model that represents the session data that is stored in the Shared
 * Preferences. The Session class is implemented in such a way to allow any class easy access to the
 * current user's session data by utilising the Shared Preferences.
 *
 * @author Abdelrahman Ahmed
 */
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

    /**
     * A constructor for the Session that only takes the application context, and
     * populates the Session object with the data inside the Shared Preferences.
     *
     * @param context The application context
     */
    public Session(Context context) {
        // Get all the values from the Shared Preferences
        sSharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        Map<String, ?> sharedPrefs = sSharedPreferences.getAll();

        // Iterate over the pref values and set the class attributes
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

    /**
     * A constructor for the Session class that takes specified values for the
     * class attributes which then populates the SharedPreferences with these values for later use.
     *
     * @param context The application context
     * @param userId The current user ID
     * @param screenName The user's screen name on Twitter
     * @param token The user's access token
     * @param tokenSecret The user's access token secret
     * @param authenticated Whether or not the user is authenticated
     */
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

    /**
     * Gets the user access token
     *
     * @return String The user token
     */
    public String getToken() {
        return mToken;
    }

    /**
     * Sets the user token
     *
     * @param token The user access token
     */
    public void setToken(String token) {
        this.mToken = token;
    }

    /**
     * Gets the user ID
     *
     * @return long The user ID
     */
    public long getUserId() {
        return mUserId;
    }

    /**
     * Sets the user ID
     *
     * @param userId The user ID
     */
    public void setUserId(long userId) {
        this.mUserId = userId;
    }

    /**
     * Gets the user's screen name/username
     *
     * @return String he user screen name
     */
    public String getScreenName() {
        return mScreenName;
    }

    /**
     * Sets the user's screen name
     *
     * @param screenName The user's screen name
     */
    public void setScreenName(String screenName) {
        this.mScreenName = screenName;
    }

    /**
     * Gets the user token secret
     *
     * @return String The access token secret
     */
    public String getTokenSecret() {
        return mTokenSecret;
    }

    /**
     * Sets the user token secret
     *
     * @param tokenSecret The user token secret
     */
    public void setTokenSecret(String tokenSecret) {
        this.mTokenSecret = tokenSecret;
    }

    /**
     * The authentication status of the user
     *
     * @return boolean Whether the user has authenticated
     */
    public boolean isAuthenticated() {
        return mAuthenticated;
    }

    /**
     * Sets the authenticated status for the user
     *
     * @param authenticated Whether the user has authenticated
     */
    public void setAuthenticated(boolean authenticated) {
        this.mAuthenticated = authenticated;
    }

    /**
     * Gets the user access token object
     *
     * @return AccessToken
     */
    public AccessToken getAccessToken() {
        return this.mAccessToken;
    }

    /**
     * Takes the token and token secret to create an AccessToken object
     *
     * @param token The user token
     * @param tokenSecret The token secret
     */
    private void setAccessToken(String token, String tokenSecret) {
        this.mAccessToken = new AccessToken(token, tokenSecret);
    }

    /**
     * Checks the SharedPreferences to see if the user is authenticated. The method is static and
     * does not rely on the class object by fetching the value directly from the SharedPrefs.
     *
     * @param context The application context
     * @return boolean Whether the user has authenticated
     */
    public static boolean hasAuthenticated(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, 0);

        return sharedPref.getBoolean(PREF_AUTHENTICATED, false);
    }

    /**
     * Clears the data from SharedPreferences
     */
    public void clear() {
        // Clear the shared preferences
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
