package com.joker.gankor.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by joker on 2016/8/12.
 */
public class LazyUtil {
    private static Toast toast;

    //    toast 优化显示
    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    //    Closeable close() 方法封装，增强代码可读性
    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String log) {
        Log.e("TAG", log);
    }

    public static void log(String tag, String log) {
        Log.e(tag, log);
    }
}
