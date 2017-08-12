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

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mad.declutter.R;
import com.mad.declutter.adapter.TimelineAdapter;
import com.mad.declutter.db.DatabaseHelper;
import com.mad.declutter.helpers.TwitterHelper;
import com.mad.declutter.model.Session;

/**
 * The TimelineActivity is main activity that the user interacts with, and provides access to
 * other activities, such as Favourites and Friends via the menu. The activity uses the recycler view
 * and the Timeline adapter to display the timeline data.
 *
 * @author Abdelrahman Ahmed
 */
public class TimelineActivity extends AppCompatActivity {
    private SQLiteDatabase mDbRead;
    private DatabaseHelper mDbHelper;
    private RecyclerView mRecyclerView;
    private TwitterHelper mTwitterHelper;
    private TimelineAdapter mTimelineAdapter;
    private ProgressBar mProgressBar;
    private Session mSession;
    private TextView mEmptyView;

    /**
     * onCreate is a lifecycle method that gets called when the activity is created. This method
     * initialises all the necessary helpers, adapters and the recycler view. It also fetches the
     * data from the database for the adapter.
     *
     * @param savedInstanceState The previously saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the session and initialise the Twitter helper
        mSession = new Session(getApplicationContext());
        mTwitterHelper = new TwitterHelper(getApplicationContext());
        mTwitterHelper.setAccessToken(mSession.getAccessToken());

        // Get database helper instance
        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());
        mDbRead = mDbHelper.getReadableDatabase();

        // Get tweets from the favourite friends
        Cursor statuses = getTimeline();

        // Initialise the Timeline Adapter
        mTimelineAdapter = new TimelineAdapter(statuses);

        // Get the progress bar view
        mProgressBar = (ProgressBar) findViewById(R.id.timelineProgress);

        // Get the recyclerView and set its parameters including passing trainAdapter
        mRecyclerView = (RecyclerView) findViewById(R.id.timelineView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mTimelineAdapter);
        mRecyclerView.setHasFixedSize(true);

        // Get the empty data view
        mEmptyView = (TextView) findViewById(R.id.empty_view);

        // Display empty view if tweets cursor is empty
        if (statuses.getCount() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * onResume is a lifecycle method that is executed when the activity is resumed. It's used to
     * re-fetch the timeline when the user returns to the activity.
     */
    @Override
    protected void onResume() {
        super.onResume();

        Cursor statuses = getTimeline();
        mTimelineAdapter.swapCursor(statuses);
    }

    /**
     * Creates the activity menu from the specified layout
     *
     * @param menu The activity menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handles the on select event for the menu options
     *
     * @param item The selected menu item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            // Execute async task to fetch timeline
            case R.id.action_refresh:
                mTwitterHelper.new FetchHomeTimeline(
                        mSession.getUserId(),
                        mTimelineAdapter,
                        mRecyclerView,
                        mProgressBar,
                        mEmptyView
                ).execute();
                break;

            // Navigate to the Friends Activity
            case R.id.action_friends:
                startActivity(new Intent(TimelineActivity.this, FriendsActivity.class));
                break;

            // Navigate to the Favourites Activity
            case R.id.action_favourites:
                startActivity(new Intent(TimelineActivity.this, FavouritesActivity.class));
                break;

            // Destroy the session to logout the user
            case R.id.action_logout:
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Fetches the user's filtered timeline to show only favourites, and attempts to fetch
     * the full timeline if the user has no favourites.
     *
     * @return Cursor containing the tweets
     */
    private Cursor getTimeline() {
        Cursor statuses = mDbHelper.getFavouriteStatuses(mDbRead, mSession.getUserId());

        // If there are no results, fetch all tweets
        if (statuses.getCount() == 0) {
            statuses = mDbHelper.getStatuses(mDbRead, mSession.getUserId());
        }

        return statuses;
    }

    /**
     * Clears the data from Shared Preferences, and redirects the user back to MainActivity
     */
    private void logout() {
        // Clear the user session
        mSession.clear();
        mSession = null;

        // Redirect the user back to MainActivity
        startActivity(new Intent(TimelineActivity.this, MainActivity.class));
        finish();
    }
}
