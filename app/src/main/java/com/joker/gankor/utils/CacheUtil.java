package com.joker.gankor.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
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

    //  获取缓存大小
    public double getDirSize(File file) {
        //  判断文件是否存在
        if (file.exists()) {
            //  如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0.00f;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {
                return (double) file.length() / 1024 / 1024;
            }
        } else {
            return 0.00f;
        }
    }

    //  清理缓存
    public boolean clearCache(File file) {
        if (file.isDirectory() && file.exists()) {
            //  遍历获取子文件或子文件夹
            File[] child = file.listFiles();
            for (File childFile : child) {
                if (!childFile.delete()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    public void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases)
     *
     * @param context
     */
    public void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File(context.getFilesDir().getPath()
                + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除外部 cache 下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    public void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * 清除本应用所有的数据
     *
     * @param context
     */
    public void cleanApplicationData(Context context) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
     *
     * @param directory
     */
    private void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if (item.getName().equals("ASimpleCache")) {
                    clear();
                } else {
                    item.delete();
                }
            }
        }
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

    public void clear() {
        mCache.clear();
    }
}
