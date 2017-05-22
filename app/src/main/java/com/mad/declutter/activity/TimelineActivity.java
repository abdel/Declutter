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

import com.mad.declutter.R;
import com.mad.declutter.adapter.TimelineAdapter;
import com.mad.declutter.db.DatabaseHelper;
import com.mad.declutter.helpers.TwitterHelper;
import com.mad.declutter.model.Session;

public class TimelineActivity extends AppCompatActivity {
    private SQLiteDatabase mDbRead;
    private DatabaseHelper mDbHelper;
    private RecyclerView mRecyclerView;
    private TwitterHelper mTwitterHelper;
    private TimelineAdapter mTimelineAdapter;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSession = new Session(getApplicationContext());
        mTwitterHelper = new TwitterHelper(getApplicationContext());
        mTwitterHelper.setAccessToken(mSession.getAccessToken());

        // Get database helper instance
        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());
        mDbRead = mDbHelper.getReadableDatabase();

        Cursor statuses = mDbHelper.getStatuses(mDbRead);
        mTimelineAdapter = new TimelineAdapter(statuses);

        // Get the recyclerView and set its parameters including passing trainAdapter
        mRecyclerView = (RecyclerView) findViewById(R.id.timelineView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mTimelineAdapter);
        mRecyclerView.setHasFixedSize(true);
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
                mTwitterHelper.new FetchHomeTimeline(mSession.getUserId()).execute();
                break;

            case R.id.action_friends:
                Intent friendsIntent = new Intent(TimelineActivity.this, FriendsActivity.class);
                startActivity(friendsIntent);
                break;

            case R.id.action_favourites:
                Intent favouritesIntent = new Intent(TimelineActivity.this, FavouritesActivity.class);
                startActivity(favouritesIntent);
                break;

            case R.id.action_logout:
                destroySession();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void destroySession() {
        mSession.clear();
        mSession = null;
    }
}
