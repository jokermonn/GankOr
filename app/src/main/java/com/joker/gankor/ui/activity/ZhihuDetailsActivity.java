package com.joker.gankor.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.joker.gankor.R;
import com.joker.gankor.model.ZhihuDetails;
import com.joker.gankor.ui.BaseActivity;
import com.joker.gankor.utils.CacheUtil;
import com.joker.gankor.utils.ImageUtil;
import com.joker.gankor.utils.OkUtil;

import okhttp3.Call;

public class ZhihuDetailsActivity extends BaseActivity {
    public static final String URL = "url";
    public ZhihuDetails mTopDetails;
    private String url;
    private CacheUtil mCache;
    private ImageView mTitleImageView;
    private CollapsingToolbarLayout mTitleCollapsingToolbarLayout;
    private WebView mContentWebView;
    private Toolbar mTitleToolbar;

    public static Intent newTopStoriesIntent(Activity activity, String url) {
        Bundle bundle = new Bundle();
        bundle.putString(URL, url);
        Intent intent = new Intent(activity, ZhihuDetailsActivity.class);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void initView() {
        setContentView(R.layout.activity_daily_details);
        mTitleImageView = (ImageView) findViewById(R.id.iv_title);
        mTitleToolbar = (Toolbar) findViewById(R.id.tb_title);
        mTitleCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ctl_title);
        mContentWebView = (WebView) findViewById(R.id.wb_content);
        setSupportActionBar(mTitleToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        WebSettings settings = mContentWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        //        加载缓存，如果不存在就加载网络数据
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //        app cache
        settings.setAppCacheEnabled(true);
        //        dom storage
        settings.setDomStorageEnabled(true);
//        database cache
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
    }

    @Override
    protected void initData() {
        mCache = CacheUtil.getInstance(this);
        parseIntent();
        loadContent();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        url = bundle.getString(URL);
    }

    private void loadContent() {
        Gson mGson = new Gson();
        if (!mCache.isCacheEmpty(url)) {
            mTopDetails = mGson.fromJson(mCache.getAsString(url), ZhihuDetails.class);
            initAppBarLayout();
            loadWebView();
        } else {
            if (isNetConnect()) {
                loadLatestData();
            }
        }
    }

    private void loadLatestData() {
        OkUtil.getInstance().okHttpZhihuGson(url, new OkUtil.ResultCallback<ZhihuDetails>
                () {
            @Override
            public void onError(Call call, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(ZhihuDetails response, String json) {
                if (response != null && (mCache.isCacheEmpty(url) || mCache.isNewResponse
                        (url, json))) {
                    mTopDetails = response;
                    mCache.put(url, json);
                    initAppBarLayout();
                    loadWebView();
                }
            }
        });
    }

    private void initAppBarLayout() {
        ImageUtil.getInstance().displayImage(mTopDetails.getImage(), mTitleImageView);
        mTitleCollapsingToolbarLayout.setTitle(mTopDetails.getTitle());
    }

    private void loadWebView() {
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news\" " +
                "type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + mTopDetails.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        mContentWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mContentWebView != null) {
            mContentWebView.destroy();
        }
    }


    @Override
    protected void onPause() {
        if (mContentWebView != null) {
            mContentWebView.onPause();
        }
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mContentWebView != null) {
            mContentWebView.onResume();
        }
    }
}
