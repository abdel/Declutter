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

    static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            "id_str TEXT NOT NULL," +
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
