package com.joker.gankor.ui;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.joker.gankor.R;
import com.joker.gankor.ui.activity.MainActivity;
import com.joker.gankor.utils.CacheUtil;
import com.joker.gankor.utils.LazyUtil;
import com.joker.gankor.utils.OkUtil;
import com.joker.gankor.view.SpacesItemDecoration;

/**
 * 懒加载 fragment
 * A simple {@link Fragment} subclass.
 * Created by joker on 2016/8/8.
 */
public abstract class BaseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    protected BaseActivity mActivity;
    //    数据是否加载完毕
    protected boolean isDataLoaded = false;
    //    视图是否创建完毕
    protected boolean isViewCreated = false;
    protected OkUtil mOkUtil;
    protected CacheUtil mCache;
    protected Gson mGson;
    protected RecyclerView mContentRecyclerView;
    protected SwipeRefreshLayout mContentSwipeRefreshLayout;
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    loadLatestData();
                    break;
                case 0x122:
                    LazyUtil.showToast(mActivity, "网络没有连接哦");
                    break;
                default:
                    break;
            }
            mContentSwipeRefreshLayout.setRefreshing(false);
        }
    };

    public BaseFragment() {
        // Required empty public constructor
    }

    protected abstract void loadLatestData();

    @Nullable
    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mActivity = (BaseActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_base, container, false);
        mContentRecyclerView = (RecyclerView) view.findViewById(R.id.rv_content);
        mContentSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_content);
        SpacesItemDecoration decoration = new SpacesItemDecoration((int) (Math.random() * 5 + 15));
        mContentRecyclerView.addItemDecoration(decoration);

        initRecyclerView(inflater, container, savedInstanceState);
        initToolbar();
        initSwipeRefreshLayout();

//          对于第一个直接呈现在用户面前的 fragment， 我们需要直接加载数据
        if (((MainActivity) mActivity).getItemId() == 0) {
            initData();
        }

        isViewCreated = true;

        return view;
    }

    protected abstract void initRecyclerView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState);

    protected void initSwipeRefreshLayout() {
        mContentSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android
                .R.color.holo_orange_light, android.R.color.holo_green_light, android.R.color
                .holo_red_dark);
        mContentSwipeRefreshLayout.setOnRefreshListener(this);
    }

    protected abstract void initToolbar();

    protected abstract void loadRecyclerView();

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

    public boolean isNetConnect() {
        return mActivity.isNetConnect();
    }

    @Override
    public void onRefresh() {
        if (mActivity.isNetConnect()) {
            mHandler.sendEmptyMessage(0x123);
        } else {
            mHandler.sendEmptyMessage(0x122);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LazyUtil.Log(getClass().getName() + "    onAttach");
    }

    @Override
    public void onStart() {
        super.onStart();
        LazyUtil.Log(getClass().getName() + "   onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        LazyUtil.Log(getClass().getName() + "   onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LazyUtil.Log(getClass().getName() + "    onStop");
        OkUtil.getInstance().cancelAll(mOkUtil);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LazyUtil.Log(getClass().getName() + "    onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LazyUtil.Log(getClass().getName() + "    onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LazyUtil.Log(getClass().getName() + "    onDetach");
    }
}
