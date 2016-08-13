package com.joker.gankor.ui;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.joker.gankor.utils.CacheUtil;
import com.joker.gankor.utils.LazyUtil;
import com.joker.gankor.utils.OkUtil;

/**
 * 懒加载 fragment
 * A simple {@link Fragment} subclass.
 * Created by joker on 2016/8/8.
 */
public abstract class BaseFragment extends Fragment {
    protected Activity mActivity;
    //    数据是否加载完毕
    protected boolean isDataLoaded = false;
    //    视图是否创建完毕
    protected boolean isViewCreated = false;
    protected OkUtil mOkUtil;
    protected CacheUtil mCache;
    protected Gson mGson;


    public BaseFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        isViewCreated = true;

        return initView(inflater, container, savedInstanceState);
    }

    @CallSuper
    protected void initData() {
        isDataLoaded = true;
        mOkUtil = OkUtil.getInstance();
        mCache = CacheUtil.getInstance(mActivity);
        mGson = new Gson();
    }

    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState);

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
