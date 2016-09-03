package com.joker.gankor.ui.fragment;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joker.gankor.adapter.HotNewsRecyclerAdapter;
import com.joker.gankor.model.ZhihuHotNews;
import com.joker.gankor.utils.API;
import com.joker.gankor.utils.LazyUtil;
import com.joker.gankor.utils.OkUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZhihuHotNewsFragment extends ContentFragment implements SwipeRefreshLayout.OnRefreshListener,
        HotNewsRecyclerAdapter.OnHotItemClickListener {
    public final static String HOT_NEWS_JSON = "hot_news_json";
    public HotNewsRecyclerAdapter mAdapter;
    private HotNewsRecyclerAdapter.OnHotItemClickListener mItemListener;

    public ZhihuHotNewsFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getFirstPageUrl() {
        return "";
    }

    @Override
    protected void initView(LayoutInflater inflater, ViewGroup container) {
        List<ZhihuHotNews.RecentBean> mRecent = new ArrayList<ZhihuHotNews.RecentBean>();
        mContentRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new HotNewsRecyclerAdapter(mActivity, mRecent);
        mAdapter.setOnHotItemClickListener(this);
        mContentRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();

        if (!mCache.isCacheEmpty(HOT_NEWS_JSON)) {
            mAdapter.changeListData(mGson.fromJson(mCache.getAsString(HOT_NEWS_JSON), ZhihuHotNews
                    .class).getRecent());
        } else {
            if (isNetConnect()) {
                mContentSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mContentSwipeRefreshLayout.setRefreshing(true);
                    }
                });
                loadDataFromNet("");
            } else {
                LazyUtil.showToast("网络没有连接哦");
            }
        }
    }

    @Override
    public void loadDataFromNet(String url) {
        //        获取知乎热门消息
        mOkUtil.okHttpZhihuGson(API.ZHIHU_HOT_NEWS, new OkUtil.ResultCallback<ZhihuHotNews>() {
                    @Override
                    public void onError(Call call, Exception e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(ZhihuHotNews response, String json) {
                        if (response != null) {
                            if (mCache.isNewResponse(HOT_NEWS_JSON, json)) {
                                mCache.put(HOT_NEWS_JSON, json);
                                mAdapter.changeListData(response.getRecent());
                            }
                        }
                        mContentSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    @Override
    public void onZhihuHotItemClick(View view, ZhihuHotNews.RecentBean bean) {
        if (mItemListener != null) {
            mItemListener.onZhihuHotItemClick(view, bean);
        }
    }

    public void setOnItemClickListener(HotNewsRecyclerAdapter.OnHotItemClickListener itemClickListener) {
        mItemListener = itemClickListener;
    }
}
