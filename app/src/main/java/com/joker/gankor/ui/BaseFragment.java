package com.joker.gankor.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 懒加载 fragment
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {
    protected Activity mActivity;
    //    数据是否加载完毕
    private boolean isDataLoaded = false;
    //    视图是否创建完毕
    private boolean isViewCreated = false;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        isViewCreated = true;

        return initView(inflater, container, savedInstanceState);
    }

    protected void initData() {
        isDataLoaded = true;
    }

    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState);

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
}
