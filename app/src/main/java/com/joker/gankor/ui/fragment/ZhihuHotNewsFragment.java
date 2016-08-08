package com.joker.gankor.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joker.gankor.R;
import com.joker.gankor.ui.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZhihuHotNewsFragment extends BaseFragment {
    public ZhihuHotNewsFragment() {
        // Required empty public constructor
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_zhihu_hot_news, container, false);
    }

    @Override
    protected void initData() {
        super.initData();
    }
}
