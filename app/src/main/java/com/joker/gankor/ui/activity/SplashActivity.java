package com.joker.gankor.ui.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.joker.gankor.utils.OkUtil;

import java.io.IOException;

import okhttp3.Call;

public class SplashActivity extends BaseActivity {
    public static final String IMG = "img";
    private ImageView mSplashImageView;
    private CacheUtil mCache;

    @Override
    protected void initView(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_splash);
        mSplashImageView = (ImageView) findViewById(R.id.iv_splash);
    }

    @Override
    protected void initData() {
        mCache = CacheUtil.getInstance(this);

//        缓存不为空加载缓存
        if (!mCache.isCacheEmpty(IMG)) {
            ImageUtil.getInstance().displayImage(mCache.getAsString(IMG), mSplashImageView);
        }
//        在联网情况下，写入新缓存，且如果旧缓存为空则显示图片
        if (isNetConnect()) {
            OkUtil.getInstance().okHttpZhihuJObject(API.ZHIHU_START, IMG, new OkUtil.JObjectCallback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    startToMainActivity();
                }

                @Override
                public void onResponse(Call call, String jObjectUrl) {
                    if (jObjectUrl != null) {
                        if (mCache.isCacheEmpty(IMG)) {
                            ImageUtil.getInstance().displayImage(jObjectUrl, mSplashImageView);
                        }
                        mCache.put(IMG, jObjectUrl);
                    } else {
                        if (mCache.isCacheEmpty(IMG)) {
                            startToMainActivity();
                        }
                    }
                }
            });
        } else {
//            没网没缓存
            if (mCache.isCacheEmpty(IMG)) {
                startToMainActivity();
            }
            LazyUtil.showToast(this, "网络连接错误");
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
            }
        });
        mSplashImageView.startAnimation(scaleAnimation);
    }

    private void startToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
