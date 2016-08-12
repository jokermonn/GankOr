package com.joker.gankor.utils;

import android.content.Context;
import android.widget.Toast;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by joker on 2016/8/12.
 */
public class LazyUtil {
    private static Toast toast;

    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
