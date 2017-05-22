package com.mad.declutter.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.Status;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.mad.declutter.db.DatabaseHelper;
import com.mad.declutter.model.Relationship;
import com.mad.declutter.model.Session;

public class TwitterHelper {

    // Twitter Configuration Keys
    private static final String TWITTER_CALLBACK_URL = "oauth://oob";
    private static final String TWITTER_CONSUMER_KEY = "jXYoqycRtKniFhstPOKcnFfRA";
    private static final String TWITTER_CONSUMER_SECRET = "2nuiZbHL3HPwT46qKjMsCart3moo7XLJa400XiSk5JZabnJsUl";

    // Twitter OAuth API Endpoints
    private static final String URL_TWITTER_AUTH = "auth_url";
    private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    private static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    private static final String LOG_KEY = "DECLUTTER";

    private Context mContext;

    // Twitter API Instances
    private static Twitter sTwitter;
    public static RequestToken sRequestToken;

    // Shared Preferences
    private static SharedPreferences sSharedPreferences;

    // Database
    private SQLiteDatabase mDbWrite;
    private DatabaseHelper mDbHelper;

    public TwitterHelper(Context context) {
        this.mContext = context;

        // Get database helper instance
        mDbHelper = DatabaseHelper.getInstance(context);
        mDbWrite = mDbHelper.getWritableDatabase();

        // Setup Twitter configuration
        ConfigurationBuilder configBuilder = new ConfigurationBuilder();
        configBuilder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        configBuilder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

        // Get Twitter instance with configuration
        Configuration configuration = configBuilder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        sTwitter = factory.getInstance();
    }

    public static boolean hasKeys() {
        return (TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0);
    }

    public void setAccessToken(AccessToken accessToken) {
        sTwitter.setOAuthAccessToken(accessToken);
    }

    public class TwitterLoginHandler extends AsyncTask<Void, Void, Void> {
        private Button mLoginButton;
        private boolean mAuthenticated;
        private ProgressBar mProgressBar;

        public TwitterLoginHandler(ProgressBar progressBar, Button loginButton, boolean authenticated) {
            this.mProgressBar = progressBar;
            this.mLoginButton = loginButton;
            this.mAuthenticated = authenticated;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressBar.setVisibility(View.VISIBLE);
            mLoginButton.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mAuthenticated) return null;

            try {
                sRequestToken = sTwitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(sRequestToken.getAuthenticationURL())));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            if (mAuthenticated) {
                Toast.makeText(mContext, "You're already authenticated with Twitter!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public class TwitterCallbackHandler extends AsyncTask<Uri, Void, AccessToken> {
        private ProgressBar mProgressBar;
        private Button mLoginButton;

        public TwitterCallbackHandler(ProgressBar progressBar, Button loginButton) {
            this.mProgressBar = progressBar;
            this.mLoginButton = loginButton;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressBar.setVisibility(View.VISIBLE);
            mLoginButton.setVisibility(View.GONE);
        }

        @Override
        protected AccessToken doInBackground(Uri... uris) {
            Uri uri = uris[0];
            AccessToken accessToken = null;

            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                // oAuth verifier
                final String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

                try {
                    // Get the access token
                    accessToken = sTwitter.getOAuthAccessToken(sRequestToken, verifier);

                    // Update user details
                    mDbHelper.insertUser(mDbWrite,
                            sTwitter.showUser(accessToken.getUserId())
                    );

                    Thread.sleep(2000);

                    Log.i(LOG_KEY, "OAuth Token: " + accessToken.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return accessToken;
        }

        @Override
        protected void onPostExecute(AccessToken accessToken) {
            if (accessToken == null) return;

            Session newSession = new Session(mContext,
                    accessToken.getUserId(),
                    accessToken.getScreenName(),
                    accessToken.getToken(),
                    accessToken.getTokenSecret(),
                    true
            );

            // Hide progress bar
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public class FetchTwitterFriends extends AsyncTask<Void, Void, Void> {
        private long mUserId;

        public FetchTwitterFriends(long userId) {
            this.mUserId = userId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            long[][] friendsIdsChunks;

            try {
                IDs friendsIDs = sTwitter.getFriendsIDs(-1);
                friendsIdsChunks = AppHelper.chunkArray(friendsIDs.getIDs(), 100);

                for (long[] friendsIdsChunk : friendsIdsChunks) {
                    ResponseList<User> friends = sTwitter.lookupUsers(friendsIdsChunk);

                    for (User friend : friends) {
                        Relationship friendship = new Relationship(
                                mUserId,
                                friend.getId(),
                                true
                        );

                        mDbHelper.insertUser(mDbWrite, friend);
                        mDbHelper.insertRelationship(mDbWrite, friendship);
                    }

                    Log.d("FETCH_TWITTER_FRIENDS", "Stored x" + String.valueOf(friends.toArray().length) + " friends and relationships.");
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) { }
    }

    public class FetchHomeTimeline extends AsyncTask<Void, Void, Void> {
        private long mUserId;

        public FetchHomeTimeline(long userId) {
            this.mUserId = userId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                int limit = 5;
                long sinceId = -1;
                int statusesCount = 0;
                int totalStatusesCount = 0;

                Paging cursor = new Paging(1, 200);

                for (int i = 1; i <= limit; i++) {
                    ResponseList<twitter4j.Status> statuses = sTwitter.getHomeTimeline(cursor);
                    statusesCount = statuses.toArray().length;
                    totalStatusesCount += statusesCount;

                    sinceId = statuses.get(statusesCount - 1).getId();
                    cursor.setSinceId(sinceId);

                    for (twitter4j.Status status : statuses) {
                        mDbHelper.insertStatus(mDbWrite, status);
                    }

                    Log.d("FETCH_HOME_TIMELINE", "Stored x" + String.valueOf(totalStatusesCount) + " tweets.");
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) { }
    }
}
