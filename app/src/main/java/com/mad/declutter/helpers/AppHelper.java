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

package com.mad.declutter.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mad.declutter.R;

/**
 * The AppHelper contains generic and useful methods that can be used in any of the classes. It
 * includes methods such as checking for network connectivity, showing an alert dialog, and splitting
 * an array into smaller chunks.
 *
 * @author Abdelrahman Ahmed
 */
public class AppHelper {

    private Context mContext;

    /**
     * The constructor for the class which sets the application context
     *
     * @param context The application context
     */
    public AppHelper(Context context){
        this.mContext = context;
    }

    /**
     * Checks whether the user has an active network connection
     *
     * @return Returns true if the network connection is available
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Shows an alert dialog
     *
     * @param context The activity context
     * @param title The title of the alert dialog
     * @param message The message content of the dialog
     * @param status The alert status/type (true => success, false => error)
     */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = alertDialogBuilder.create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        if(status != null) {
            // Setting alert dialog icon
            alertDialog.setIcon((status) ? R.drawable.ic_success : R.drawable.ic_fail);
        }

        // Setting OK Button
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    /**
     * Divides an array into smaller array chunks
     *
     * @param array The original array that needs to be divided
     * @param chunkSize The number of chunks to create
     *
     * @return Array of chunks
     */
    static long[][] chunkArray(long[] array, int chunkSize) {
        int numOfChunks = (int)Math.ceil((double)array.length / chunkSize);
        long[][] output = new long[numOfChunks][];

        for(int i = 0; i < numOfChunks; ++i) {
            int start = i * chunkSize;
            int length = Math.min(array.length - start, chunkSize);

            long[] temp = new long[length];
            System.arraycopy(array, start, temp, 0, length);
            output[i] = temp;
        }

        return output;
    }
}
