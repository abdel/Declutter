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
 * The UserSchema class defines the schema for the Twitter Users table in the SQLite3
 * database. It contains references to the column names and SQL queries for creating and dropping
 * the table from the database.
 *
 * @author Abdelrahman Ahmed
 */
public final class UserSchema {
    private UserSchema() {}

    // Table name
    static final String TABLE_NAME = "twitter_users";

    // Table column fields
    static final String COLUMN_ID = "id";
    static final String COLUMN_NAME = "name";
    public static final String COLUMN_SCREEN_NAME = "screen_name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PROFILE_IMAGE = "profile_image_url_https";
    static final String COLUMN_STATUSES_COUNT = "statuses_count";
    static final String COLUMN_FOLLOWERS_COUNT = "followers_count";
    static final String COLUMN_FRIENDS_COUNT = "friends_count";
    static final String COLUMN_CREATED_AT = "created_at";

    // The SQL query for creating the table
    static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER NOT NULL," +
            COLUMN_NAME + " TEXT NOT NULL," +
            COLUMN_SCREEN_NAME + " TEXT NOT NULL," +
            COLUMN_DESCRIPTION + " TEXT NOT NULL," +
            COLUMN_PROFILE_IMAGE + " TEXT NOT NULL," +
            COLUMN_FRIENDS_COUNT + " INTEGER NOT NULL," +
            COLUMN_FOLLOWERS_COUNT + " INTEGER NOT NULL," +
            COLUMN_STATUSES_COUNT + " INTEGER NOT NULL," +
            COLUMN_CREATED_AT + " NUMERIC NOT NULL," +
            "PRIMARY KEY (" + COLUMN_ID + ")," +
            "UNIQUE (" + COLUMN_SCREEN_NAME + "));";

    // The SQL query for dropping the table
    static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
