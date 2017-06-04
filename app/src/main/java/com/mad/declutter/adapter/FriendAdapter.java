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

package com.mad.declutter.adapter;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.database.Cursor;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.util.SparseBooleanArray;
import android.support.v7.widget.RecyclerView;

import com.mad.declutter.R;
import com.mad.declutter.db.DatabaseHelper;
import com.mad.declutter.db.UserSchema;
import com.mad.declutter.db.RelationshipSchema;
import com.mad.declutter.activity.FriendsActivity;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

/**
 * The FriendAdapter class utilises the CursorRecyclerAdapter to bind the data set from the database
 * (using a Cursor) to the views that are displayed within the RecyclerView. The adapter also
 * handles any interaction with the item views, such as marking a friend as a favourite by highlighting
 * the selected friend and using the DatabaseHelper to update their status in the database.
 *
 * The FriendAdapter was implemented to be utilised by both the Friends and Favourites activity.
 *
 * @author Abdelrahman Ahmed
 */
public class FriendAdapter extends CursorRecyclerAdapter<FriendAdapter.ViewHolder> {
    private long mUserId;
    private SQLiteDatabase mDbRead;
    private SQLiteDatabase mDbWrite;
    private DatabaseHelper mDbHelper;
    private Context mActivityContext;

    // Constants
    private static final int STATUS_FRIEND = 0;
    private static final int STATUS_FAVOURITE = 1;
    private static final String LOG_KEY = "FriendAdapter";
    private static final String FAVOURITES_ACTIVITY_NAME = "FavouritesActivity";

    /**
     * The constructor for the FriendAdapter which sets the required attributes for the class, such as
     * the database helper and instances of the read/write databases.
     *
     * @param activityContext Activity context
     * @param appContext The application context
     * @param userId The current user Id
     * @param cursor The Cursor containing the friends/favourites data
     */
    public FriendAdapter(Context activityContext, Context appContext, long userId, Cursor cursor) {
        super(cursor);
        this.mUserId = userId;
        this.mActivityContext = activityContext;

        mDbHelper = DatabaseHelper.getInstance((appContext));
        mDbWrite = mDbHelper.getWritableDatabase();
        mDbRead = mDbHelper.getReadableDatabase();
    }

