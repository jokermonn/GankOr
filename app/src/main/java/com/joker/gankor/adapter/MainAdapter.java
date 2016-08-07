package com.joker.gankor.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by joker on 2016/8/4.
 */
public class MainAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;
    private List<String> mTitles;

    public MainAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        mFragments = fragments;
//        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    /*@Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }*/
}
