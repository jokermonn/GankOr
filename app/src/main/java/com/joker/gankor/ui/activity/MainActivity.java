package com.joker.gankor.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.joker.gankor.R;
import com.joker.gankor.adapter.DailyNewsRecyclerAdapter;
import com.joker.gankor.adapter.GankRecyclerAdapter;
import com.joker.gankor.adapter.HotNewsRecyclerAdapter;
import com.joker.gankor.adapter.MainAdapter;
import com.joker.gankor.model.ZhihuDailyNews;
import com.joker.gankor.model.ZhihuHotNews;
import com.joker.gankor.ui.BaseActivity;
import com.joker.gankor.ui.fragment.GankFragment;
import com.joker.gankor.ui.fragment.ZhihuDailyNewsFragment;
import com.joker.gankor.ui.fragment.ZhihuHotNewsFragment;
import com.joker.gankor.utils.API;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GankRecyclerAdapter.TextViewListener,
        GankRecyclerAdapter.ImageViewListener, DailyNewsRecyclerAdapter.OnItemClickListener,
        ZhihuDailyNewsFragment.OnBannerClickListener, HotNewsRecyclerAdapter.OnItemClickListener {

    public MainAdapter mAdapter;
    private Toolbar mTitleToolbar;
    private TabLayout mTitleTabLayout;
    private NavigationView mContentNavigationView;
    private ViewPager mContentViewPager;
    private List<Fragment> mFragments;
    private List<String> mTitles;
    private DrawerLayout mMainDrawerLayout;
    private AppBarLayout mTitleAppBarLayout;
    private long firstTime;
    private int mLastItemId;
    private int itemId;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        mTitleAppBarLayout = (AppBarLayout) findViewById(R.id.abl_title);
        mTitleToolbar = (Toolbar) findViewById(R.id.tb_title);
        mTitleTabLayout = (TabLayout) findViewById(R.id.tl_title);
        mContentViewPager = (ViewPager) findViewById(R.id.vp_content);
        mContentNavigationView = (NavigationView) findViewById(R.id.nv_content);
        mMainDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main);

        setSupportActionBar(mTitleToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mMainDrawerLayout,
                mTitleToolbar, R.string.meizhi, R.string.meizhi);
        mMainDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //        导航栏内容设置
        setupDrawerContent();
    }

    @Override
    protected void initData() {
        changeFragments(R.id.nav_beauty);

        mContentNavigationView.setCheckedItem(0);
    }

    private void setupDrawerContent() {
        mLastItemId = mContentNavigationView.getMenu().getItem(0).getItemId();
        mContentNavigationView.setNavigationItemSelectedListener(new NavigationView
                .OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mMainDrawerLayout.closeDrawers();
                if (item.getItemId() != mLastItemId) {
                    item.setChecked(true);
                    changeFragments(item.getItemId());
                    mLastItemId = item.getItemId();
                }
                return true;
            }
        });
    }

    public void changeFragments(int itemId) {
        if (mFragments == null) {
            mFragments = new ArrayList<Fragment>();
            mTitles = new ArrayList<String>();
        } else {
            mFragments.clear();
            mTitles.clear();
        }

        switch (itemId) {
            case R.id.nav_knowledge:
//                      知乎界面
                ZhihuDailyNewsFragment dailyNewsFragment = new ZhihuDailyNewsFragment();
                dailyNewsFragment.setOnItemClickListener(this);
                dailyNewsFragment.setOnBannerClickListener(this);
                ZhihuHotNewsFragment hotNewsFragment = new ZhihuHotNewsFragment();
                hotNewsFragment.setOnItemClickListener(this);

                mFragments.add(dailyNewsFragment);
                mFragments.add(hotNewsFragment);
                mTitles.add("知乎日報");
                mTitles.add("熱門消息");

                break;
            case R.id.nav_beauty:
//                      妹纸界面
                GankFragment gankFragment = new GankFragment();
                gankFragment.setImageListener(this);
                gankFragment.setTextListener(this);

                mFragments.add(gankFragment);
                mTitles.add("妹纸");

                break;
            default:
                break;
        }
        setupViewPager(mFragments, mTitles);
    }

    private void setupViewPager(List<Fragment> fragments, List<String> titles) {
        mAdapter = new MainAdapter(getSupportFragmentManager(), fragments, titles);
        mContentViewPager.setAdapter(mAdapter);
        mTitleTabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        mTitleTabLayout.setupWithViewPager(mContentViewPager);
        mContentViewPager.setCurrentItem(0);
    }

    public void hideTabLayout(boolean hide) {
        if (hide) {
            mTitleTabLayout.setVisibility(View.GONE);
        } else {
            mTitleTabLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setToolbarScroll(boolean scroll) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mTitleAppBarLayout.getChildAt
                (0).getLayoutParams();
        if (scroll) {
//            上滑隐藏，下滑立即可见
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout
                    .LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        } else {
//            上滑隐藏，下滑以 minHeight 高度可见
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout
                    .LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams
                    .SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);
        }
        mTitleAppBarLayout.getChildAt(0).setLayoutParams(params);
    }

    public void setToolbarTitle(String title) {
        mTitleToolbar.setTitle(title);
    }

//    获取此 fragment 是 viewPager 的第几个页面
    public int getItemId() {
        return itemId < mFragments.size() ? itemId++ : 0;
    }

    @Override
    public void onBackPressed() {
        if (mContentNavigationView.isShown()) {
            mMainDrawerLayout.closeDrawers();
            return;
        }
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Snackbar sb = Snackbar.make(mContentNavigationView, "再按一次退出", Snackbar.LENGTH_SHORT);
            sb.getView().setBackgroundColor(getResources().getColor(R.color.accent));
            sb.show();
            firstTime = secondTime;
        } else {
            finish();
        }
    }

    //    知乎日报列表点击事件
    @Override
    public void onZhihuItemClick(ZhihuDailyNews.StoriesBean storiesBean) {
        startActivity(ZhihhuDetailsActivity.newTopStoriesIntent(this, (API.ZHIHU_NEWS_FOUR + String.valueOf
                (storiesBean.getId()))));
    }

    //    知乎日报头条点击事件
    @Override
    public void onBannerClickListener(ZhihuDailyNews.TopStoriesBean topStories) {
        startActivity(ZhihhuDetailsActivity.newTopStoriesIntent(this, (API.ZHIHU_NEWS_FOUR + String.valueOf
                (topStories.getId()))));
    }

    //    知乎日报热门列表点击事件
    @Override
    public void onZhihuItemClick(ZhihuHotNews.RecentBean recentBean) {
        startActivity(ZhihhuDetailsActivity.newTopStoriesIntent(this, (API.ZHIHU_NEWS_TWO + String.valueOf
                (recentBean.getNewsId()))));
    }

    //    Gank 图片点击
    @Override
    public void onGankImageClick(View image, String url, String desc) {
        clickWelfare(image, url, desc);
    }

    //    Gank 文字点击
    @Override
    public void onGankTextClick(String url) {
        clickVideo(url);
    }

    private void clickWelfare(View image, String url, String desc) {
        Intent intent = PictureActivity.newIntent(MainActivity.this, url, desc);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                MainActivity.this, image, PictureActivity.TRANSIT_PIC);
        try {
            ActivityCompat.startActivity(MainActivity.this, intent, optionsCompat.toBundle());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            startActivity(intent);
        }
    }

    private void clickVideo(String url) {
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
    }
}
