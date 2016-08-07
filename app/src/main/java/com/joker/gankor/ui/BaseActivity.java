package com.joker.gankor.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    protected void initData() {}

    protected abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("BaseActivity", "onDestroy");
    }
}
