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
import com.mad.declutter.model.Session;
import com.mad.declutter.db.DatabaseHelper;
import com.mad.declutter.adapter.FriendAdapter;
import com.mad.declutter.helpers.TwitterHelper;

/**
 * The FriendsActivity allows the user to fetch their friends from Twitter and browse through them.
 * The user can select the friends and they get marked as favourites. The activity uses the recycler view
 * and Friend Adapter to display the friend data.
 *
 * @author Abdelrahman Ahmed
 */
public class FriendsActivity extends AppCompatActivity {
    private Session mSession;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private TwitterHelper mTwitterHelper;
    private FriendAdapter mFriendAdapter;
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
        setContentView(R.layout.activity_friends);

        // Toolbar configuration
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setTitle("Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get database helper instance
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
        SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();

        mSession = new Session(getApplicationContext());
        mTwitterHelper = new TwitterHelper(getApplicationContext());
        mTwitterHelper.setAccessToken(mSession.getAccessToken());

        // Fetch friends for the logged in user
        Cursor friends = dbHelper.getFriends(dbRead, mSession.getUserId());
        mFriendAdapter = new FriendAdapter(this, getApplicationContext(), mSession.getUserId(), friends);

        // Get progress bar
        mProgressBar = (ProgressBar) findViewById(R.id.friendsProgress);

        // Get the recyclerView and set its parameters including passing friendAdapter
        mRecyclerView = (RecyclerView) findViewById(R.id.friendsView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mFriendAdapter);

        // Get the empty data view
        mEmptyView = (TextView) findViewById(R.id.empty_view);

        // Display empty view if cursor is empty
        if (friends.getCount() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * onResume is a lifecycle method that is executed when the activity is resumed. It's used to
     * re-fetch the friends from the database when the user returns to the activity.
     */
    @Override
    protected void onResume() {
        super.onResume();

        mFriendAdapter.refreshFriendsData();
    }

    /**
     * Allows the user to navigate back to the Timeline activity
     *
     * @return boolean
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
        getMenuInflater().inflate(R.menu.menu_secondary, menu);
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
            // Execute an async task to fetch friends from Twitter
            case R.id.action_refresh:
                mTwitterHelper.new FetchTwitterFriends(
                        mSession.getUserId(),
                        mFriendAdapter,
                        mRecyclerView,
                        mProgressBar,
                        mEmptyView
                ).execute();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

