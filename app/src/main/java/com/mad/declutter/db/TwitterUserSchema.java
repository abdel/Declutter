package com.mad.declutter.db;

final class TwitterUserSchema {
    private TwitterUserSchema() {}

    private static final String TABLE_NAME = "twitter_users";

    static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            "id_str VARCHAR(50) NOT NULL," +
            "screen_name INT NOT NULL," +
            "profile_image_url_https INT NOT NULL," +
            "description INT NOT NULL," +
            "created_at INT NOT NULL," +
            "name INT NOT NULL," +
            "statuses_count INT NOT NULL," +
            "followers_count INT NOT NULL," +
            "friends_count INT NOT NULL," +
            "updated_at INT NOT NULL," +
            "PRIMARY KEY (id_str)," +
            "UNIQUE (screen_name));";

    static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
