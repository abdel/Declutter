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

package com.mad.declutter.model;

/**
 * The Relationship class is a model of the data stored in the Twitter Relationships table in the
 * database. It contains details about the relationship between two Twitter users, including their
 * user IDs, their friend status and whether or not the application user marked them as a favourite.
 *
 * @author Abdelrahman
 */
public class Relationship {

    private long mUserId;
    private int mFollows;
    private int mFavourite;
    private long mUpdatedAt;
    private long mTargetUserId;

    /**
     * The constructor for the class which sets up attributes
     *
     * @param userId The user ID for the main user
     * @param targetUserId The user ID for the target user
     * @param follows Whether or not the main user follows the target user
     * @param favourite Whether or not the main user marked the target user as a favourite
     */
    public Relationship(long userId, long targetUserId, int follows, int favourite) {
        this.mUserId = userId;
        this.mFollows = follows;
        this.mFavourite = favourite;
        this.mTargetUserId = targetUserId;
        this.mUpdatedAt = System.currentTimeMillis();
    }

    /**
     * Gets the User id
     *
     * @return The user ID
     */
    public long getUserId() {
        return mUserId;
    }

    /**
     * Sets the user id
     *
     * @param userId The User Id
     */
    public void setUserId(long userId) {
        this.mUserId = userId;
    }

    /**
     * Gets the last time the relationship was updated
     *
     * @return The relationship update time
     */
    public long getUpdatedAt() {
        return mUpdatedAt;
    }

    /**
     * Sets the updated time for the relationship
     *
     * @param updatedAt The relationship update time
     */
    public void setUpdatedAt(long updatedAt) {
        this.mUpdatedAt = updatedAt;
    }

    /**
     * Gets the follows status
     *
     * @return The follows status
     */
    public int isFollows() {
        return mFollows;
    }

    /**
     * Sets the follows status
     *
     * @param follows The follow status i.e. friend or not
     */
    public void setFollows(int follows) {
        this.mFollows = follows;
    }

    /**
     * Gets the favourite status in the relationship
     *
     * @return The favourite status
     */
    public int isFavourite() {
        return mFavourite;
    }

    /**
     * Sets the favourite status
     *
     * @param favourite The favourite status for this friend
     */
    public void setFavourite(int favourite) {
        this.mFavourite = favourite;
    }

    /**
     * Gets the target user ID in a relationship
     *
     * @return The target user ID
     */
    public long getTargetUserId() {
        return mTargetUserId;
    }

    /**
     * Sets the target user ID
     *
     * @param targetUserId The target user ID
     */
    public void setTargetUserId(long targetUserId) {
        this.mTargetUserId = targetUserId;
    }
}
