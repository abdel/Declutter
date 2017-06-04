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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mad.declutter.R;
import com.mad.declutter.adapter.FriendAdapter;
import com.mad.declutter.db.DatabaseHelper;
import com.mad.declutter.helpers.TwitterHelper;
import com.mad.declutter.model.Session;

/**
 * The FavouritesActivity allows the user to view and manage the friends they marked as favourites.
 * The user can de-select any friend to remove them from the favourites list. The activity utilises
 * recycler view and the Friend adapter to display the favourites data.
 *
 * @author Abdelrahman Ahmed
 */
public class FavouritesActivity extends AppCompatActivity {
    FriendAdapter mFriendAdapter;

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
        setContentView(R.layout.activity_favourites);

        // Toolbar configuration
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setTitle("Favourites");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get database helper instance
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
        SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();

        // Initialise helper and get session
        Session session = new Session(getApplicationContext());
        TwitterHelper twitterHelper = new TwitterHelper(getApplicationContext());
        twitterHelper.setAccessToken(session.getAccessToken());

        // Fetch favourites for the logged in user
        Cursor favourites = dbHelper.getFavourites(dbRead, session.getUserId());
        mFriendAdapter = new FriendAdapter(this, getApplicationContext(), session.getUserId(), favourites);

        // Get the recyclerView and set its parameters including passing friendAdapter
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.favouritesView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mFriendAdapter);

        // Get the empty data view
        TextView emptyView = (TextView) findViewById(R.id.empty_view);

        // Show empty view if favourites are empty
        if (favourites.getCount() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * onResume is a lifecycle method that is executed when the activity is resumed. It's used to
     * re-fetch the favourites from the database when the user returns to the activity.
     */
    @Override
    protected void onResume() {
        super.onResume();

        mFriendAdapter.refreshFavouritesData();
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
}
