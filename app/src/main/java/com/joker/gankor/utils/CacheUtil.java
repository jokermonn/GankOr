package com.joker.gankor.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * 基于 ASimpleCache 的二次封装
 * Created by joker on 2016/8/14.
 */
public class CacheUtil {
    private static CacheUtil instance = null;
    private ASimpleCache mCache;

    private CacheUtil(Context context) {
        mCache = ASimpleCache.get(context);
    }

    public static synchronized CacheUtil getInstance(Context context) {
        if (instance == null) {
            instance = new CacheUtil(context);
        }
        return instance;
    }

    //    ASimpleCache 中的源码
    public String json2String(String json) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new StringReader(json));
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
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isCacheEmpty(String key) {
        return TextUtils.isEmpty(mCache.getAsString(key));
    }

    public boolean isNewResponse(String key, String newJson) {
        return (!TextUtils.isEmpty(mCache.getAsString(key)) &&
                !mCache.getAsString(key).equals(json2String(newJson))) || TextUtils.isEmpty(mCache
                .getAsString(key));
    }

    public String getAsString(String key) {
        return mCache.getAsString(key);
    }

    // 缓存只保存一天
    public void put(String key, String value) {
        mCache.put(key, value, ASimpleCache.TIME_DAY);
    }
}
