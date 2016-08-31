package com.joker.gankor.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.joker.gankor.R;
import com.joker.gankor.adapter.DailyNewsRecyclerAdapter;
import com.joker.gankor.adapter.GankRecyclerAdapter;
import com.joker.gankor.adapter.HotNewsRecyclerAdapter;
import com.joker.gankor.model.GankWelfare;
import com.joker.gankor.model.ZhihuDailyNews;
import com.joker.gankor.model.ZhihuHotNews;
import com.joker.gankor.ui.BaseActivity;
import com.joker.gankor.ui.fragment.MainFragment;
import com.joker.gankor.ui.fragment.ZhihuDailyNewsFragment;
import com.joker.gankor.utils.API;
import com.joker.gankor.utils.CacheUtil;
import com.joker.gankor.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GankRecyclerAdapter.TextViewListener,
        GankRecyclerAdapter.ImageViewListener, DailyNewsRecyclerAdapter.OnDailyItemClickListener,
        ZhihuDailyNewsFragment.OnBannerClickListener, HotNewsRecyclerAdapter.OnHotItemClickListener {

    public MainFragment mContentGank;
    public MainFragment mContentZhihu;
    private Toolbar mTitleToolbar;
    private TabLayout mTitleTabLayout;
    private NavigationView mContentNavigationView;
    private DrawerLayout mMainDrawerLayout;
    private long firstTime;
    private int mLastItemId;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mTitleToolbar = (Toolbar) findViewById(R.id.tb_title);
        mTitleTabLayout = (TabLayout) findViewById(R.id.tl_title);
        mContentNavigationView = (NavigationView) findViewById(R.id.nv_content);
        mMainDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main);

//        设置导航栏顶部图片
        View view = mContentNavigationView.getHeaderView(0);
        ImageView header = (ImageView) view.findViewById(R.id.nav_head);
        ImageUtil.getInstance().displayImage(CacheUtil.getInstance(this).getAsString(SplashActivity.IMG),
                header);

//        设置 toolBar
        setSupportActionBar(mTitleToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mMainDrawerLayout,
                mTitleToolbar, R.string.meizhi, R.string.meizhi);
        mMainDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


