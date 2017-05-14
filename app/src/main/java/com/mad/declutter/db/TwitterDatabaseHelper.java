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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * @author Abdelrahman Ahmed
 */
class TwitterDatabaseHelper extends SQLiteOpenHelper {

    private static TwitterDatabaseHelper sInstance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "twitter_db";

    /**
     * This method returns an instance of the database helper
     *
     * @param context Context
     * @return sInstance TwitterDatabaseHelper
     */
    public static synchronized TwitterDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TwitterDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private TwitterDatabaseHelper(Context context) {
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
        db.execSQL(TwitterUserSchema.SQL_CREATE_TABLE);
        db.execSQL(TwitterStatusSchema.SQL_CREATE_TABLE);
        db.execSQL(TwitterRelationshipSchema.SQL_CREATE_TABLE);
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
        db.execSQL(TwitterUserSchema.SQL_DELETE_TABLE);
        db.execSQL(TwitterStatusSchema.SQL_DELETE_TABLE);
        db.execSQL(TwitterRelationshipSchema.SQL_DELETE_TABLE);
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
}
