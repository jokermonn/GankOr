package com.joker.gankor.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.joker.gankor.R;
import com.joker.gankor.model.ZhihuDetails;
import com.joker.gankor.ui.BaseActivity;
import com.joker.gankor.utils.CacheUtil;
import com.joker.gankor.utils.ImageUtil;
import com.joker.gankor.utils.LazyUtil;
import com.joker.gankor.utils.OkUtil;
import com.joker.gankor.view.RevealBackgroundView;

import okhttp3.Call;

public class ZhihuDetailsActivity extends BaseActivity implements RevealBackgroundView
        .OnStateChangeListener {
    public static final String URL = "url";
    public static final String LOCATION = "location";
    public ZhihuDetails mTopDetails;
    public int[] mLocation;
    public RevealBackgroundView mContentRevealBackgroundView;
    private String url;
    private CacheUtil mCache;
    private ImageView mTitleImageView;
    private CollapsingToolbarLayout mTitleCollapsingToolbarLayout;
    private WebView mContentWebView;
    private NestedScrollView mContentNestedScrollView;
    private AppBarLayout mContentAppBarLayout;

    public static Intent newTopStoriesIntent(Activity activity, String url, int[] locationArr) {
        Bundle bundle = new Bundle();
        bundle.putString(URL, url);
        bundle.putIntArray(LOCATION, locationArr);
        Intent intent = new Intent(activity, ZhihuDetailsActivity.class);
        intent.putExtras(bundle);

        return intent;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_daily_details);
        mContentRevealBackgroundView = (RevealBackgroundView) findViewById(R.id.rbv_content);
        mTitleImageView = (ImageView) findViewById(R.id.iv_title);
        Toolbar mTitleToolbar = (Toolbar) findViewById(R.id.tb_title);
        mTitleCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ctl_title);
        mContentAppBarLayout = (AppBarLayout) findViewById(R.id.abl_content);
        mContentNestedScrollView = (NestedScrollView) findViewById(R.id.nsv_content);
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

        parseIntent();

        mContentRevealBackgroundView.setOnStateChangeListener(this);
        if (mLocation == null || savedInstanceState != null) {
            mContentRevealBackgroundView.setToFinishedFrame();
        } else {
            mContentRevealBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
                    .OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mContentRevealBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mContentRevealBackgroundView.startFromLocation(mLocation);
                    return true;
                }
            });
        }
    }

    @Override
    protected void initData() {
        mCache = CacheUtil.getInstance(this);
        loadContent();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        url = bundle.getString(URL);
        mLocation = bundle.getIntArray(LOCATION);
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
            } else {
                LazyUtil.showToast(ZhihuDetailsActivity.this, "网络没有连接哦");
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
                if (response != null && mCache.isNewResponse(url, json)) {
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
            mContentWebView.removeAllViews();
            mContentWebView.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mContentWebView != null) {
            mContentWebView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mContentWebView != null) {
            mContentWebView.onResume();
        }
    }

    @Override
    public void onStateChange(int state) {
        if (state == RevealBackgroundView.STATE_FINISHED) {
            mContentNestedScrollView.setVisibility(View.VISIBLE);
            mContentAppBarLayout.setVisibility(View.VISIBLE);
        } else {
            mContentNestedScrollView.setVisibility(View.GONE);
            mContentAppBarLayout.setVisibility(View.GONE);
        }
    }
}
