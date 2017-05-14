package com.mad.declutter.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ProgressDialog;
import android.widget.TextView;
import android.widget.Button;
import android.util.Log;
import android.net.Uri;
import android.os.AsyncTask;
import android.content.Intent;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.content.pm.ActivityInfo;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.mad.declutter.R;
import com.mad.declutter.helpers.AlertDialogHelper;
import com.mad.declutter.helpers.ConnectionHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Twitter API Instances
    private static Twitter sTwitter;
    private static RequestToken sRequestToken;

    // Twitter Configuration Keys
    private static final String TWITTER_CALLBACK_URL = "oauth://oob";
    private static final String TWITTER_CONSUMER_KEY = "jXYoqycRtKniFhstPOKcnFfRA";
    private static final String TWITTER_CONSUMER_SECRET = "2nuiZbHL3HPwT46qKjMsCart3moo7XLJa400XiSk5JZabnJsUl";

    // Twitter OAuth API Endpoints
    private static final String URL_TWITTER_AUTH = "auth_url";
    private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    private static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

    // Preference Constants
    public static final String PREF_NAME = "declutter_twitter";
    public static final String PREF_OAUTH_TOKEN = "oauth_token";
    public static final String PREF_OAUTH_SECRET = "oauth_token_secret";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_SCREEN_NAME = "screen_name";
    public static final String PREF_AUTHENTICATED = "isAuthenticated";

    private static final String LOG_KEY = "DECLUTTER";

    // Widgets
    Button mTwitterLoginBtn;
    TextView mTwitterUsername;
    ProgressBar mProgressBar;

    // Shared Preferences
    private static SharedPreferences mSharedPreferences;

    // Alert Dialog Helper
    private AlertDialogHelper mAlert = new AlertDialogHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Shared Preferences
        ConnectionHelper connection = new ConnectionHelper(getApplicationContext());

        // Check if an Internet connection is available
        if (!connection.isNetworkAvailable()) {
            // Internet Connection is not present
            mAlert.showAlertDialog(MainActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }

        // Check if the Twitter consumer key and secret are set
        if (TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0) {
            // Internet Connection is not present
            mAlert.showAlertDialog(MainActivity.this, "Twitter oAuth tokens", "Please set your twitter oauth tokens first!", false);
            // stop executing code by return
            return;
        }

        // UI Elements
        mTwitterLoginBtn = (Button) findViewById(R.id.twitterLoginBtn);
        mTwitterUsername = (TextView) findViewById(R.id.twitterUsername);
        mProgressBar = (ProgressBar) findViewById(R.id.mainProgressBar);

        // Shared Preferences
        mSharedPreferences = getApplicationContext().getSharedPreferences(PREF_NAME, 0);

        if (!isTwitterAuthenticated()) {
            Uri uri = getIntent().getData();
            new TwitterCallbackHandler().execute(uri);
        } else {
            String screen_name = mSharedPreferences.getString(PREF_SCREEN_NAME, "");
            mTwitterUsername.setText(screen_name);

            mTwitterLoginBtn.setVisibility(View.GONE);
            mTwitterUsername.setVisibility(View.VISIBLE);
            mTwitterUsername.setText(getString(R.string.welcome_user, screen_name));
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.twitterLoginBtn:
                new TwitterLoginHandler(isTwitterAuthenticated()).execute();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_refresh:
                return true;

            case R.id.action_settings:
                return true;

            case R.id.action_logout:
                twitterLogout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Check user already logged in your application using twitter Login flag is
     * fetched from Shared Preferences
     *
     * @return boolean Whether the user is currently logged into Twitter
     */
    private boolean isTwitterAuthenticated() {
        return mSharedPreferences.getBoolean(PREF_AUTHENTICATED, false);
    }

    /**
     *
     */
    private void twitterLogout() {
        // Clear the shared preferences
        Editor e = mSharedPreferences.edit();
        e.remove(PREF_OAUTH_TOKEN);
        e.remove(PREF_OAUTH_SECRET);
        e.remove(PREF_AUTHENTICATED);
        e.apply();

        mTwitterUsername.setVisibility(View.GONE);
        mTwitterUsername.setText("");

        mTwitterLoginBtn.setVisibility(View.VISIBLE);
    }

    private class TwitterLoginHandler extends AsyncTask<Void, Void, Void> {
        private boolean mAuthenticated;

        private TwitterLoginHandler(boolean authenticated) {
            this.mAuthenticated = authenticated;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressBar.setVisibility(View.VISIBLE);
            mTwitterLoginBtn.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mAuthenticated) return null;

            ConfigurationBuilder configBuilder = new ConfigurationBuilder();
            configBuilder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            configBuilder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

            Configuration configuration = configBuilder.build();
            TwitterFactory factory = new TwitterFactory(configuration);
            sTwitter = factory.getInstance();

            try {
                sRequestToken = sTwitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                MainActivity.this.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(sRequestToken.getAuthenticationURL())));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            if (mAuthenticated) {
                Toast.makeText(getApplicationContext(), "You've already authenticated with Twitter!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private class TwitterCallbackHandler extends AsyncTask<Uri, Void, AccessToken> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressBar.setVisibility(View.VISIBLE);
            mTwitterLoginBtn.setVisibility(View.GONE);
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
            String screen_name = accessToken.getScreenName();

            Editor e = mSharedPreferences.edit();
            e.putBoolean(PREF_AUTHENTICATED, true);
            e.putString(PREF_SCREEN_NAME, screen_name);
            e.putLong(PREF_USER_ID, accessToken.getUserId());
            e.putString(PREF_OAUTH_TOKEN, accessToken.getToken());
            e.putString(PREF_OAUTH_SECRET, accessToken.getTokenSecret());
            e.apply();

            mProgressBar.setVisibility(View.GONE);
            mTwitterLoginBtn.setVisibility(View.GONE);
            mTwitterUsername.setVisibility(View.VISIBLE);

            // Display the user's screen name
            mTwitterUsername.setText(getString(R.string.welcome_user, screen_name));
        }
    }
}
