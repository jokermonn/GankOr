package com.joker.gankor.ui.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.joker.gankor.R;
import com.joker.gankor.ui.BaseFragment;
import com.joker.gankor.utils.CacheUtil;
import com.joker.gankor.utils.LazyUtil;
import com.joker.gankor.utils.OkUtil;
import com.joker.gankor.view.PullLoadRecyclerView;
import com.joker.gankor.view.SpacesItemDecoration;

/**
 * 懒加载 fragment
 * A simple {@link Fragment} subclass.
 * Created by joker on 2016/8/8.
 */
public abstract class ContentFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    //    数据是否加载完毕
    protected boolean isDataLoaded = false;
    //    视图是否创建完毕
    protected boolean isViewCreated = false;
    protected OkUtil mOkUtil;
    protected CacheUtil mCache;
    protected Gson mGson;
    protected PullLoadRecyclerView mContentRecyclerView;
    protected SwipeRefreshLayout mContentSwipeRefreshLayout;
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x122:
                    mContentSwipeRefreshLayout.setRefreshing(false);
                    LazyUtil.showToast("网络没有连接哦");
                    break;
                case 0x121:
//                    下拉刷新
                    loadDataFromNet(getFirstPageUrl());
                default:
                    break;
            }
        }
    };

    public ContentFragment() {
        // Required empty public constructor
    }

    protected abstract String getFirstPageUrl();

    protected boolean isFirstPage(String url) {
        return getFirstPageUrl().equals(url);
    }

    /**
     * 1. 缓存为空时第一次加载缓存 或者刷新
     * 2. 上拉加载更多
     *
     * @param url 前者使用 getFirstPageUrl() 后者需自己传入 是固定值
     */
    protected abstract void loadDataFromNet(String url);

    @Nullable
    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        mContentRecyclerView = (PullLoadRecyclerView) view.findViewById(R.id.rv_content);
        mContentSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_content);
        SpacesItemDecoration decoration = new SpacesItemDecoration((int) (Math.random() * 5 + 15));
        mContentRecyclerView.addItemDecoration(decoration);

        initView(inflater, container);
        initSwipeRefreshLayout();

        isViewCreated = true;

        return view;
    }

    protected abstract void initView(LayoutInflater inflater, ViewGroup container);

    protected void initSwipeRefreshLayout() {
        mContentSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android
                .R.color.holo_orange_light, android.R.color.holo_green_light, android.R.color
                .holo_red_dark);
        mContentSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @CallSuper
    protected void initData() {
        isDataLoaded = true;
        mOkUtil = OkUtil.getInstance();
        mCache = CacheUtil.getInstance(mActivity);
        mGson = new Gson();
    }

    @CallSuper
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && !isDataLoaded && isViewCreated) {
//            ViewPager 其他页面的 fragment，我们进行判断后再加载数据
            initData();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //          对于第一个直接呈现在用户面前的 fragment， 我们需要加载数据
        if (getUserVisibleHint()) {
            initData();
        }
    }

    public boolean isNetConnect() {
        return mActivity.isNetConnect();
    }

    @Override
    public void onRefresh() {
        if (isNetConnect()) {
            mHandler.sendEmptyMessage(0x121);
        } else {
            mHandler.sendEmptyMessage(0x122);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        LazyUtil.log(getClass().getName(), "    onStop");
        OkUtil.getInstance().cancelAll(mOkUtil);
        if (mContentSwipeRefreshLayout.isRefreshing()) {
            mContentSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LazyUtil.log(getClass().getName() + "    onDestroy");
//        RefWatcher refWatcher = GankOrApplication.getRefWatcher(mActivity.getApplicationContext());
//        refWatcher.watch(this);
        mActivity = null;
    }
}