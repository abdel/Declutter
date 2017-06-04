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

package com.mad.declutter.helpers;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.widget.ProgressBar;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.database.sqlite.SQLiteDatabase;

import twitter4j.IDs;
import twitter4j.User;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.ResponseList;
import twitter4j.TwitterFactory;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.mad.declutter.R;
import com.mad.declutter.activity.MainActivity;
import com.mad.declutter.activity.TimelineActivity;
import com.mad.declutter.model.Session;
import com.mad.declutter.db.DatabaseHelper;
import com.mad.declutter.model.Relationship;
import com.mad.declutter.adapter.FriendAdapter;
import com.mad.declutter.adapter.TimelineAdapter;

/**
 * The TwitterHelper utilises the Twitter4J library and handles all the interactions with the Twitter API.
 * The class consists of multiple Async Tasks for user authentication and fetching data from the
 * different API endpoints, such as fetching the user's friends and timeline.
 *
 * The TWITTER_CONSUMER_KEY and TWITTER_CONSUMER_SECRET for the Twitter application must be set in order
 * to authenticate and use the Twitter API.
 *
 * @author Abdelrahman Ahmed
 */
public class TwitterHelper {
    private Context mContext;
    private SQLiteDatabase mDbRead;
    private SQLiteDatabase mDbWrite;
    private DatabaseHelper mDbHelper;

    // Twitter API Instances
    private static Twitter sTwitter;
    public static RequestToken sRequestToken;

    // Constants
    private static final String LOG_KEY = "TwitterHelper";
    private static final String TWITTER_CALLBACK_URL = "oauth://oob";
    private static final String TWITTER_CONSUMER_KEY = "jXYoqycRtKniFhstPOKcnFfRA";
    private static final String TWITTER_CONSUMER_SECRET = "2nuiZbHL3HPwT46qKjMsCart3moo7XLJa400XiSk5JZabnJsUl";
    private static final String TWITTER_OAUTH_VERIFIER_URL = "oauth_verifier";

    /**
     * The constructor for TwitterHelper which sets up the Twitter configuration
     * and creates a new instance of TwitterFactory from the Twitter4j library
     *
     * @param context The application context
     */
    public TwitterHelper(Context context) {
        this.mContext = context;

        // Get database helper instance
        mDbHelper = DatabaseHelper.getInstance(context);
        mDbWrite = mDbHelper.getWritableDatabase();
        mDbRead = mDbHelper.getReadableDatabase();

        // Setup Twitter configuration
        ConfigurationBuilder configBuilder = new ConfigurationBuilder();
        configBuilder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        configBuilder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

        // Get Twitter instance with configuration
        Configuration configuration = configBuilder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        sTwitter = factory.getInstance();
    }

    /**
     * Checks if the application keys are set
     *
     * @return boolean True if the keys are not empty
     */
    public static boolean hasKeys() {
        return (TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0);
    }

    /**
     * Sets the oAuth Access Token in the current TwitterFactory instance, so it can
     * be used in future requests to the Twitter API
     *
     * @param accessToken AccessToken object containing the oauth token and secret
     */
    public void setAccessToken(AccessToken accessToken) {
        sTwitter.setOAuthAccessToken(accessToken);
    }

    /**
     * The TwitterLoginHandler is an Async Task that handles the initial authentication request to
     * Twitter when the user clicks the Login button. It sends an OAuthRequestToken to the Twitter
     * Authentication endpoint, with a callback URL for when the user logs in on Twitter.
     *
     * @author Abdelrahman Ahmed
     */
    public class TwitterLoginHandler extends AsyncTask<Void, Void, Void> {
        private Button mLoginButton;
        private boolean mAuthenticated;
        private ProgressBar mProgressBar;

        /**
         * The constructor for the class takes the view ids for the progress bar and login button to
         * handle their visibility before and after the task execution.
         *
         * @param progressBar The Progress bar in MainActivity
         * @param loginButton The "Login to Twitter" button in MainActivity
         * @param authenticated Whether or not the user is already authenticated
         */
        public TwitterLoginHandler(ProgressBar progressBar, Button loginButton, boolean authenticated) {
            this.mProgressBar = progressBar;
            this.mLoginButton = loginButton;
            this.mAuthenticated = authenticated;
        }

        /**
         * onPreExecute hides the login button, and displays a progress bar before the task starts
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressBar.setVisibility(View.VISIBLE);
            mLoginButton.setVisibility(View.GONE);
        }

        /**
         * Creates an OAuthRequestToken with a callback URL using the Twitter4j library, then redirects
         * the user to Twitter.com to login using their username and password
         *
         * @param params Void parameters
         * @return null
         */
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

