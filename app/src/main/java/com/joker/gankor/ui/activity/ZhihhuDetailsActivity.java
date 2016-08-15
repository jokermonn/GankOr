package com.joker.gankor.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

public class ZhihhuDetailsActivity extends BaseActivity implements View.OnClickListener {
    public static final String URL = "url";
    public ZhihuDetails mTopDetails;
    private String url;
    private CacheUtil mCache;
    private ImageView mTitleImageView;
    private AppBarLayout mTitleAppBarLayout;
    private NestedScrollView mContentNestedScrollView;
    private CollapsingToolbarLayout mTitleCollapsingToolbarLayout;
    private WebView mContentWebView;
    private Toolbar mTitleToolbar;
    private String zhihuUrl;

    public static Intent newTopStoriesIntent(Activity activity, String url) {
        Bundle bundle = new Bundle();
        bundle.putString(URL, url);
        Intent intent = new Intent(activity, ZhihhuDetailsActivity.class);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_daily_details);
        mTitleImageView = (ImageView) findViewById(R.id.iv_title);
        mTitleToolbar = (Toolbar) findViewById(R.id.tb_title);
        mTitleCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ctl_title);
        mTitleAppBarLayout = (AppBarLayout) findViewById(R.id.abl_title);
        mContentWebView = (WebView) findViewById(R.id.wb_content);
        mContentNestedScrollView = (NestedScrollView) findViewById(R.id.nsc_content);
        mTitleToolbar.setNavigationOnClickListener(this);
    }

    @Override
    protected void initData() {
        parseIntent();
        loadContent();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        url = bundle.getString(URL);
    }

    private void loadContent() {
        mCache = CacheUtil.getInstance(this);
        Gson mGson = new Gson();
        if (!mCache.isCacheEmpty(URL)) {
            mTopDetails = mGson.fromJson(mCache.getAsString(URL), ZhihuDetails.class);
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
                if (response != null && (mCache.isCacheEmpty(URL) || mCache.isNewResponse(URL, json))) {
                    mTopDetails = response;
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

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView() {
        WebSettings settings = mContentWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        //        加载缓存，如果不存在就加载网络数据
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //        app cache
        settings.setAppCacheEnabled(true);
        //        dom storage
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setSupportZoom(true);
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news\" " +
                "type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + mTopDetails.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        //        获取到原文超链接
        zhihuUrl = mTopDetails.getBody().substring(mTopDetails.getBody().indexOf("<a href=") + 9, mTopDetails.getBody().indexOf("\">查看知乎讨论"));
        mContentWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.fab_show:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(zhihuUrl)));
                break;
            default:
                break;
        }
    }
}
