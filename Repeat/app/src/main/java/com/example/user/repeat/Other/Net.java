package com.example.user.repeat.Other;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by user on 2015/12/11.
 */
public class Net {
    public static boolean isNetWork(Context ctxt) {
        ConnectivityManager CM = (ConnectivityManager) ctxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = CM.getActiveNetworkInfo();
        return info != null ? info.isAvailable() : false;
    }
}
