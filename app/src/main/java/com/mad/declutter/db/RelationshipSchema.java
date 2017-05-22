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

import twitter4j.User;

final class RelationshipSchema {
    private RelationshipSchema() {}

    static final String TABLE_NAME = "twitter_relationships";

    static final String COLUMN_ID = "id";
    static final String COLUMN_FOLLOWS = "follows";
    static final String COLUMN_FAVOURITE = "is_favourite";
    static final String COLUMN_USER_ID = "user_id";
    static final String COLUMN_TARGET_USER_ID = "target_user_id";
    static final String COLUMN_updated_at = "updated_at";

    static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER NOT NULL," +
            COLUMN_FOLLOWS + " NUMERIC NOT NULL," +
            COLUMN_FAVOURITE + " NUMERIC NOT NULL," +
            COLUMN_USER_ID + " INTEGER NOT NULL," +
            COLUMN_TARGET_USER_ID + " INTEGER NOT NULL," +
            COLUMN_updated_at + " NUMERIC NOT NULL," +
            "PRIMARY KEY (" + COLUMN_ID + ")," +
            "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " +
                UserSchema.TABLE_NAME + "(" + UserSchema.COLUMN_ID + ")," +
            "FOREIGN KEY (" + COLUMN_TARGET_USER_ID + ") REFERENCES " +
                UserSchema.TABLE_NAME + "(" + UserSchema.COLUMN_ID + "))";

    static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
