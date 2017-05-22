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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mad.declutter.R;
import com.mad.declutter.adapter.FriendAdapter;
import com.mad.declutter.adapter.SimpleCursorRecyclerAdapter;
import com.mad.declutter.db.DatabaseHelper;
import com.mad.declutter.db.UserSchema;
import com.mad.declutter.helpers.TwitterHelper;
import com.mad.declutter.helpers.ClickListener;
import com.mad.declutter.helpers.RecyclerTouchListener;
import com.mad.declutter.model.Session;

import twitter4j.User;

/**
 * Mobile Application Development - Exercise 6
 *
 * @author Abdelrahman Ahmed (Abdel)
 */
public class FriendsActivity extends AppCompatActivity {
    private Session mSession;
    private SQLiteDatabase mDbRead;
    private TwitterHelper mTwitterHelper;
    private DatabaseHelper mDbHelper;
    private RecyclerView mRecyclerView;
    private FriendAdapter mFriendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Toolbar configuration
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setTitle("Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get database helper instance
        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());
        mDbRead = mDbHelper.getReadableDatabase();

        mSession = new Session(getApplicationContext());
        mTwitterHelper = new TwitterHelper(getApplicationContext());
        mTwitterHelper.setAccessToken(mSession.getAccessToken());

        Cursor friends = mDbHelper.getFriends(mDbRead, mSession.getUserId());
        mFriendAdapter = new FriendAdapter(friends);

        // Get the recyclerView and set its parameters including passing friendAdapter
        mRecyclerView = (RecyclerView) findViewById(R.id.friendsRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mFriendAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                Toast.makeText(FriendsActivity.this, "Single Click on position: " + position,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(FriendsActivity.this, "Long press on position: " + position,
                        Toast.LENGTH_LONG).show();
            }
        }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_secondary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_refresh:
                mTwitterHelper.new FetchTwitterFriends(mSession.getUserId()).execute();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}

