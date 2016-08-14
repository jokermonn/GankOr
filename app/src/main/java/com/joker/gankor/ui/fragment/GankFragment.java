package com.joker.gankor.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joker.gankor.adapter.GankRecyclerAdapter;
import com.joker.gankor.model.GankWelfare;
import com.joker.gankor.ui.BaseFragment;
import com.joker.gankor.ui.activity.MainActivity;
import com.joker.gankor.utils.API;
import com.joker.gankor.utils.NetUtil;
import com.joker.gankor.utils.OkUtil;

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
    private List<GankWelfare.ResultsBean> mWelfare;
    private List<GankWelfare.ResultsBean> mVideo;
    private GankRecyclerAdapter.TextViewListener mTextListener;
    private GankRecyclerAdapter.ImageViewListener mImageListener;
    private int page = 1;

    public GankFragment() {
        // Required empty public constructor
    }

    @Override
    protected void initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager
                        .VERTICAL));
    }

    @Override
    protected void initToolbar() {
        ((MainActivity) mActivity).hideTabLayout(true);
        ((MainActivity) mActivity).setToolbarScroll(true);
        ((MainActivity) mActivity).setToolbarTitle("妹纸");
    }

    @Override
    protected void initData() {
        super.initData();

//        缓存不为空时直接加载缓存，否则在联网情况下加载数据
        if (!mCache.isCacheEmpty(GANK_WELFARE_JSON) && !mCache.isCacheEmpty(GANK_VIDEO_JSON)) {
            mWelfare = mGson.fromJson(mCache.getAsString(GANK_WELFARE_JSON), GankWelfare
                    .class).getResults();
            mVideo = mGson.fromJson(mCache.getAsString(GANK_VIDEO_JSON), GankWelfare
                    .class).getResults();
            initRecyclerView();
        } else {
            if (NetUtil.isNetConnected(mActivity)) {
                loadLatestData();
            }
        }
    }

    @Override
    public void loadLatestData() {
        //        Gank 福利图片
        mOkUtil.okHttpGankGson(API.GANK_WELFARE + page, new OkUtil
                .ResultCallback<GankWelfare>() {
            @Override
            public void onError(Call call, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(GankWelfare response, String json) {
                if ((mCache.isNewResponse(GANK_WELFARE_JSON, json) || mCache.isCacheEmpty
                        (GANK_WELFARE_JSON)) &&
                        response != null &&
                        !response.isError()) {
                    mWelfare = response.getResults();
                    mCache.put(GANK_WELFARE_JSON, json);
                    loadVideo();
                }
            }
        });
    }

    //        Gank 休息视频
    private void loadVideo() {
        mOkUtil.okHttpGankGson(API.GANK_VIDEO + page, new OkUtil.ResultCallback<GankWelfare>
                () {
            @Override
            public void onError(Call call, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(GankWelfare response, String json) {
                if (response != null && !response.isError() && (mCache.isNewResponse(
                        GANK_VIDEO_JSON, json) ||
                        mCache.isCacheEmpty
                                (GANK_VIDEO_JSON))) {
                    mVideo = response.getResults();
                    mCache.put(GANK_VIDEO_JSON, json);
                    initRecyclerView();
                }
            }
        });
    }

    //    初始化 RecyclerView
    @Override
    public void initRecyclerView() {
        GankRecyclerAdapter mAdapter = new GankRecyclerAdapter(mContentRecyclerView
                .getContext(),
                mWelfare, mVideo);
        mAdapter.setImageListener(this);
        mAdapter.setTextListener(this);
        mContentRecyclerView.setAdapter(mAdapter);
    }

    public void setTextListener(GankRecyclerAdapter.TextViewListener textListener) {
        mTextListener = textListener;
    }

    public void setImageListener(GankRecyclerAdapter.ImageViewListener imageListener) {
        mImageListener = imageListener;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mContentSwipeRefreshLayout.isRefreshing()) {
            mContentSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onGankImageClick(View image, String url, String desc) {
        if (mImageListener != null) {
            mImageListener.onGankImageClick(image, url, desc);
        }
    }

    @Override
    public void onGankTextClick(String url) {
        if (mTextListener != null) {
            mTextListener.onGankTextClick(url);
        }
    }
}
