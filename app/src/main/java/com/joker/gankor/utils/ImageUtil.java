package com.joker.gankor.utils;

import android.widget.ImageView;

import com.joker.gankor.R;
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

//    添加加载时显示图片，可有效解决快速滑动 recyclerView 异步加载图片错位
    public void displayImageOnLoading(String url, ImageView view) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.download_defualt)
                .build();
        mImageLoader.displayImage(url, view, options);
    }

    //    取出缓存图片路径
    public String getAbsolutePath(String url) {
        return mImageLoader.getDiskCache().get(url).getAbsolutePath();
    }

    //    判断是否有缓存
    public boolean isExist(String url) {
        return mImageLoader.getDiskCache().get(url).exists();
    }
}
