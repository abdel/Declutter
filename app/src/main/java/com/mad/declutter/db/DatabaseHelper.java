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

package com.mad.declutter.db;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mad.declutter.model.Relationship;

import twitter4j.Status;
import twitter4j.User;

/**
 *
 * @author Abdelrahman Ahmed
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper sInstance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "twitter_db";

    /**
     * This method returns an instance of the database helper
     *
     * @param context Context
     * @return sInstance DatabaseHelper
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * The onCreate method is called when the database is created to create any tables, indexes
     * or insert some seed data to the database.
     *
     * @param db SQLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserSchema.SQL_CREATE_TABLE);
        db.execSQL(StatusSchema.SQL_CREATE_TABLE);
        db.execSQL(RelationshipSchema.SQL_CREATE_TABLE);
    }

    /**
     * The onUpgrade method is used when there are changes to the schema by dropping the current
     * tables in the database, and calls onCreate to re-insert the updated table schema.
     *
     * @param db SQLiteDatabase
     * @param oldVersion int The old version of the database
     * @param newVersion int The new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UserSchema.SQL_DELETE_TABLE);
        db.execSQL(StatusSchema.SQL_DELETE_TABLE);
        db.execSQL(RelationshipSchema.SQL_DELETE_TABLE);
        onCreate(db);
    }

    /**
     * The onDowngrade method
     *
     * @param db SQLiteDatabase
     * @param oldVersion int The old version of the database
     * @param newVersion int The new version of the database
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insertUser(SQLiteDatabase db, User user) {
        ContentValues values = new ContentValues();
        values.put(UserSchema.COLUMN_ID, user.getId());
        values.put(UserSchema.COLUMN_NAME, user.getName());
        values.put(UserSchema.COLUMN_SCREEN_NAME, user.getScreenName());
        values.put(UserSchema.COLUMN_DESCRIPTION, user.getDescription());
        values.put(UserSchema.COLUMN_PROFILE_IMAGE, user.getProfileImageURLHttps());
        values.put(UserSchema.COLUMN_FRIENDS_COUNT, user.getFriendsCount());
        values.put(UserSchema.COLUMN_FOLLOWERS_COUNT, user.getFollowersCount());
        values.put(UserSchema.COLUMN_STATUSES_COUNT, user.getStatusesCount());
        values.put(UserSchema.COLUMN_CREATED_AT, user.getCreatedAt().toString());

        db.insertWithOnConflict(UserSchema.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void insertRelationship(SQLiteDatabase db, Relationship relationship) {
        ContentValues values = new ContentValues();
        values.put(RelationshipSchema.COLUMN_USER_ID, relationship.getUserId());
        values.put(RelationshipSchema.COLUMN_TARGET_USER_ID, relationship.getTargetUserId());
        values.put(RelationshipSchema.COLUMN_FOLLOWS, relationship.isFollows());
        values.put(RelationshipSchema.COLUMN_updated_at, relationship.getUpdatedAt());

        db.insertWithOnConflict(RelationshipSchema.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void insertStatus(SQLiteDatabase db, Status status) {
        ContentValues values = new ContentValues();
        values.put(StatusSchema.COLUMN_ID, status.getId());
        values.put(StatusSchema.COLUMN_CREATED_AT, status.getCreatedAt().toString());
        values.put(StatusSchema.COLUMN_LANG, status.getLang());
        values.put(StatusSchema.COLUMN_FAVOURITE_COUNT, status.getFavoriteCount());
        values.put(StatusSchema.COLUMN_RETWEET_COUNT, status.getRetweetCount());
        values.put(StatusSchema.COLUMN_RETWEET, (status.getRetweetedStatus() != null));
        values.put(StatusSchema.COLUMN_REPLY_SCREEN_NAME, status.getInReplyToScreenName());
        values.put(StatusSchema.COLUMN_REPLY_USER_ID, status.getInReplyToUserId());
        values.put(StatusSchema.COLUMN_REPLY_STATUS_ID, status.getInReplyToStatusId());
        values.put(StatusSchema.COLUMN_TEXT, status.getText());
        values.put(StatusSchema.COLUMN_SOURCE, status.getSource());
        values.put(StatusSchema.COLUMN_USER_ID, status.getUser().getId());

        db.insertWithOnConflict(StatusSchema.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Cursor getFriends(SQLiteDatabase db, long currentUserId) {
        String userTable = UserSchema.TABLE_NAME;
        String relTable = RelationshipSchema.TABLE_NAME;


        String userId = userTable + "." + UserSchema.COLUMN_ID;
        String screenName = userTable + "." + UserSchema.COLUMN_SCREEN_NAME;
        String relUserId = relTable + "." + RelationshipSchema.COLUMN_USER_ID;
        String relTargetId = relTable + "." + RelationshipSchema.COLUMN_TARGET_USER_ID;

        return db.rawQuery("SELECT " + userId + ", " + screenName +
                " FROM " + userTable +
                " JOIN " + relTable + "" +
                " ON " + userId + " = " + relTargetId +
                " WHERE " + relUserId + " = ?" +
                " LIMIT 100", new String[] { String.valueOf(currentUserId) });
    }
}
