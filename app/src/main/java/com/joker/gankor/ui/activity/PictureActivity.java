package com.joker.gankor.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.joker.gankor.R;
import com.joker.gankor.ui.BaseActivity;
import com.joker.gankor.utils.ImageUtil;

public class PictureActivity extends BaseActivity implements View.OnClickListener {
    public static final String EXTRA_IMAGE_URL = "image_url";
    public static final String TRANSIT_PIC = "picture";
    private String mImageUrl;
    private ImageView mPicImageView;
    private Toolbar mTitleToolbar;

    public static Intent newIntent(Context context, String url) {
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putExtra(PictureActivity.EXTRA_IMAGE_URL, url);
        return intent;
    }

    @Override
    protected void initView() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_picture);
        mTitleToolbar = (Toolbar) findViewById(R.id.tb_title);
        mPicImageView = (ImageView) findViewById(R.id.iv_pic);

        setSupportActionBar(mTitleToolbar);
        getSupportActionBar().setTitle("妹纸");
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        mTitleToolbar.setNavigationOnClickListener(this);
        mPicImageView.setOnClickListener(this);

        ViewCompat.setTransitionName(mPicImageView, TRANSIT_PIC);
    }

    @Override
    protected void initData() {
        super.initData();
        parseIntent();

        loadPic(mImageUrl, mPicImageView);
    }

    private void parseIntent() {
        mImageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
    }

    private void loadPic(String imageUrl, ImageView imageView) {
        ImageUtil.getInstance().displayImage(imageUrl, imageView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tb_title:
                onBackPressed();
                break;
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
}
