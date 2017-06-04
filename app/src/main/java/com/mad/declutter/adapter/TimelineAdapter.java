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

import android.view.View;
import android.view.ViewGroup;
import android.database.Cursor;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.support.v7.widget.RecyclerView;

import com.mad.declutter.R;
import com.mad.declutter.db.UserSchema;
import com.mad.declutter.db.StatusSchema;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

/**
 * The Timeline class utilises the CursorRecyclerAdapter to bind the data set from the database
 * (using a Cursor) to the views that are displayed within the RecyclerView. The adapter is responsible
 * for displaying the tweet data from the database.
 *
 * @author Abdelrahman Ahmed
 */
public class TimelineAdapter extends CursorRecyclerAdapter<TimelineAdapter.ViewHolder> {

    /**
     * The ViewHolder describes the item view and the metadata about its place in the RecyclerView.
     * This ViewHolder class also contains an onClickListener for handling any interaction with
     * the items in the view.
     *
     * @author Abdelrahman Ahmed
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView screenName;
        TextView statusText;
        ImageView profilePicture;

        /**
         * The constructor for ViewHolder which identifies the required fields in the view.
         *
         * @param view An item view within the RecyclerView
         */
        ViewHolder (View view) {
            super(view);

            // Find and cache all fields from the view
            statusText = (TextView) view.findViewById(R.id.statusText);
            screenName = (TextView) view.findViewById(R.id.screenName);
            profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
        }
    }

    /**
     * The constructor for the class which sets the cursor for the adapter.
     *
     * @param cursor The Cursor containing the tweets
     */
    public TimelineAdapter(Cursor cursor) {
        super(cursor);
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
                .inflate(R.layout.item_status, parent, false);

        return new ViewHolder(itemView);
    }

    /**
     * onBindViewHolder sets the attributes for each tweet in the view.
     *
     * @param holder The ViewHolder instance
     * @param position The position of this tweet item
     * @param cursor The Cursor containing the tweet data
     */
    @Override
    public void onBindViewHolder (ViewHolder holder, int position, Cursor cursor) {
        // Set the screen name of the user who tweeted
        holder.screenName.setText(cursor.getString(cursor.getColumnIndexOrThrow(
                UserSchema.COLUMN_SCREEN_NAME
        )));

        // Display the tweet text
        holder.statusText.setText(cursor.getString(cursor.getColumnIndexOrThrow(
                StatusSchema.COLUMN_TEXT
        )));

        // Fetch the user's profile picture using the UrlImageViewHelper
        UrlImageViewHelper.setUrlDrawable(
                holder.profilePicture,
                cursor.getString(cursor.getColumnIndexOrThrow(
                        UserSchema.COLUMN_PROFILE_IMAGE
                )),
                R.mipmap.avatar);
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
