package com.joker.gankor.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.joker.gankor.utils.NetUtil;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityState(this);

        initView(savedInstanceState);
        initData();
    }

    public void setActivityState(Activity activity) {
//        设置 APP 只能竖屏显示
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected void initData() {}

    protected abstract void initView(Bundle savedInstanceState);

    public boolean isNetConnect() {
        return NetUtil.isNetConnect(this);
    }
}
