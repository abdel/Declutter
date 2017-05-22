package com.mad.declutter.adapter;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.mad.declutter.R;
import com.mad.declutter.db.UserSchema;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FriendAdapter extends CursorRecyclerAdapter<FriendAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView screenName;
        TextView description;
        ImageView profilePicture;

        ViewHolder (View view) {
            super(view);

            description = (TextView) view.findViewById(R.id.description);
            screenName = (TextView) view.findViewById(R.id.screenName);
            profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
        }
    }

    public FriendAdapter(Cursor cursor) {
        super(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, Cursor cursor) {
        holder.screenName.setText(cursor.getString(cursor.getColumnIndexOrThrow(
                UserSchema.COLUMN_SCREEN_NAME
        )));

        holder.description.setText(cursor.getString(cursor.getColumnIndexOrThrow(
                UserSchema.COLUMN_DESCRIPTION
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