//        设置 viewPager
//        setupViewPager();
    }

    @Override
    protected void initData() {
        setupDrawerContent();
        mContentNavigationView.setCheckedItem(0);
    }

    private void setupDrawerContent() {
        mLastItemId = mContentNavigationView.getMenu().getItem(0).getItemId();
        changeFragments(mLastItemId);
        mContentNavigationView.setNavigationItemSelectedListener(new NavigationView
                .OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mMainDrawerLayout.closeDrawers();
                if (item.getItemId() == R.id.menu_introduce) {
                    startActivity(new Intent(MainActivity.this, AboutMeActivity.class));
                    item.setChecked(false);
                } else {
                    if (item.getItemId() != mLastItemId) {
                        item.setChecked(true);
                        changeFragments(item.getItemId());
                        mLastItemId = item.getItemId();
                    }
                }
                return true;
            }
        });
    }

    public void changeFragments(int itemId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAll(transaction);
        switch (itemId) {
            case R.id.nav_knowledge:
//                      知乎界面
                if (mContentZhihu != null) {
                    transaction.show(mContentZhihu);
                } else {
                    mContentZhihu = MainFragment.newInstance(MainFragment
                            .MENU_ZHIHU);
                    mContentZhihu.setOnBannerClickListener(this);
                    mContentZhihu.setOnDailyItemClickListener(this);
                    mContentZhihu.setOnBannerClickListener(this);
                    mContentZhihu.setOnHotItemClickListener(this);
                    transaction.add(R.id.fl_content, mContentZhihu);
                }
                initToolbar(MainFragment
                        .MENU_ZHIHU);
                break;
            case R.id.nav_beauty:
//                      妹纸界面
                if (mContentGank != null) {
                    transaction.show(mContentGank);
                } else {
                    mContentGank = MainFragment.newInstance(MainFragment
                            .MENU_GANK);
                    mContentGank.setTextListener(this);
                    mContentGank.setImageListener(this);
                    transaction.add(R.id.fl_content, mContentGank);
                }
                initToolbar(MainFragment
                        .MENU_GANK);
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void hideAll(FragmentTransaction transaction) {
        if (mContentZhihu != null) {
            transaction.hide(mContentZhihu);
        }
        if (mContentGank != null) {
            transaction.hide(mContentGank);
        }
    }

    //    暴露给 fragment 连接 tabLayout
    public void setupViewPager(ViewPager viewPager) {
        mTitleTabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        mTitleTabLayout.setupWithViewPager(viewPager);
    }

    public void hideTabLayout(boolean hide) {
        if (hide) {
            mTitleTabLayout.setVisibility(View.GONE);
        } else {
            mTitleTabLayout.setVisibility(View.VISIBLE);
        }
    }

    public void initToolbar(String args) {
        if (args.equals(MainFragment.MENU_GANK)) {
            hideTabLayout(true);
            setToolbarTitle("妹纸");
        } else {
            hideTabLayout(false);
            setToolbarTitle("知乎");
        }
    }

    /*
    public void setToolbarScroll(boolean scroll) {
        AppBarLayout.LayoutParams paramsTool = (AppBarLayout.LayoutParams) mTitleAppBarLayout.getChildAt
                (0).getLayoutParams();
        AppBarLayout.LayoutParams paramsTab = (AppBarLayout.LayoutParams) mTitleAppBarLayout.getChildAt
                (1).getLayoutParams();
        if (scroll) {
//            toolBar上滑隐藏，下滑立即可见
            paramsTool.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout
                    .LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        } else {
//            toolBar上滑隐藏，下滑不可见
            paramsTool.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
            paramsTab.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout
                    .LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams
                    .SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        }
        mTitleAppBarLayout.getChildAt(0).setLayoutParams(paramsTool);
        mTitleAppBarLayout.getChildAt(1).setLayoutParams(paramsTab);
    }
    */

    public void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
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
            sb.getView().setBackgroundColor(getResources().getColor(R.color.red_300));
            sb.show();
            firstTime = secondTime;
        } else {
            finish();
        }
    }

    //    知乎日报列表点击事件
    @Override
    public void onZhihuDailyItemClick(View view, ZhihuDailyNews.StoriesBean storiesBean) {
        int[] clickLocation = getClickLocation(view);
        startActivity(ZhihuDetailsActivity.newTopStoriesIntent(this, (API.ZHIHU_NEWS_FOUR + String.valueOf
                (storiesBean.getId())), clickLocation));
        this.overridePendingTransition(0, 0);
    }

    //    知乎日报头条点击事件
    @Override
    public void onBannerClickListener(ZhihuDailyNews.TopStoriesBean topStories) {
        startActivity(ZhihuDetailsActivity.newTopStoriesIntent(this, (API.ZHIHU_NEWS_FOUR + String.valueOf
                (topStories.getId())), null));
    }

    //    知乎热门列表点击事件
    @Override
    public void onZhihuHotItemClick(View view, ZhihuHotNews.RecentBean recentBean) {
        int[] clickLocation = getClickLocation(view);
        startActivity(ZhihuDetailsActivity.newTopStoriesIntent(this, (API.ZHIHU_NEWS_TWO + String.valueOf
                (recentBean.getNewsId())), clickLocation));
        this.overridePendingTransition(0, 0);
    }

    //    Gank 图片点击
    @Override
    public void onGankImageClick(View image, List<GankWelfare.ResultsBean> bean, int position) {
        Intent intent = PictureActivity.newIntent(MainActivity.this, (ArrayList<GankWelfare.ResultsBean>) bean, position);
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
    @Override
    public void onGankTextClick(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private int[] getClickLocation(View v) {
        int[] clickLocation = new int[2];
        v.getLocationOnScreen(clickLocation);
        clickLocation[0] += v.getWidth() / 2;

        return clickLocation;
    }
}