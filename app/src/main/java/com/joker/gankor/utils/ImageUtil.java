package com.joker.gankor.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.widget.ImageView;

import com.joker.gankor.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

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
                .showImageForEmptyUri(R.drawable.download_defualt)
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

    //      保存图片
    public void saveImage(Activity activity, String name, String url) {
        //        6.0 检查权限
        if (Build.VERSION.SDK_INT >= 23) {
            int write = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (write != PackageManager.PERMISSION_GRANTED || read != PackageManager
                    .PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest
                        .permission.READ_EXTERNAL_STORAGE}, 300);
            }
        }
        File saveFile = new File(Environment.getExternalStorageDirectory(), "GankOr");
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        if (copyImage(saveFile.getAbsolutePath() + "//" + name + ".jpg",
                url)) {
            LazyUtil.showToast(String.format(activity.getString(R.string.picture_save_on), saveFile
                    .getAbsolutePath()));
        } else {
            LazyUtil.showToast("保存失败");
        }
    }

    //    拷贝图片
    public boolean copyImage(String newPath, String url) {
        String oldPath = ImageUtil.getInstance().getAbsolutePath(url);
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                inStream = new FileInputStream(oldPath); //读入原文件
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LazyUtil.close(inStream);
            LazyUtil.close(fs);
        }
        return false;
    }
}