    /**
     * The ViewHolder describes the item view and the metadata about its place in the RecyclerView.
     * This ViewHolder class also contains an onClickListener for handling any interaction with
     * the items in the view.
     *
     * @author Abdelrahman Ahmed
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView screenName;
        TextView description;
        ImageView profilePicture;
        RelativeLayout friendLayout;

        /**
         * The constructor for ViewHolder which identifies the required fields in the view, and
         * also handles the onClick event for selecting a particular item in the layout.
         *
         * @param itemView An item view within the RecyclerView
         */
        ViewHolder(View itemView) {
            super(itemView);

            // Find and cache all fields from the view
            screenName = (TextView) itemView.findViewById(R.id.screenName);
            description = (TextView) itemView.findViewById(R.id.description);
            profilePicture = (ImageView) itemView.findViewById(R.id.profilePicture);
            friendLayout = (RelativeLayout) itemView.findViewById(R.id.friendLayout);

            // An onClick listener for each item
            itemView.setOnClickListener(new View.OnClickListener() {
                /**
                 * An onClick implementation for selecting any friend in the list. This highlights
                 * the selected item and updates the database to mark this friend as a 'favourite'.
                 *
                 * @param v The item that was clicked
                 */
                @Override
                public void onClick(View v) {
                    long targetUserId = 0;
                    int isFavourite = STATUS_FRIEND;

                    // Get the user ID from the view tag
                    if (friendLayout.getTag() != null) {
                        targetUserId = Long.parseLong(friendLayout.getTag().toString());
                    }

                    // Unhighlight the friend if already selected, otherwise highlight
                    // and set isFavourite to 1 (to indicate true)
                    if (friendLayout.isSelected()) {
                        friendLayout.setSelected(false);
                    }
                    else {
                        friendLayout.setSelected(true);
                        isFavourite = STATUS_FAVOURITE;
                    }

                    // Ensure the target user ID was retrieved
                    if (targetUserId != 0) {
                        // Update the favourite status for this friend
                        boolean isUpdated = mDbHelper.updateFavouriteStatus(
                                mDbWrite,
                                mUserId,
                                targetUserId,
                                isFavourite
                        );

                        // Check if the status was successfully updated
                        if (isUpdated) {
                            String activityName = mActivityContext.getClass().getSimpleName();

                            // Refresh the data depending on which activity we're in
                            if (activityName.equals(FAVOURITES_ACTIVITY_NAME)) {
                                refreshFavouritesData();
                            } else {
                                refreshFriendsData();
                            }
                        } else {
                            // Un-select the friend on failure, to allow the user to re-select
                            friendLayout.setSelected(false);
                            Log.e(LOG_KEY, "Failed to update the favourite status for " + targetUserId);
                        }
                    }
                }
            });
        }
    }

    /**
     * The refreshFriendsData method re-fetches all the user's friends data from the database
     * and swaps the cursor in order to update the view
     */
    public void refreshFriendsData() {
        Cursor updatedFriends = mDbHelper.getFriends(mDbRead, mUserId);
        this.swapCursor(updatedFriends);
    }

    /**
     * The refreshFavouritesData method re-fetches all the user's friends that are
     * marked as favourite, and swaps the cursor to update the view
     */
    public void refreshFavouritesData() {
        Cursor updatedFavourites = mDbHelper.getFavourites(mDbRead, mUserId);
        this.swapCursor(updatedFavourites);
    }

    /**
     * onCreateViewHolder creates a new ViewHolder with the specified layout, which then uses
     * the onBindViewHolder method to display the data.
     *
     * @param parent The parent ViewGroup
     * @param viewType ViewType
     * @return ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);

        return new ViewHolder(itemView);
    }

    /**
     * onBindViewHolder sets the attributes for each friend in the view.
     *
     * @param holder The ViewHolder instance
     * @param position The position of this friend item
     * @param cursor The Cursor containing the friend data
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position, Cursor cursor) {
        // Get the friend's favourite status from the Cursor
        int isFavourite = cursor.getInt(cursor.getColumnIndexOrThrow(
                RelationshipSchema.COLUMN_FAVOURITE
        ));

        // Highlight the item if the friend is marked as a favourite,
        // otherwise don't highlight them
        if (isFavourite == STATUS_FAVOURITE) {
            holder.friendLayout.setSelected(true);
        } else {
            holder.friendLayout.setSelected(false);
        }

        // Set the friend ID in the tag of the item layout
        holder.friendLayout.setTag(cursor.getLong(cursor.getColumnIndexOrThrow(
                RelationshipSchema.COLUMN_TARGET_USER_ID
        )));

        // Set the screen name of the username
        holder.screenName.setText(cursor.getString(cursor.getColumnIndexOrThrow(
                UserSchema.COLUMN_SCREEN_NAME
        )));

        // Set the biography of the user
        holder.description.setText(cursor.getString(cursor.getColumnIndexOrThrow(
                UserSchema.COLUMN_DESCRIPTION
        )));

        // Fetch the user's profile picture using the UrlImageViewHelper
        UrlImageViewHelper.setUrlDrawable(
                holder.profilePicture,
                cursor.getString(cursor.getColumnIndexOrThrow(
                        UserSchema.COLUMN_PROFILE_IMAGE
                )),
                R.mipmap.avatar
        );
    }

    /**
     * Swaps the current cursor with a new one in order to update the RecyclerView. swapCursor
     * also automatically calls the notifyDataSetChanged method.
     *
     * @param c The new cursor
     * @return Cursor
     */
    @Override
    public Cursor swapCursor(Cursor c) {
        return super.swapCursor(c);
    }
}
