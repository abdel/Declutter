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

package com.mad.declutter.activity;

import android.net.Uri;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.ProgressBar;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;

import com.mad.declutter.R;
import com.mad.declutter.model.Session;
import com.mad.declutter.helpers.AppHelper;
import com.mad.declutter.helpers.TwitterHelper;

/**
 * The MainActivity is responsible for authenticating and diverting the user to the correct
 * location. The activity prompts the user to login to Twitter, and handles the login and callback
 * processes via async tasks. It then redirects the user to the Timeline activity if they are
 * successfully authenticated.
 *
 * @author Abdelrahman Ahmed
 */
public class MainActivity extends AppCompatActivity {
    private static final String LOG_KEY = "MainActivity";

    /**
     * onCreate is a lifecycle method that gets called when the activity is created. This method
     * initialises all the necessary helpers, and handles the login and callback process for Twitter.
     * It also diverts the user to the Timeline activity when successfully authenticated.
     *
     * @param savedInstanceState The previously saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialise the app helper
        AppHelper appHelper = new AppHelper(getApplicationContext());

        // Check if an Internet connection is available
        if (!appHelper.isNetworkAvailable()) {
            appHelper.showAlertDialog(MainActivity.this,
                    getString(R.string.dialog_network_title),
                    getString(R.string.dialog_network_message),
                    false
            );
            return;
        }

        // Check if the Twitter consumer key and secret are set
        if (TwitterHelper.hasKeys()) {
            // Log the error for application keys
            Log.e(LOG_KEY, "The consumer key and consumer secret are not set correctly");
            return;
        }

        // Initialise the Twitter Helper
        final TwitterHelper twitterHelper = new TwitterHelper(getApplicationContext());

        // Get main UI elements
        final Button twitterLoginBtn = (Button) findViewById(R.id.twitterLoginBtn);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.mainProgressBar);

        // Redirect authenticated users to the TimelineActivity
        if (Session.hasAuthenticated(getApplicationContext())) {
            startActivity(new Intent(MainActivity.this, TimelineActivity.class));
        }

        // Handle the callback after users login
        if (TwitterHelper.sRequestToken != null) {
            Uri uri = getIntent().getData();
            twitterHelper.new TwitterCallbackHandler(this, progressBar, twitterLoginBtn).execute(uri);
        }

        // An onClick Listener for the Twitter Login Button
        twitterLoginBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the click event for the Twitter Login button
             *
             * @param view The view that was clicked
             */
            @Override
            public void onClick(View view) {
                // Authenticate the user with Twitter
                twitterHelper.new TwitterLoginHandler(
                        progressBar,
                        twitterLoginBtn,
                        Session.hasAuthenticated(getApplicationContext())
                ).execute();
            }
        });
    }
}
