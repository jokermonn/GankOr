package com.joker.gankor.ui.fragment;


import android.support.v4.app.Fragment;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.joker.gankor.adapter.GankRecyclerAdapter;
import com.joker.gankor.model.GankWelfare;
import com.joker.gankor.ui.BaseFragment;
import com.joker.gankor.ui.activity.MainActivity;
import com.joker.gankor.utils.API;
import com.joker.gankor.utils.LazyUtil;
import com.joker.gankor.utils.OkUtil;
import com.joker.gankor.view.PullLoadRecyclerView;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
    public GankRecyclerAdapter mAdapter;
    public StaggeredGridLayoutManager manager;
    private List<GankWelfare.ResultsBean> mWelfare;
    private HashMap<GankWelfare.ResultsBean, GankWelfare.ResultsBean> dataMap;
    private GankRecyclerAdapter.TextViewListener mTextListener;
    private GankRecyclerAdapter.ImageViewListener mImageListener;
    private int page = 1;

    public GankFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getUrl() {
        return String.valueOf(page);
    }

    @Override
    protected void initRecyclerView() {
        dataMap = new LinkedHashMap<GankWelfare.ResultsBean, GankWelfare.ResultsBean>();
        manager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager
                        .VERTICAL);
        mContentRecyclerView.setLayoutManager(manager);
        mAdapter = new GankRecyclerAdapter(mContentRecyclerView
                .getContext(), dataMap);
        mAdapter.setImageListener(this);
        mAdapter.setTextListener(this);
        mContentRecyclerView.setAdapter(mAdapter);
        mContentRecyclerView.setPullLoadListener(new PullLoadRecyclerView.onPullLoadListener() {
            @Override
            public void onPullLoad() {
                loadDataFromNet(String.valueOf(++page), false);
            }
        });
    }


    @Override
    protected void initData() {
        super.initData();

//        缓存不为空时直接加载缓存，否则在联网情况下加载数据
        if (!mCache.isCacheEmpty(GANK_WELFARE_JSON) && !mCache.isCacheEmpty(GANK_VIDEO_JSON)) {
//            取出缓存
            List<GankWelfare.ResultsBean> welfare = mGson.fromJson(mCache.getAsString(GANK_WELFARE_JSON),
                    GankWelfare
                            .class).getResults();
            List<GankWelfare.ResultsBean> video = mGson.fromJson(mCache.getAsString(GANK_VIDEO_JSON),
                    GankWelfare
                            .class).getResults();

            for (int i = 0; i < welfare.size(); i++) {
                dataMap.put(welfare.get(i), video.get(i));
            }
            mAdapter.addDataMap(dataMap);
        } else {
            if (isNetConnect()) {
                loadDataFromNet(String.valueOf(page), true);
            } else {
                LazyUtil.showToast(mActivity, "网络没有连接哦");
            }
        }
    }

    @Override
    public void loadDataFromNet(final String url, final boolean isSaveCache) {
        if (!isSaveCache) {
            mContentSwipeRefreshLayout.setRefreshing(true);
        }
        //        Gank 福利图片
        mOkUtil.okHttpGankGson(API.GANK_WELFARE + url, new OkUtil
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
                    if (isSaveCache) {
                        mCache.put(GANK_WELFARE_JSON, json);
                    }
                    mWelfare = response.getResults();
                    loadGankVideo(url, isSaveCache);
                }
            }
        });
    }

    private void loadGankVideo(String url, final boolean isSaveCache) {
        //        Gank 休息视频
        mOkUtil.okHttpGankGson(API.GANK_VIDEO + url, new OkUtil.ResultCallback<GankWelfare>
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
                    List<GankWelfare.ResultsBean> video = response.getResults();

                    dataMap.clear();
                    for (int i = 0; i < video.size(); i++) {
                        dataMap.put(mWelfare.get(i), video.get(i));
                    }
                    mAdapter.addDataMap(dataMap);

                    if (isSaveCache) {
                        mCache.put(GANK_VIDEO_JSON, json);
                    } else {
                        mContentSwipeRefreshLayout.setRefreshing(false);
                        mContentRecyclerView.setIsLoading(false);
                    }
                }
            }
        });
    }

    @Override
    protected void initToolbar() {
        ((MainActivity) mActivity).hideTabLayout(true);
        ((MainActivity) mActivity).setToolbarTitle("妹纸");
    }

    public void setTextListener(GankRecyclerAdapter.TextViewListener textListener) {
        mTextListener = textListener;
    }

    public void setImageListener(GankRecyclerAdapter.ImageViewListener imageListener) {
        mImageListener = imageListener;
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