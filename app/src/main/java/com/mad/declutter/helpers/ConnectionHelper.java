package com.mad.declutter.helpers;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;

public class ConnectionHelper {

    private Context _context;

    public ConnectionHelper(Context context){
        this._context = context;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
