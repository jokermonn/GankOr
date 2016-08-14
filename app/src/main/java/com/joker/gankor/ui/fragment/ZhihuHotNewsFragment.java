package com.joker.gankor.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.joker.gankor.adapter.HotNewsRecyclerAdapter;
import com.joker.gankor.model.ZhihuHotNews;
import com.joker.gankor.ui.BaseFragment;
import com.joker.gankor.utils.API;
import com.joker.gankor.utils.LazyUtil;
import com.joker.gankor.utils.NetUtil;
import com.joker.gankor.utils.OkUtil;

import java.util.List;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZhihuHotNewsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        HotNewsRecyclerAdapter.OnItemClickListener {
    public final static String HOT_NEWS_JSON = "hot_news_json";
    private List<ZhihuHotNews.RecentBean> mRecent;
    private HotNewsRecyclerAdapter.OnItemClickListener mItemListener;

    public ZhihuHotNewsFragment() {
        // Required empty public constructor
    }

    @Override
    protected void initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    @Override
    protected void initToolbar() {
    }

    @Override
    protected void initData() {
        super.initData();

        if (!mCache.isCacheEmpty(HOT_NEWS_JSON)) {
            ZhihuHotNews hotNews = mGson.fromJson(mCache.getAsString(HOT_NEWS_JSON), ZhihuHotNews
                    .class);
            mRecent = hotNews.getRecent();
            initRecyclerView();
        } else {
            if (NetUtil.isNetConnected(mActivity)) {
                loadLatestData();
            }
        }
    }

    @Override
    public void loadLatestData() {
        //        获取知乎热门消息
        mOkUtil.okHttpZhihuGson(API.ZHIHU_HOT_NEWS, new OkUtil.ResultCallback<ZhihuHotNews>() {
            @Override
            public void onError(Call call, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(ZhihuHotNews response, String json) {
                if (response != null && (mCache.isNewResponse(HOT_NEWS_JSON, json) || mCache.isCacheEmpty
                        (HOT_NEWS_JSON))) {
                    mRecent = response.getRecent();
                    mCache.put(HOT_NEWS_JSON, json);
                    initRecyclerView();
                }
            }
        });
    }

    @Override
    public void initRecyclerView() {
        mContentRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        HotNewsRecyclerAdapter mAdapter = new HotNewsRecyclerAdapter(mActivity, mRecent);
        mAdapter.setOnItemClickListener(this);
        mContentRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onZhihuItemClick(ZhihuHotNews.RecentBean bean) {
        if (mItemListener != null) {
            mItemListener.onZhihuItemClick(bean);
        }
    }

    public void setOnItemClickListener(HotNewsRecyclerAdapter.OnItemClickListener itemClickListener) {
        mItemListener = itemClickListener;
    }
}
