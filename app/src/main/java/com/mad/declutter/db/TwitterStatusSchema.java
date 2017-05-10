package com.mad.declutter.db;

final class TwitterStatusSchema {
    private TwitterStatusSchema() {}

    private static final String TABLE_NAME = "twitter_statuses";

    static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            "id_str VARCHAR(50) NOT NULL," +
            "created_at timestamp NOT NULL," +
            "lang VARCHAR(10) NOT NULL," +
            "favourite_count INT NOT NULL," +
            "retweet_count INT NOT NULL," +
            "is_retweet boolean NOT NULL," +
            "in_reply_to_screen_name VARCHAR(50) NOT NULL," +
            "in_reply_to_user_id_str VARCHAR(50) NOT NULL," +
            "in_reply_to_status_id_str VARCHAR(50) NOT NULL," +
            "text TEXT NOT NULL," +
            "source VARCHAR(150) NOT NULL," +
            "user_id_str INT NOT NULL," +
            "PRIMARY KEY (id_str)," +
            "FOREIGN KEY (user_id_str) REFERENCES twitter_users(id_str));";

    static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
