package com.joker.gankor.ui.activity;

import android.content.Intent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.joker.gankor.R;
import com.joker.gankor.ui.BaseActivity;
import com.joker.gankor.utils.API;
import com.joker.gankor.utils.ImageUtil;
import com.joker.gankor.utils.NetUtil;
import com.joker.gankor.utils.OkUtil;

import java.io.IOException;

import okhttp3.Call;

public class SplashActivity extends BaseActivity {
    private ImageView mSplashImageView;

    @Override
    protected void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams
                .FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mSplashImageView = (ImageView) findViewById(R.id.iv_splash);
    }

    @Override
    protected void initData() {
        OkUtil.getInstance().okHttpZhihuJObject(API.ZHIHU_START, "img", new OkUtil.JObjectCallback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                startToMainActivity();
            }

            @Override
            public void onResponse(Call call, String jObjectUrl) {
                ImageUtil.getInstance().displayImage(jObjectUrl, mSplashImageView);
            }
        });

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(3000);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (!NetUtil.isNetConnected(SplashActivity.this)) {
                    Toast.makeText(SplashActivity.this, "网络连接错误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startToMainActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        mSplashImageView.startAnimation(scaleAnimation);
    }

    private void startToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
