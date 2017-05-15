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

final class TwitterUserSchema {
    private TwitterUserSchema() {}

    private static final String TABLE_NAME = "twitter_users";
    private static final String ID = "id";
    private static final String SCREEN_NAME = "screen_name";
    private static final String PROFILE_IMAGE = "profile_image_url_https";
    private static final String DESCRIPTION = "description";
    private static final String CREATED_AT = "created_at";
    private static final String NAME = "name";
    private static final String STATUSES_COUNT = "statuses_count";
    private static final String FOLLOWERS_COUNT = "followers_count";
    private static final String FRIENDS_COUNT = "friends_count";

    static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            "id INTEGER NOT NULL," +
            "screen_name INTEGER NOT NULL," +
            "profile_image_url_https TEXT NOT NULL," +
            "description TEXT NOT NULL," +
            "created_at NUMERIC NOT NULL," +
            "name TEXT NOT NULL," +
            "statuses_count INTEGER NOT NULL," +
            "followers_count INTEGER NOT NULL," +
            "friends_count INTEGER NOT NULL," +
            "updated_at NUMERIC NOT NULL," +
            "PRIMARY KEY (id_str)," +
            "UNIQUE (screen_name));";

    static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
