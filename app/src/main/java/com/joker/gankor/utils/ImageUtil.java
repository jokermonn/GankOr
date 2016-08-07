package com.joker.gankor.utils;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by joker on 2016/8/5.
 */
public class ImageUtil {
    private static ImageUtil instance = null;
    private ImageLoader mImageLoader;

    private ImageUtil() {
        mImageLoader = ImageLoader.getInstance();
    }

    public static synchronized ImageUtil getInstance() {
        if (instance == null) {
            instance = new ImageUtil();
        }
        return instance;
    }

//    显示图片
    public void displayImage(String url, ImageView view) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        mImageLoader.displayImage(url, view, options);
    }
}
