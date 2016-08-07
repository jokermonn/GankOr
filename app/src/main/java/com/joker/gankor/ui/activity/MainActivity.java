package com.joker.gankor.ui.activity;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.joker.gankor.R;
import com.joker.gankor.adapter.MainAdapter;
import com.joker.gankor.ui.BaseActivity;
import com.joker.gankor.ui.fragment.GankFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private Toolbar mTitleToolbar;
    private TabLayout mTitleTabLayout;
    private NavigationView mContentNavigationView;
    private ViewPager mContentViewPager;
    private SwipeRefreshLayout mContentSwipeRefreshLayout;
    private List<Fragment> mFragments;
    private List<Integer> mLayouts;
    private List<String> mTitles;
    private DrawerLayout mMainDrawerLayout;
    private AppBarLayout mTitleAppBarLayout;
    private long firstTime;

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
        mTitleToolbar.setTitle("干乎");
        setSupportActionBar(mTitleToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mMainDrawerLayout,
                mTitleToolbar, R.string.app_name, R.string
                .app_name);
        mMainDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        mFragments = new ArrayList<Fragment>();
        GankFragment gankFragment = new GankFragment();
        gankFragment.setArguments(getIntent().getExtras());
        mFragments.add(gankFragment);
//        mFragments.add(new GankFragment());
        mLayouts = new ArrayList<Integer>();
        mLayouts.add(R.layout.fragment_gank);
//        mLayouts.add(R.layout.fragment_gank);
        mTitles = new ArrayList<String>();
        mTitles.add("妹纸");
//        mTitles.add("知乎");

        mContentViewPager.setAdapter(new MainAdapter(getSupportFragmentManager(), mFragments, mTitles));
        mTitleTabLayout.setupWithViewPager(mContentViewPager);
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
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Snackbar sb = Snackbar.make(mContentNavigationView, "再按一次退出", Snackbar.LENGTH_SHORT);
            sb.getView().setBackgroundColor(getResources().getColor(R.color.primary));
            sb.show();
            firstTime = secondTime;
        } else {
            finish();
        }

    }
}
