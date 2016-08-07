package com.joker.gankor.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {
    protected Activity mActivity;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        initData();
        return initView(inflater, container, savedInstanceState);
    }

    protected void initData() {}

    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState);
}
