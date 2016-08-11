package com.joker.gankor.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.joker.gankor.R;
import com.joker.gankor.adapter.GankRecyclerAdapter;
import com.joker.gankor.model.GankWelfare;
import com.joker.gankor.ui.BaseFragment;
import com.joker.gankor.ui.activity.MainActivity;
import com.joker.gankor.utils.API;
import com.joker.gankor.utils.CacheUtil;
import com.joker.gankor.utils.NetUtil;
import com.joker.gankor.utils.OkUtil;
import com.joker.gankor.view.SpacesItemDecoration;

import java.util.List;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 * Created by joker on 2016/8/8.
 */
public class GankFragment extends BaseFragment implements GankRecyclerAdapter.TextViewListener,
        GankRecyclerAdapter.ImageViewListener {
    public final static String GANK_WELFARE_JSON = "gank_welfare_json";
    public final static String GANK_VIDEO_JSON = "gank_video_json";
    private RecyclerView mGankRecyclerView;
    private List<GankWelfare.ResultsBean> mWelfare;
    private List<GankWelfare.ResultsBean> mVideo;
    private GankRecyclerAdapter.TextViewListener mTextListener;
    private GankRecyclerAdapter.ImageViewListener mImageListener;
    private GankRecyclerAdapter mAdapter;
    private int page = 1;
    private CacheUtil mCache;
    private Gson mGson;

    public GankFragment() {
        // Required empty public constructor
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gank, container, false);
        mGankRecyclerView = (RecyclerView) view.findViewById(R.id.rv_gank);
        ((MainActivity) mActivity).hideTabLayout(true);
        ((MainActivity) mActivity).setToolbarScroll(true);
        ((MainActivity) mActivity).setToolbarTitle("妹纸");

        return view;
    }

    @Override
    protected void initData() {
        super.initData();

        mCache = CacheUtil.get(mActivity);
//        有网络的情况下
        if (NetUtil.isNetConnected(mActivity)) {
            //        Gank 福利图片
            OkUtil.getInstance().okHttpGankGson(API.GANK_WELFARE + page, new OkUtil
                    .ResultCallback<GankWelfare>() {
                @Override
                public void onError(Call call, Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(GankWelfare response, String json) {
                    if (response != null && !response.isError()) {
                        mWelfare = response.getResults();
                        mCache.put(GANK_WELFARE_JSON, json);
                        loadVideo();
                    }
                }
            });
        } else {
            mGson = new Gson();
            mWelfare = mGson.fromJson(mCache.getAsString(GANK_WELFARE_JSON), GankWelfare
                    .class).getResults();
            mVideo = mGson.fromJson(mCache.getAsString(GANK_VIDEO_JSON), GankWelfare
                    .class).getResults();
            initRecyclerView();
        }
    }

    //        Gank 休息视频
    private void loadVideo() {
        OkUtil.getInstance().okHttpGankGson(API.GANK_VIDEO + page, new OkUtil.ResultCallback<GankWelfare>
                () {
            @Override
            public void onError(Call call, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(GankWelfare response, String json) {
                if (response != null && !response.isError()) {
                    mVideo = response.getResults();
                    mCache.put(GANK_VIDEO_JSON, json);
                    initRecyclerView();
                }
            }
        });
    }

    //    初始化 RecyclerView
    private void initRecyclerView() {
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        mGankRecyclerView.addItemDecoration(decoration);

        mGankRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager
                        .VERTICAL));

        mAdapter = new GankRecyclerAdapter(mGankRecyclerView
                .getContext(),
                mWelfare, mVideo);
        mAdapter.setImageListener(this);
        mAdapter.setTextListener(this);
        mGankRecyclerView.setAdapter(mAdapter);
    }

    public void setTextListener(GankRecyclerAdapter.TextViewListener textListener) {
        mTextListener = textListener;
    }

    public void setImageListener(GankRecyclerAdapter.ImageViewListener imageListener) {
        mImageListener = imageListener;
    }

    @Override
    public void onClick(View view, String url) {
        switch (view.getId()) {
            case R.id.tv_content:
                if (mTextListener != null) {
                    mTextListener.onClick(view, url);
                }
                break;
            case R.id.iv_content:
                if (mImageListener != null) {
                    mImageListener.onClick(view, url);
                }
                break;
            default:
                break;
        }
    }
}
