package com.joker.gankor.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.joker.gankor.R;
import com.joker.gankor.ui.BaseActivity;
import com.joker.gankor.utils.API;
import com.joker.gankor.utils.CacheUtil;
import com.joker.gankor.utils.ImageUtil;
import com.joker.gankor.utils.LazyUtil;
import com.joker.gankor.utils.NetUtil;
import com.joker.gankor.utils.OkUtil;

import java.io.IOException;

import okhttp3.Call;

public class SplashActivity extends BaseActivity {
    public final String IMG = "img";
    private ImageView mSplashImageView;

    @Override
    protected void initView() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_splash);
        mSplashImageView = (ImageView) findViewById(R.id.iv_splash);
    }

    @Override
    protected void initData() {
        final CacheUtil cacheUtil = CacheUtil.get(this);

        if (NetUtil.isNetConnected(SplashActivity.this)) {
            OkUtil.getInstance().okHttpZhihuJObject(API.ZHIHU_START, IMG, new OkUtil.JObjectCallback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    startToMainActivity();
                }

                @Override
                public void onResponse(Call call, String jObjectUrl) {
                    cacheUtil.put(IMG, jObjectUrl);
                    ImageUtil.getInstance().displayImage(jObjectUrl, mSplashImageView);
                }
            });
        } else {
            LazyUtil.showToast(this, "网络连接错误");
            if (ImageUtil.getInstance().isExist(cacheUtil.getAsString(IMG))) {
                ImageUtil.getInstance().displayImage(cacheUtil.getAsString(IMG), mSplashImageView);
            } else {
                startToMainActivity();
            }
        }

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(3000);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startToMainActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
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
            }
        });
        mSplashImageView.startAnimation(scaleAnimation);
    }

    private void startToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
