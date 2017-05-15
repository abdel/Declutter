package com.mad.declutter.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mad.declutter.R;
import com.mad.declutter.adapter.FriendAdapter;
import com.mad.declutter.db.DatabaseHelper;

import java.util.ArrayList;

import twitter4j.User;

/**
 * Mobile Application Development - Exercise 6
 *
 * @author Abdelrahman Ahmed (Abdel)
 */
public class FavouritesActivity extends AppCompatActivity {
    private SQLiteDatabase mDbRead;
    private DatabaseHelper mDbHelper;
    private RecyclerView mRecyclerView;
    private FriendAdapter mFriendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

    }

}

