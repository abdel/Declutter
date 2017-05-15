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

public class Relationship {

    private long mUserId;
    private long mUpdatedAt;
    private boolean mFollows;
    private long mTargetUserId;

    public Relationship(long userId, long targetUserId, boolean follows) {
        this.mUserId = userId;
        this.mFollows = follows;
        this.mTargetUserId = targetUserId;
        this.mUpdatedAt = System.currentTimeMillis();
    }

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long mUserId) {
        this.mUserId = mUserId;
    }

    public long getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(long mUpdatedAt) {
        this.mUpdatedAt = mUpdatedAt;
    }

    public boolean isFollows() {
        return mFollows;
    }

    public void setFollows(boolean mFollows) {
        this.mFollows = mFollows;
    }

    public long getTargetUserId() {
        return mTargetUserId;
    }

    public void setTargetUserId(long mTargetUserId) {
        this.mTargetUserId = mTargetUserId;
    }
}
