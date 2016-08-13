package com.joker.gankor.utils;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;

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

//    Closeable close() 方法封装，增强代码可读性
    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    CacheUtil 中的源码
    public static String json2String(String json) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new StringBufferInputStream(json)));
            String readString = "";
            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                readString += currentLine;
            }
            return readString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(in);
        }
    }
}
