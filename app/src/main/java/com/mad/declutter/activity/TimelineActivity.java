package com.mad.declutter.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mad.declutter.R;
import com.mad.declutter.adapter.FriendAdapter;
import com.mad.declutter.db.DatabaseHelper;
import com.mad.declutter.helpers.TwitterHelper;
import com.mad.declutter.model.Session;

import java.util.ArrayList;

import twitter4j.User;

public class TimelineActivity extends AppCompatActivity implements View.OnClickListener {
    private SQLiteDatabase mDbRead;
    private DatabaseHelper mDbHelper;
    private RecyclerView mRecyclerView;
    private FriendAdapter mFriendAdapter;
    private TwitterHelper mTwitterHelper;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mSession = new Session(getApplicationContext());
        mTwitterHelper = new TwitterHelper(getApplicationContext());

        // Get database helper instance
        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());
        mDbRead = mDbHelper.getReadableDatabase();

        ArrayList<User> friendsList = new ArrayList<>(10);

        // Create an instance of TrainAdapter with the trains ArrayList
        // and set the RefreshTrainListener
        mFriendAdapter = new FriendAdapter(this, friendsList);

        // Get the recyclerView and set its parameters including passing trainAdapter
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mFriendAdapter);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.twitterFriendsBtn:
                mTwitterHelper.new FetchTwitterFriends(mSession.getUserId()).execute();
                break;

            case R.id.twitterTimelineBtn:
                mTwitterHelper.new FetchTwitterFriends(mSession.getUserId()).execute();
                break;
        }
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
