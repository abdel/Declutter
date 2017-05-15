package com.mad.declutter.db;

import twitter4j.User;

final class StatusSchema {
    private StatusSchema() {}

    static final String TABLE_NAME = "twitter_statuses";

    static final String COLUMN_ID = "id";
    static final String COLUMN_CREATED_AT = "created_at";
    static final String COLUMN_LANG = "lang";
    static final String COLUMN_FAVOURITE_COUNT = "favourite_count";
    static final String COLUMN_RETWEET_COUNT = "retweet_count";
    static final String COLUMN_RETWEET = "is_retweet";
    static final String COLUMN_REPLY_SCREEN_NAME = "in_reply_to_screen_name";
    static final String COLUMN_REPLY_USER_ID = "in_reply_to_user_id_str";
    static final String COLUMN_REPLY_STATUS_ID = "in_reply_to_status_id_str";
    static final String COLUMN_TEXT = "text";
    static final String COLUMN_SOURCE = "source";
    static final String COLUMN_USER_ID = "user_id";

    static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER NOT NULL," +
            COLUMN_CREATED_AT + " NUMERIC NOT NULL," +
            COLUMN_LANG + " TEXT NOT NULL," +
            COLUMN_FAVOURITE_COUNT + " INTEGER NOT NULL," +
            COLUMN_RETWEET_COUNT + " INTEGER NOT NULL," +
            COLUMN_RETWEET + " NUMERIC NOT NULL," +
            COLUMN_REPLY_SCREEN_NAME + " TEXT NOT NULL," +
            COLUMN_REPLY_USER_ID + " INTEGER NOT NULL," +
            COLUMN_REPLY_STATUS_ID + " INTEGER NOT NULL," +
            COLUMN_TEXT + " TEXT NOT NULL," +
            COLUMN_SOURCE + " TEXT NOT NULL," +
            COLUMN_USER_ID + " INTEGER NOT NULL," +
            "PRIMARY KEY (" + COLUMN_ID + ")," +
            "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + UserSchema.TABLE_NAME + "(" + UserSchema.COLUMN_ID + "));";

    static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
