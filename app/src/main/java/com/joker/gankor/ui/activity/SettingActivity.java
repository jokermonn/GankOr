package com.joker.gankor.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joker.gankor.R;
import com.joker.gankor.ui.BaseActivity;
import com.joker.gankor.utils.CacheUtil;

import java.io.File;

public class SettingActivity extends BaseActivity {
    private CacheUtil mCache;
    private File mStorageCacheFile;
    private File mLocalFile;
    private File mDatabaseFile;
    private TextView mCacheTextView;
    private RelativeLayout mCacheRelativeLayout;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        mCacheTextView = (TextView) findViewById(R.id.tv_cache);
        mCacheRelativeLayout = (RelativeLayout) findViewById(R.id.rl_cache);

        mCache = CacheUtil.getInstance(this);
        mStorageCacheFile = getExternalCacheDir();
        mLocalFile = getCacheDir();
        mDatabaseFile = new File(getFilesDir().getPath()
                + getPackageName() + "/databases");
        mCacheTextView.setText(String.format(getString(R.string.cache_size), getFileSize()));

        mCacheRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCache.cleanApplicationData(SettingActivity.this);
                mCacheTextView.setText(String.format(getString(R.string.cache_size), getFileSize()));
            }
        });
    }

    public double getFileSize() {
        return mCache.getDirSize(mDatabaseFile) + mCache.getDirSize(mStorageCacheFile) + mCache
                .getDirSize(mLocalFile);
    }
}