        /**
         * If the user was already authenticated, display a Toast to indicate so
         *
         * @param param Void parameter
         */
        @Override
        protected void onPostExecute(Void param) {
            if (mAuthenticated) {
                Toast.makeText(mContext, R.string.authenticated, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * The TwitterCallbackHandler is an AsyncTask that is executed when the user is redirected back
     * from Twitter.com after successfully logging in. The task matches the callback URL, and gets
     * the OAuthAccessToken from Twitter using the twitter4j library. The task then stores the
     * user data into the database, and updates the Session with the details.
     *
     * @author Abdelrahman Ahmed
     */
    public class TwitterCallbackHandler extends AsyncTask<Uri, Void, AccessToken> {
        private ProgressBar mProgressBar;
        private Button mLoginButton;
        private Activity mActivity;

        /**
         * The constructor for the class which takes the progress bar and login button to change
         * their visibility before and after the task execution.
         *
         * @param progressBar The Progress bar in MainActivity
         * @param loginButton The "Login to Twitter" button in MainActivity
         */
        public TwitterCallbackHandler(Activity activity, ProgressBar progressBar, Button loginButton) {
            this.mActivity = activity;
            this.mLoginButton = loginButton;
            this.mProgressBar = progressBar;
        }

        /**
         * onPreExecute hides the login button, and displays a progress bar before the task starts
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressBar.setVisibility(View.VISIBLE);
            mLoginButton.setVisibility(View.GONE);
        }

        /**
         * Verifies the callback url, and then requests an OAuth Access Token for the user. It then
         * fetches the user data from Twitter, and stores it in the database. The AccessToken is then
         * passed to the onPostExecute method.
         *
         * @param uris The callback URL
         * @return AccessToken The user's OAuth Access Token
         */
        @Override
        protected AccessToken doInBackground(Uri... uris) {
            Uri uri = uris[0];
            AccessToken accessToken = null;

            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                // oAuth verifier
                final String verifier = uri.getQueryParameter(TWITTER_OAUTH_VERIFIER_URL);

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


        /**
         * The onPostExecute method takes the AccessToken and creates a new Session for the user,
         * storing any relevant details that can be used throughout the application activity.
         *
         * @param accessToken The User's access token
         */
        @Override
        protected void onPostExecute(AccessToken accessToken) {
            if (accessToken == null) return;

            // Create a new user session
            Session newSession = new Session(mContext,
                    accessToken.getUserId(),
                    accessToken.getScreenName(),
                    accessToken.getToken(),
                    accessToken.getTokenSecret(),
                    true
            );

            // Hide progress bar
            mProgressBar.setVisibility(View.GONE);

            mActivity.startActivity(new Intent(mActivity, TimelineActivity.class));
        }
    }

    /**
     * The FetchTwittterFriends is an Async Task that fetches the logged in user's friends from
     * Twitter and stores both the friend data and the relationship to the user in the database.
     *
     * @author Abdelrahman Ahmed
     */
    public class FetchTwitterFriends extends AsyncTask<Void, Void, Void> {
        private long mUserId;
        private TextView mEmptyView;
        private RecyclerView mFriendsView;
        private ProgressBar mFriendsProgress;
        private FriendAdapter mFriendAdapter;

        /**
         * A constructor for FetchTwitterFriends that sets the attributes required for the task
         *
         * @param userId The current user ID
         * @param friendAdapter The FriendAdapter for the RecyclerView
         * @param friendsView The RecyclerView containing the friends list
         * @param friendsProgress A progress bar to indicate the task progress
         * @param emptyView A TextView that's displayed when there is no data
         */
        public FetchTwitterFriends(long userId, FriendAdapter friendAdapter, RecyclerView friendsView, ProgressBar friendsProgress, TextView emptyView) {
            this.mUserId = userId;
            this.mEmptyView = emptyView;
            this.mFriendsView = friendsView;
            this.mFriendAdapter = friendAdapter;
            this.mFriendsProgress = friendsProgress;
        }

        /**
         * OnPreExecute hides the current friends view, and displays the progress bar before
         * the task begins and until its completed
         */
        @Override
        protected void onPreExecute() {
            mEmptyView.setVisibility(View.GONE);
            mFriendsView.setVisibility(View.GONE);
            mFriendsProgress.setVisibility(View.VISIBLE);
        }

        /**
         * doInBackground fetches about 5000 friend ids for this user from Twitter, deletes the old
         * relationships (to ensure users who were unfollowed are removed). It then divides the
         * friend id array into smaller chunks as the lookupUsers endpoint only takes 100 ids
         * per request. Using that endpoint, the task then fetches the details for each user and
         * updates the database with the user data and the relationship to the user.
         *
         * @param params Void parameters
         * @return null
         */
        @Override
        protected Void doInBackground(Void... params) {
            long[][] friendsIdsChunks;

            try {
                // Get friend IDs and divide them into chunks of arrays with maximum size of a 100
                IDs friendsIDs = sTwitter.getFriendsIDs(-1);
                friendsIdsChunks = AppHelper.chunkArray(friendsIDs.getIDs(), 100);

                // Delete old relationships for the user
                mDbHelper.deleteRelationships(mDbWrite, mUserId);

                // Iterates through all the smaller arrays, and fetches the data for each 100 users
                for (long[] friendsIdsChunk : friendsIdsChunks) {
                    ResponseList<User> friends = sTwitter.lookupUsers(friendsIdsChunk);

                    for (User friend : friends) {
                        // Create a relationship for this friend
                        Relationship friendship = new Relationship(
                                mUserId,
                                friend.getId(),
                                1,
                                0
                        );

                        // Insert friend and their relationship
                        mDbHelper.insertUser(mDbWrite, friend);
                        mDbHelper.insertRelationship(mDbWrite, friendship);
                    }

                    // Log the number of stored friends
                    Log.d(LOG_KEY, "Stored " + String.valueOf(friends.toArray().length) + " friends and relationships.");
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Swap the cursor to update the view with new data, hide the progress and re-display
         * the friends list
         *
         * @param param Void parameter
         */
        @Override
        protected void onPostExecute(Void param) {
            // Fetch the new friends and swap the cursor to update the view
            Cursor newFriends = mDbHelper.getFriends(mDbRead, mUserId);
            mFriendAdapter.swapCursor(newFriends);

            mFriendsProgress.setVisibility(View.GONE);
            mFriendsView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * The FetchHomeTimeline is an Async Task that uses the home_timeline on Twitter, to fetch
     * tweets from the user's home timeline (i.e. the tweets from people they follow). All the tweets
     * are then stored in the database.
     *
     * @author Abdelrahman Ahmed
     */
    public class FetchHomeTimeline extends AsyncTask<Void, Void, Void> {
        private long mUserId;
        private TextView mEmptyView;
        private RecyclerView mTimelineView;
        private ProgressBar mTimelineProgress;
        private TimelineAdapter mTimelineAdapter;

        /**
         * A constructor for FetchHomeTimeline that sets the attributes required for the task
         *
         * @param userId The current user ID
         * @param timelineAdapter The TimelineAdapter for the RecyclerView
         * @param timelineView The RecyclerView containing the timeline
         * @param timelineProgress A progress bar to indicate the task progress
         * @param emptyView A TextView that's displayed when there is no data
         */
        public FetchHomeTimeline(long userId, TimelineAdapter timelineAdapter, RecyclerView timelineView, ProgressBar timelineProgress, TextView emptyView) {
            this.mUserId = userId;
            this.mEmptyView = emptyView;
            this.mTimelineView = timelineView;
            this.mTimelineAdapter = timelineAdapter;
            this.mTimelineProgress = timelineProgress;
        }

        /**
         * OnPreExecute hides the current friends view, and displays the progress bar before
         * the task begins and until its completed
         */
        @Override
        protected void onPreExecute() {
            mEmptyView.setVisibility(View.GONE);
            mTimelineView.setVisibility(View.GONE);
            mTimelineProgress.setVisibility(View.VISIBLE);
        }

        /**
         * The doInBackground method fetches about 1K of the recent tweets from the user's home timeline
         * using the cursoring method. Tweets are modelled by the Twitter4j library's Status class
         * and are then directly stored into the database.
         *
         * @param params Void parameters
         * @return null
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                int limit = 5;
                long sinceId = -1;
                int statusesCount = 0;
                int totalStatusesCount = 0;

                // The home_timeline endpoint can only retrieve up to 200 tweets per request
                Paging cursor = new Paging(1, 200);

                for (int i = 1; i <= limit; i++) {
                    // Get a batch of the tweets from the timeline
                    ResponseList<twitter4j.Status> statuses = sTwitter.getHomeTimeline(cursor);
                    statusesCount = statuses.toArray().length;
                    totalStatusesCount += statusesCount;

                    // Set the last tweet ID as the sinceId to continue cursoring
                    sinceId = statuses.get(statusesCount - 1).getId();
                    cursor.setSinceId(sinceId);

                    // Store all the tweets in the database
                    for (twitter4j.Status status : statuses) {
                        mDbHelper.insertStatus(mDbWrite, status);
                    }

                    // Log the amount of tweets stored in the database
                    Log.d(LOG_KEY, "Stored x" + String.valueOf(totalStatusesCount) + " tweets.");
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Swap the cursor to update the view with new data, hide the progress and re-display
         * the timeline
         *
         * @param param Void parameter
         */
        @Override
        protected void onPostExecute(Void param) {
            // Fetch the new tweets, and swap the cursor
            Cursor newStatuses = mDbHelper.getFavouriteStatuses(mDbRead, mUserId);

            // If there are no results, fetch all tweets
            if (newStatuses.getCount() == 0) {
                newStatuses = mDbHelper.getStatuses(mDbRead, mUserId);
            }

            mTimelineAdapter.swapCursor(newStatuses);
            mTimelineProgress.setVisibility(View.GONE);
            mTimelineView.setVisibility(View.VISIBLE);
        }
    }
}
