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
import android.widget.TextView;
import android.content.Context;
import android.view.LayoutInflater;
import android.support.v7.widget.RecyclerView;

import com.mad.declutter.R;
import java.util.ArrayList;

import twitter4j.User;

/**
 * Mobile Application Development - Exercise 6
 *
 * TrainAdapter class binds the train data from the MainActivity trainsList and sets it to the
 * views that are displayed in the RecyclerView. The adapter also uses the RefreshTrainInterface
 * which is an interface between MainActivity and TrainAdapter to listen to clicks on the
 * arrivalTimeLayout in order to refresh an individual train item.
 *
 * @author Abdelrahman Ahmed (Abdel)
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<User> mFriendsList;

    /**
     * The ViewHolder class describes the friend view and the data that will be contained in the view.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mFriendScreenName;

        /**
         * Constructor for the ViewHolder class. Finds the views for the friend data and sets them
         * as class variables.
         *
         * @param view The friend item view
         */
        ViewHolder(View view) {
            super(view);

            mFriendScreenName = (TextView) view.findViewById(R.id.friendScreenName);
        }
    }

    /**
     * FriendAdapter constructor sets the context and trains ArrayList
     *
     * @param context The application context
     * @param friends An ArrayList of the friends
     */
    public FriendAdapter(Context context, ArrayList<User> friends) {
        this.mContext = context;
        this.mFriendsList = friends;
    }

    /**
     * Inflates the layout for RecyclerView. Uses train_item.xml as a layout for the individual
     * item in the RecyclerView.
     * @param parent The parent ViewGroup (RecyclerView)
     * @param viewType View type of the new view
     * @return ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);

        return new ViewHolder(itemView);
    }

    /**
     * Initialises the values for each item in the RecyclerView.
     *
     * @param holder ViewHolder
     * @param position The position of the train/item
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User friend = mFriendsList.get(position);
    }

    /**
     * Returns the size of the trains ArrayList
     *
     * @return The size of the trains list
     */
    @Override
    public int getItemCount() {
        return mFriendsList.size();
    }
}
