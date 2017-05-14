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

final class TwitterRelationshipSchema {
    private TwitterRelationshipSchema() {}

    private static final String TABLE_NAME = "twitter_relationships";

    static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            "id INT NOT NULL" +
            "follows BOOLEAN NOT NULL," +
            "user_id_str TEXT NOT NULL," +
            "target_id_str TEXT NOT NULL," +
            "updated_at timestamp NOT NULL," +
            "PRIMARY KEY (id)," +
            "FOREIGN KEY (user_id_str) REFERENCES twitter_users(id_str)," +
            "FOREIGN KEY (target_id_str) REFERENCES twitter_users(id_str)";

    static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
