package com.joker.gankor.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.joker.gankor.R;
import com.joker.gankor.adapter.DailyNewsRecyclerAdapter;
import com.joker.gankor.model.ZhihuDailyNews;
import com.joker.gankor.ui.BaseFragment;
import com.joker.gankor.ui.activity.MainActivity;
import com.joker.gankor.utils.API;
import com.joker.gankor.utils.OkUtil;
import com.joker.gankor.view.ZhihuTopNewsHolderView;

import java.util.List;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZhihuDailyNewsFragment extends BaseFragment implements com.bigkoo.convenientbanner.listener
        .OnItemClickListener,
        DailyNewsRecyclerAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    public final static String DAILY_NEWS_JSON = "daily_news_json";
    private ConvenientBanner mShowConvenientBanner;
    private DailyNewsRecyclerAdapter.OnItemClickListener mItemListener;
    private List<ZhihuDailyNews.TopStoriesBean> mTopStories;
    private List<ZhihuDailyNews.StoriesBean> mNewsStories;
    private OnBannerClickListener mBannerListener;

    public ZhihuDailyNewsFragment() {
        // Required empty public constructor
    }

    @Override
    protected void initRecyclerView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        View header = inflater.inflate(R.layout.daily_news_header_view, mContentRecyclerView, false);
        mShowConvenientBanner = (ConvenientBanner) header.findViewById(R.id.cb_show);
    }

    @Override
    protected void initToolbar() {
        ((MainActivity) mActivity).hideTabLayout(false);
        ((MainActivity) mActivity).setToolbarScroll(false);
        ((MainActivity) mActivity).setToolbarTitle("知乎");
    }

    @Override
    protected void initData() {
        super.initData();

        if (!mCache.isCacheEmpty(DAILY_NEWS_JSON)) {
            ZhihuDailyNews dailyNews = mGson.fromJson(mCache.getAsString(DAILY_NEWS_JSON), ZhihuDailyNews
                    .class);
            mTopStories = dailyNews.getTopStories();
            mNewsStories = dailyNews.getStories();
            initBanner();
            loadRecyclerView();
        } else {
            if (isNetConnect()) {
                loadLatestData();
            }
        }
    }

    @Override
    public void loadLatestData() {
        //        获取知乎最新消息
        mOkUtil.okHttpZhihuGson(API.ZHIHU_NEWS_FOUR + API.ZHIHU_LATEST, new OkUtil
                .ResultCallback<ZhihuDailyNews>() {
            @Override
            public void onError(Call call, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(ZhihuDailyNews response, String json) {
                if (response != null && (mCache.isNewResponse(DAILY_NEWS_JSON, json) ||
                        mCache.isCacheEmpty(DAILY_NEWS_JSON))) {
//                知乎头条消息
                    mTopStories = response.getTopStories();
//                最新消息
                    mNewsStories = response.getStories();
                    mCache.put(DAILY_NEWS_JSON, json);
                    initBanner();
                    loadRecyclerView();
                }
            }
        });
    }

    @Override
    public void loadRecyclerView() {
        DailyNewsRecyclerAdapter mAdapter = new DailyNewsRecyclerAdapter(mActivity, mNewsStories);
        mAdapter.setHeaderView(mShowConvenientBanner);
        mAdapter.setOnItemClickListener(this);
        mContentRecyclerView.setAdapter(mAdapter);
    }

    private void initBanner() {
        mShowConvenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new ZhihuTopNewsHolderView();
            }
        }, mTopStories)
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                .setPageIndicator(new int[]{R.drawable.indicator_gray, R.drawable.indicator_red})
                .setOnItemClickListener(this);
    }

    // 开始自动翻页
    @Override
    public void onResume() {
        super.onResume();
        mShowConvenientBanner.startTurning(4000);
    }

    // 暂停自动翻页
    @Override
    public void onPause() {
        super.onPause();
        mShowConvenientBanner.stopTurning();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isDataLoaded && isViewCreated) {
            mShowConvenientBanner.startTurning(4000);
        }
        if (!isVisibleToUser && isViewCreated) {
            mShowConvenientBanner.stopTurning();
        }
    }

    //    Banner 点击事件
    @Override
    public void onItemClick(int position) {
        if (mBannerListener != null) {
            mBannerListener.onBannerClickListener(mTopStories.get(position));
        }
    }

    public void setOnItemClickListener(DailyNewsRecyclerAdapter.OnItemClickListener itemClickListener) {
        mItemListener = itemClickListener;
    }

    public void setOnBannerClickListener(OnBannerClickListener bannerClickListener) {
        mBannerListener = bannerClickListener;
    }

    @Override
    public void onZhihuItemClick(ZhihuDailyNews.StoriesBean bean) {
        if (mItemListener != null) {
            mItemListener.onZhihuItemClick(bean);
        }
    }

    public interface OnBannerClickListener {
        void onBannerClickListener(ZhihuDailyNews.TopStoriesBean topStories);
    }
}
