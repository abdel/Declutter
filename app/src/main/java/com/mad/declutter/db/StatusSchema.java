package com.mad.declutter.db;

final class TwitterStatusSchema {
    private TwitterStatusSchema() {}

    private static final String TABLE_NAME = "twitter_statuses";

    static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            "id INTEGER NOT NULL," +
            "created_at NUMERIC NOT NULL," +
            "lang TEXT NOT NULL," +
            "favourite_count INTEGER NOT NULL," +
            "retweet_count INTEGER NOT NULL," +
            "is_retweet NUMERIC NOT NULL," +
            "in_reply_to_screen_name TEXT NOT NULL," +
            "in_reply_to_user_id_str INTEGER NOT NULL," +
            "in_reply_to_status_id_str INTEGER NOT NULL," +
            "text TEXT NOT NULL," +
            "source TEXT NOT NULL," +
            "user_id INTEGER NOT NULL," +
            "PRIMARY KEY (id_str)," +
            "FOREIGN KEY (user_id_str) REFERENCES twitter_users(id_str));";

    static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
