package com.mad.declutter.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;
import android.net.Uri;
import android.widget.ProgressBar;
import android.content.pm.ActivityInfo;
import android.content.SharedPreferences;

import twitter4j.Twitter;
import twitter4j.auth.RequestToken;

import com.mad.declutter.R;
import com.mad.declutter.helpers.AlertDialogHelper;
import com.mad.declutter.helpers.ConnectionHelper;
import com.mad.declutter.db.DatabaseHelper;
import com.mad.declutter.helpers.TwitterHelper;
import com.mad.declutter.model.Session;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Widgets
    Button mTwitterLoginBtn;
    TextView mTwitterUsername;
    ProgressBar mProgressBar;

    // Database
    private SQLiteDatabase mDbWrite;
    private DatabaseHelper mDbHelper;

    private TwitterHelper mTwitterHelper;

    // Twitter API Instances
    private static Twitter sTwitter;
    private static RequestToken sRequestToken;

    // Shared Preferences
    private static SharedPreferences sSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialise helpers
        AlertDialogHelper alertHelper = new AlertDialogHelper();
        ConnectionHelper networkHelper = new ConnectionHelper(getApplicationContext());

        // Check if an Internet connection is available
        if (!networkHelper.isNetworkAvailable()) {
            // Internet Connection is not present
            alertHelper.showAlertDialog(MainActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection",
                    false
            );

            return;
        }

        // Check if the Twitter consumer key and secret are set
        if (TwitterHelper.hasKeys()) {
            // Internet Connection is not present
            alertHelper.showAlertDialog(MainActivity.this,
                    "Twitter Application Keys",
                    "Please set your Twitter Consumer Key and Twitter Consumer Secret",
                    false
            );

            return;
        }

        mTwitterHelper = new TwitterHelper(getApplicationContext());

        // Get main UI elements
        mTwitterLoginBtn = (Button) findViewById(R.id.twitterLoginBtn);
        mTwitterUsername = (TextView) findViewById(R.id.twitterUsername);
        mProgressBar = (ProgressBar) findViewById(R.id.mainProgressBar);

        if (Session.hasAuthenticated(getApplicationContext())) {
            // Create a new Intent to TimelineActivity
            Intent intent = new Intent(MainActivity.this, TimelineActivity.class);
            startActivity(intent);
        }

        if (TwitterHelper.sRequestToken != null) {
            Uri uri = getIntent().getData();
            mTwitterHelper.new TwitterCallbackHandler(mProgressBar, mTwitterLoginBtn).execute(uri);
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.twitterLoginBtn:
                mTwitterHelper.new TwitterLoginHandler(
                        mProgressBar,
                        mTwitterLoginBtn,
                        Session.hasAuthenticated(getApplicationContext())
                ).execute();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
