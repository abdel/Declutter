package com.mad.declutter.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mad.declutter.R;
import com.mad.declutter.db.DatabaseHelper;
import com.mad.declutter.adapter.FriendAdapter;

import twitter4j.User;
import java.util.ArrayList;

/**
 * Mobile Application Development - Exercise 6
 *
 * @author Abdelrahman Ahmed (Abdel)
 */
public class FriendsActivity extends AppCompatActivity {
    private SQLiteDatabase mDbRead;
    private DatabaseHelper mDbHelper;
    private RecyclerView mRecyclerView;
    private FriendAdapter mFriendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // Get database helper instance
        mDbHelper = DatabaseHelper.getInstance(getApplicationContext());
        mDbRead = mDbHelper.getReadableDatabase();

        ArrayList<User> friendsList = new ArrayList<>(10);

        // Create an instance of TrainAdapter with the trains ArrayList
        // and set the RefreshTrainListener
        mFriendAdapter = new FriendAdapter(this, friendsList);

        // Get the recyclerView and set its parameters including passing friendAdapter
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mFriendAdapter);
    }
}

