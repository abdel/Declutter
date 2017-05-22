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

public class TimelineAdapter extends CursorRecyclerAdapter<TimelineAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView screenName;
        TextView statusText;
        ImageView profilePicture;

        ViewHolder (View view) {
            super(view);

            statusText = (TextView) view.findViewById(R.id.statusText);
            screenName = (TextView) view.findViewById(R.id.screenName);
            profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
        }
    }

    public TimelineAdapter(Cursor cursor) {
        super(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_status, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, Cursor cursor) {
        holder.screenName.setText(cursor.getString(cursor.getColumnIndexOrThrow(
                UserSchema.COLUMN_SCREEN_NAME
        )));

        holder.statusText.setText(cursor.getString(cursor.getColumnIndexOrThrow(
                StatusSchema.COLUMN_TEXT
        )));

        UrlImageViewHelper.setUrlDrawable(
                holder.profilePicture,
                cursor.getString(cursor.getColumnIndexOrThrow(
                        UserSchema.COLUMN_PROFILE_IMAGE
                )),
                R.mipmap.avatar);
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        return super.swapCursor(c);
    }
}
