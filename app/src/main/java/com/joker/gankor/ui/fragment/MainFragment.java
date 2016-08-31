package com.joker.gankor.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joker.gankor.R;
import com.joker.gankor.adapter.DailyNewsRecyclerAdapter;
import com.joker.gankor.adapter.GankRecyclerAdapter;
import com.joker.gankor.adapter.HotNewsRecyclerAdapter;
import com.joker.gankor.adapter.MainAdapter;
import com.joker.gankor.model.GankWelfare;
import com.joker.gankor.model.ZhihuDailyNews;
import com.joker.gankor.model.ZhihuHotNews;
import com.joker.gankor.ui.BaseFragment;
import com.joker.gankor.ui.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment implements GankRecyclerAdapter.ImageViewListener,
        GankRecyclerAdapter.TextViewListener, DailyNewsRecyclerAdapter.OnDailyItemClickListener,
        ZhihuDailyNewsFragment.OnBannerClickListener, HotNewsRecyclerAdapter.OnHotItemClickListener {
    public final static String MENU_GANK = "menu_gank";
    public final static String MENU_ZHIHU = "menu_zhihu";
    public final static String MENU_ID = "menu_id";
    public GankFragment mGankFragment;
    public ZhihuDailyNewsFragment mDailyNewsFragment;
    public ZhihuHotNewsFragment mHotNewsFragment;
    private MainAdapter adapter;
    private ViewPager mContentViewPager;
    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();
    private GankRecyclerAdapter.TextViewListener mTextListener;
    private GankRecyclerAdapter.ImageViewListener mImageListener;
    private DailyNewsRecyclerAdapter.OnDailyItemClickListener mDailyItemListener;
    private HotNewsRecyclerAdapter.OnHotItemClickListener mHotItemListener;
    private ZhihuDailyNewsFragment.OnBannerClickListener mBannerListener;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String menuId) {
        Bundle args = new Bundle();
        args.putString(MENU_ID, menuId);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mContentViewPager = (ViewPager) view.findViewById(R.id.vp_content);
        adapter = new MainAdapter(getChildFragmentManager(), mFragments, mTitles);
        initFragments();

        return view;
    }

    public void initFragments() {
        String args = getArguments().getString(MENU_ID);

        if (MENU_GANK.equals(args)) {
            mGankFragment = new GankFragment();
            mGankFragment.setImageListener(this);
            mGankFragment.setTextListener(this);

            mFragments.add(mGankFragment);
            mTitles.add("妹纸");

            adapter.changeDataList(mTitles, mFragments);
        } else {
            mDailyNewsFragment = new ZhihuDailyNewsFragment();
            mDailyNewsFragment.setOnItemClickListener(this);
            mDailyNewsFragment.setOnBannerClickListener(this);
            mHotNewsFragment = new ZhihuHotNewsFragment();
            mHotNewsFragment.setOnItemClickListener(this);

            mFragments.add(mDailyNewsFragment);
            mFragments.add(mHotNewsFragment);
            mTitles.add("知乎日报");
            mTitles.add("热门消息");

            adapter.changeDataList(mTitles, mFragments);
        }
        mContentViewPager.setAdapter(adapter);
        ((MainActivity) mActivity).setupViewPager(mContentViewPager);
    }

    public void setTextListener(GankRecyclerAdapter.TextViewListener textListener) {
        mTextListener = textListener;
    }

    public void setImageListener(GankRecyclerAdapter.ImageViewListener imageListener) {
        mImageListener = imageListener;
    }

    @Override
    public void onGankImageClick(View image, List<GankWelfare.ResultsBean> bean, int position) {
        if (mImageListener != null) {
            mImageListener.onGankImageClick(image, bean, position);
        }
    }

    @Override
    public void onGankTextClick(String url) {
        if (mTextListener != null) {
            mTextListener.onGankTextClick(url);
        }
    }

    public void setOnDailyItemClickListener(DailyNewsRecyclerAdapter.OnDailyItemClickListener
                                                    itemClickListener) {
        mDailyItemListener = itemClickListener;
    }

    public void setOnBannerClickListener(ZhihuDailyNewsFragment.OnBannerClickListener bannerClickListener) {
        mBannerListener = bannerClickListener;
    }

    public void setOnHotItemClickListener(HotNewsRecyclerAdapter.OnHotItemClickListener itemClickListener) {
        mHotItemListener = itemClickListener;
    }

    @Override
    public void onBannerClickListener(ZhihuDailyNews.TopStoriesBean topStories) {
        if (mBannerListener != null) {
            mBannerListener.onBannerClickListener(topStories);
        }
    }

    @Override
    public void onZhihuDailyItemClick(View view, ZhihuDailyNews.StoriesBean bean) {
        if (mDailyItemListener != null) {
            mDailyItemListener.onZhihuDailyItemClick(view, bean);
        }
    }

    @Override
    public void onZhihuHotItemClick(View view, ZhihuHotNews.RecentBean bean) {
        if (mDailyItemListener != null) {
            mHotItemListener.onZhihuHotItemClick(view, bean);
        }
    }
}
