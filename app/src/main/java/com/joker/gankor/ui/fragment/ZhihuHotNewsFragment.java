package com.joker.gankor.ui.fragment;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.joker.gankor.adapter.HotNewsRecyclerAdapter;
import com.joker.gankor.model.ZhihuHotNews;
import com.joker.gankor.ui.BaseFragment;
import com.joker.gankor.utils.API;
import com.joker.gankor.utils.LazyUtil;
import com.joker.gankor.utils.OkUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZhihuHotNewsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        HotNewsRecyclerAdapter.OnItemClickListener {
    public final static String HOT_NEWS_JSON = "hot_news_json";
    public HotNewsRecyclerAdapter mAdapter;
    private List<ZhihuHotNews.RecentBean> mRecent;
    private HotNewsRecyclerAdapter.OnItemClickListener mItemListener;

    public ZhihuHotNewsFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getUrl() {
        return "";
    }

    @Override
    protected void initRecyclerView() {
        mRecent = new ArrayList<ZhihuHotNews.RecentBean>();
        mContentRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new HotNewsRecyclerAdapter(mActivity, mRecent);
        mAdapter.setOnItemClickListener(this);
        mContentRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initToolbar() {
    }

    @Override
    protected void initData() {
        super.initData();

        if (!mCache.isCacheEmpty(HOT_NEWS_JSON)) {
            mAdapter.addListData(mGson.fromJson(mCache.getAsString(HOT_NEWS_JSON), ZhihuHotNews
                    .class).getRecent());
        } else {
            if (isNetConnect()) {
                loadDataFromNet("", true);
            } else {
                LazyUtil.showToast(mActivity, "网络没有连接哦");
            }
        }
    }

    @Override
    public void loadDataFromNet(String url, boolean isSaveCache) {
        //        获取知乎热门消息
        mOkUtil.okHttpZhihuGson(API.ZHIHU_HOT_NEWS, new OkUtil.ResultCallback<ZhihuHotNews>() {
                    @Override
                    public void onError(Call call, Exception e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(ZhihuHotNews response, String json) {
                        if (response != null && (mCache.isNewResponse(HOT_NEWS_JSON, json) || mCache
                                .isCacheEmpty
                                        (HOT_NEWS_JSON))) {
                            mCache.put(HOT_NEWS_JSON, json);
                            mAdapter.addListData(response.getRecent());
                        }
                    }
                }
        );
    }

    @Override
    public void onZhihuItemClick(View view, ZhihuHotNews.RecentBean bean) {
        if (mItemListener != null) {
            mItemListener.onZhihuItemClick(view, bean);
        }
    }

    public void setOnItemClickListener(HotNewsRecyclerAdapter.OnItemClickListener itemClickListener) {
        mItemListener = itemClickListener;
    }
}
