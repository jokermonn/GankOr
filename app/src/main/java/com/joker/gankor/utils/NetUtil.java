package com.joker.gankor.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

/**
 * Created by joker on 2016/8/5.
 */
public class NetUtil {
    public static boolean isNetConnect(@NonNull Context context) {
        ConnectivityManager service = (ConnectivityManager) context.getApplicationContext().getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo info = service.getActiveNetworkInfo();

        return info != null && info.isAvailable();
    }
}