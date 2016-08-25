package com.joker.gankor.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.joker.gankor.R;
import com.joker.gankor.ui.BaseActivity;
import com.joker.gankor.utils.ImageUtil;
import com.joker.gankor.utils.LazyUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PictureActivity extends BaseActivity implements View.OnClickListener {
    public static final String EXTRA_IMAGE_URL = "image_url";
    public static final String EXTRA_IMAGE_TITLE = "image_title";
    public static final String TRANSIT_PIC = "picture";
    private String mImageUrl;
    private String mName;
    private ImageView mPicImageView;
    private Toolbar mTitleToolbar;

    public static Intent newIntent(Context context, String url, String desc) {
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putExtra(PictureActivity.EXTRA_IMAGE_URL, url);
        intent.putExtra(PictureActivity.EXTRA_IMAGE_TITLE, desc);
        return intent;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_picture);
        mTitleToolbar = (Toolbar) findViewById(R.id.tb_title);
        mPicImageView = (ImageView) findViewById(R.id.iv_pic);

        mTitleToolbar.setNavigationOnClickListener(this);
        setSupportActionBar(mTitleToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        mPicImageView.setOnClickListener(this);

        ViewCompat.setTransitionName(mPicImageView, TRANSIT_PIC);
    }

    @Override
    protected void initData() {
        super.initData();
        parseIntent();
        getSupportActionBar().setTitle(mName);

        loadPic(mImageUrl, mPicImageView);
    }

    private void parseIntent() {
        mImageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        mName = getIntent().getStringExtra(EXTRA_IMAGE_TITLE);
    }

    private void loadPic(String imageUrl, ImageView imageView) {
        ImageUtil.getInstance().displayImage(imageUrl, imageView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_pic:
                changeToolbar();
                break;
            default:
                break;
        }
    }

    private void changeToolbar() {
        if (mTitleToolbar.isShown()) {
            mTitleToolbar.setVisibility(View.INVISIBLE);
        } else {
            mTitleToolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_save:
                saveImage();
                return true;
            case R.id.menu_share:
                LazyUtil.showToast("暂时不支持分享功能哦");
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveImage() {
        //        6.0 检查权限
        if (Build.VERSION.SDK_INT >= 23) {
            int write = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (write != PackageManager.PERMISSION_GRANTED || read != PackageManager
                    .PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest
                        .permission.READ_EXTERNAL_STORAGE}, 300);
            }
        }
        File saveFile = new File(Environment.getExternalStorageDirectory(), "GankOr");
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        if (copyImage(saveFile.getAbsolutePath() + "//" + mName + ".jpg")) {
            LazyUtil.showToast(String.format(getString(R.string.picture_save_on), saveFile
                    .getAbsolutePath()));
        } else {
            LazyUtil.showToast("保存失败");
        }
    }

    public boolean copyImage(String newPath) {
        String oldPath = ImageUtil.getInstance().getAbsolutePath(mImageUrl);
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
                int length;
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
