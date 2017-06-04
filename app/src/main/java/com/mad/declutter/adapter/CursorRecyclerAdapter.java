/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 ARNAUD FRUGIER
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.mad.declutter.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

/**
 * The CursorRecyclerAdapter was developed by Arnaud Frugier and provides an easy way to use
 * RecyclerView with data from the database inside a Cursor. It slightly modified to fit the requirements
 * of the Declutter application.
 *
 * @author Arnaud Frugier, Abdelrahman Ahmed
 * @see <a href="https://quanturium.github.io/2015/04/19/using-cursors-with-the-new-recyclerview/">ref</a>
 * @param <VH> ViewHolder
 */
abstract class CursorRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private boolean mDataValid;
    private Cursor mCursor;
    private int mRowIDColumn;

    /**
     * The constructor for the class and initialises the cursor
     *
     * @param c Cursor containing the data
     */
    CursorRecyclerAdapter(Cursor c) {
        init(c);
    }

    /**
     * Initialises the cursor
     *
     * @param c Cursor containing data
     */
    private void init(Cursor c) {
        boolean cursorPresent = c != null;
        mCursor = c;
        mDataValid = cursorPresent;
        mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;
        setHasStableIds(true);
    }

    /**
     * Implementation of the original onBindViewVolder that calls the modified onBindViewHolder
     * that contains the Cursor support
     *
     * @param holder ViewHolder
     * @param position Item position
     */
    @Override
    public final void onBindViewHolder (VH holder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        onBindViewHolder(holder, position, mCursor);
    }

    /**
     * The onBindViewHolder method is used to set the attributes for each item in the view, and
     * utilises the Cursor to retrieve the data for the attributes.
     *
     * @param holder ViewHolder
     * @param position Position of the item
     * @param cursor The Cursor containing the data
     */
    public abstract void onBindViewHolder(VH holder, int position, Cursor cursor);

    /**
     * Gets the Cursor from the class attribute
     *
     * @return Cursor
     */
    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * Gets the number of items in the cursor
     *
     * @return Number of items in the cursor
     */
    @Override
    public int getItemCount () {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    /**
     * Get the item id from the position
     *
     * @param position The item position
     * @return The item id
     */
    @Override
    public long getItemId(int position) {
        if(hasStableIds() && mDataValid && mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            } else {
                return RecyclerView.NO_ID;
            }
        } else {
            return RecyclerView.NO_ID;
        }
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     *
     * @param cursor The new cursor to be used
     */
    private void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     *
     * @param newCursor The new cursor to be used.
     * @return Returns the previously set Cursor, or null if there wasa not one.
     * If the given new Cursor is the same instance is the previously set
     * Cursor, null is also returned.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    /**
     * <p>Converts the cursor into a CharSequence. Subclasses should override this
     * method to convert their results. The default implementation returns an
     * empty String for null values or the default String representation of
     * the value.</p>
     *
     * @param cursor the cursor to convert to a CharSequence
     * @return a CharSequence representing the value
     */
    public CharSequence convertToString(Cursor cursor) {
        return cursor == null ? "" : cursor.toString();
    }
}
