package com.joker.gankor.ui.activity;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.joker.gankor.R;
import com.joker.gankor.adapter.DailyNewsRecyclerAdapter;
import com.joker.gankor.adapter.GankRecyclerAdapter;
import com.joker.gankor.adapter.MainAdapter;
import com.joker.gankor.model.ZhihuDailyNews;
import com.joker.gankor.ui.BaseActivity;
import com.joker.gankor.ui.fragment.GankFragment;
import com.joker.gankor.ui.fragment.ZhihuDailyNewsFragment;
import com.joker.gankor.ui.fragment.ZhihuHotNewsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GankRecyclerAdapter.TextViewListener,
        GankRecyclerAdapter.ImageViewListener, DailyNewsRecyclerAdapter.OnItemClickListener,
        ZhihuDailyNewsFragment.OnBannerClickListener {

    private Toolbar mTitleToolbar;
    private TabLayout mTitleTabLayout;
    private NavigationView mContentNavigationView;
    private ViewPager mContentViewPager;
    private SwipeRefreshLayout mContentSwipeRefreshLayout;
    private List<Fragment> mZhihuFragments;
    private List<Fragment> mFragments;
    private List<String> mZhihuTitles;
    private List<String> mTitles;
    private DrawerLayout mMainDrawerLayout;
    private AppBarLayout mTitleAppBarLayout;
    private long firstTime;
    private GankFragment mGankFragment;
    private ZhihuDailyNewsFragment mDailyNewsFragment;
    private ZhihuHotNewsFragment mHotNewsFragment;
    private int mLastItemId;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        mTitleAppBarLayout = (AppBarLayout) findViewById(R.id.abl_title);
        mTitleToolbar = (Toolbar) findViewById(R.id.tb_title);
        mTitleTabLayout = (TabLayout) findViewById(R.id.tl_title);
        mContentViewPager = (ViewPager) findViewById(R.id.vp_content);
        mContentSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_content);
        mContentNavigationView = (NavigationView) findViewById(R.id.nv_content);
        mMainDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main);
    }

    @Override
    protected void initData() {
        setSupportActionBar(mTitleToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mMainDrawerLayout,
                mTitleToolbar, R.string.meizhi, R.string.meizhi);
        mMainDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        loadGankFragment();

//        导航栏内容设置
        setupDrawerContent();
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
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    hideAllFragment(transaction);
                    switch (item.getItemId()) {
                        case R.id.nav_knowledge:
//                      知乎界面
                            loadZhihuFragments();
                            break;
                        case R.id.nav_beauty:
//                      妹纸界面
                            loadGankFragment();
                            break;
                        default:
                            break;
                    }
                    transaction.commit();
                    mLastItemId = item.getItemId();
                }
                return true;
            }
        });
    }

    private void loadGankFragment() {
        mFragments = new ArrayList<Fragment>();
        mGankFragment = new GankFragment();
        mGankFragment.setImageListener(this);
        mGankFragment.setTextListener(this);
        mFragments.add(mGankFragment);
        mTitles = new ArrayList<String>();
        mTitles.add("妹纸");

        setupViewPager(mFragments, mTitles);
    }

    private void loadZhihuFragments() {
        mZhihuFragments = new ArrayList<Fragment>();
        mDailyNewsFragment = new ZhihuDailyNewsFragment();
        mDailyNewsFragment.setOnItemClickListener(this);
        mDailyNewsFragment.setOnBannerClickListener(this);
        mHotNewsFragment = new ZhihuHotNewsFragment();
        mZhihuFragments.add(mDailyNewsFragment);
        mZhihuFragments.add(mHotNewsFragment);
        mZhihuTitles = new ArrayList<String>();
        mZhihuTitles.add("知乎日報");
        mZhihuTitles.add("熱門消息");

        setupViewPager(mZhihuFragments, mZhihuTitles);
    }

    private void setupViewPager(List<Fragment> fragments, List<String> titles) {
        mContentViewPager.setAdapter(new MainAdapter(getSupportFragmentManager(), fragments, titles));
        mTitleTabLayout.setupWithViewPager(mContentViewPager);
        mContentViewPager.setCurrentItem(0);
    }

    private void hideAllFragment(FragmentTransaction transaction) {
        if (mGankFragment != null) {
            transaction.hide(mGankFragment);
        }
        if (mDailyNewsFragment != null) {
            transaction.hide(mDailyNewsFragment);
        }
        if (mHotNewsFragment != null) {
            transaction.hide(mHotNewsFragment);
        }
    }

    public void hideTabLayout(boolean hide) {
        if (hide) {
            mTitleTabLayout.setVisibility(View.GONE);
        } else {
            mTitleTabLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setToolbarScroll(boolean scroll) {
        if (scroll) {
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mTitleAppBarLayout.getChildAt
                    (0).getLayoutParams();
//            上滑隐藏，下滑立即可见
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout
                    .LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            mTitleAppBarLayout.getChildAt(0).setLayoutParams(params);
        }
    }

    public void setToolbarTitle(String title) {
        mTitleToolbar.setTitle(title);
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

    //    Gank 点击事件
    @Override
    public void onClick(View view, View image, String url) {
        switch (view.getId()) {
            case R.id.tv_content:
                clickVideo( url);
                break;
            case R.id.iv_content:
                clickWelfare(image, url);
                break;
            default:
                break;
        }
    }

    //    知乎日报列表点击事件
    @Override
    public void onClick(ZhihuDailyNews.StoriesBean bean) {
        Toast.makeText(this, bean.getTitle(), Toast.LENGTH_SHORT).show();
    }

    //    知乎日报头条点击事件
    @Override
    public void onBannerClickListener(ZhihuDailyNews.TopStoriesBean topStories) {
        Toast.makeText(this, topStories.getTitle(), Toast.LENGTH_SHORT).show();
    }

    //    Gank 图片点击
    private void clickWelfare(View image, String url) {
        Intent intent = PictureActivity.newIntent(MainActivity.this, url);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                MainActivity.this, image, PictureActivity.TRANSIT_PIC);
        try {
            ActivityCompat.startActivity(MainActivity.this, intent, optionsCompat.toBundle());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            startActivity(intent);
        }
    }

    //    Gank 文字点击
    private void clickVideo(String url) {
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
    }
}
