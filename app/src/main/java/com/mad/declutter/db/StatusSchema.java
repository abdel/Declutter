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

/**
 * The StatusSchema class defines the schema for the Twitter Statuses table in the SQLite3
 * database. It contains references to the column names and SQL queries for creating and dropping
 * the table from the database.
 *
 * @author Abdelrahman Ahmed
 */
public final class StatusSchema {
    private StatusSchema() {}

    // Table name
    static final String TABLE_NAME = "twitter_statuses";

    // Table column names
    static final String COLUMN_ID = "id";
    static final String COLUMN_CREATED_AT = "created_at";
    static final String COLUMN_LANG = "lang";
    static final String COLUMN_FAVOURITE_COUNT = "favourite_count";
    static final String COLUMN_RETWEET_COUNT = "retweet_count";
    static final String COLUMN_RETWEET = "is_retweet";
    static final String COLUMN_REPLY_SCREEN_NAME = "in_reply_to_screen_name";
    static final String COLUMN_REPLY_USER_ID = "in_reply_to_user_id_str";
    static final String COLUMN_REPLY_STATUS_ID = "in_reply_to_status_id_str";
    public static final String COLUMN_TEXT = "text";
    static final String COLUMN_SOURCE = "source";
    static final String COLUMN_USER_ID = "user_id";

    // SQL query for creating the table
    static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER NOT NULL," +
            COLUMN_CREATED_AT + " NUMERIC NOT NULL," +
            COLUMN_LANG + " TEXT NULL," +
            COLUMN_FAVOURITE_COUNT + " INTEGER NOT NULL," +
            COLUMN_RETWEET_COUNT + " INTEGER NOT NULL," +
            COLUMN_RETWEET + " INTEGER NOT NULL," +
            COLUMN_REPLY_SCREEN_NAME + " TEXT NULL," +
            COLUMN_REPLY_USER_ID + " INTEGER NULL," +
            COLUMN_REPLY_STATUS_ID + " INTEGER NULL," +
            COLUMN_TEXT + " TEXT NOT NULL," +
            COLUMN_SOURCE + " TEXT NOT NULL," +
            COLUMN_USER_ID + " INTEGER NOT NULL," +
            "PRIMARY KEY (" + COLUMN_ID + ")," +
            "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + UserSchema.TABLE_NAME + "(" + UserSchema.COLUMN_ID + "));";

    // SQL query for dropping the table
    static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
