package com.joker.gankor.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
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
                ImageUtil.getInstance().saveImage(this, mName, mImageUrl);
                return true;
            case R.id.menu_share:
                LazyUtil.showToast("暂时不支持分享功能哦");
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}