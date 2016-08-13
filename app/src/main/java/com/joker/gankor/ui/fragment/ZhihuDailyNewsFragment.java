package com.joker.gankor.ui.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.joker.gankor.utils.LazyUtil;
import com.joker.gankor.utils.NetUtil;
import com.joker.gankor.utils.OkUtil;
import com.joker.gankor.view.SpacesItemDecoration;
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
    private RecyclerView mDailyNewsRecyclerView;
    private SwipeRefreshLayout mContentSwipeRefreshLayout;
    private DailyNewsRecyclerAdapter mAdapter;
    private OnBannerClickListener mBannerListener;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    loadLatestNews();
                    break;
                case 0x122:
                    LazyUtil.showToast(mActivity, "网络没有连接哦");
                    break;
                default:
                    break;
            }
            mContentSwipeRefreshLayout.setRefreshing(false);
        }
    };

    public ZhihuDailyNewsFragment() {
        // Required empty public constructor
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhihu_daily_news, container, false);
        mDailyNewsRecyclerView = (RecyclerView) view.findViewById(R.id.rv_daily_news);
        mDailyNewsRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        SpacesItemDecoration decoration = new SpacesItemDecoration(20);
        mDailyNewsRecyclerView.addItemDecoration(decoration);
        mContentSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_content);
        View header = inflater.inflate(R.layout.daily_news_header_view, mDailyNewsRecyclerView, false);
        mShowConvenientBanner = (ConvenientBanner) header.findViewById(R.id.cb_show);

        ((MainActivity) mActivity).hideTabLayout(false);
        ((MainActivity) mActivity).setToolbarScroll(false);
        ((MainActivity) mActivity).setToolbarTitle("知乎");

        initSwipeRefreshLayout();

        return view;
    }

    @Override
    protected void initData() {
        super.initData();

        if (!TextUtils.isEmpty(mCache.getAsString(DAILY_NEWS_JSON))) {
            ZhihuDailyNews dailyNews = mGson.fromJson(mCache.getAsString(DAILY_NEWS_JSON), ZhihuDailyNews
                    .class);
            mTopStories = dailyNews.getTopStories();
            mNewsStories = dailyNews.getStories();
            initBanner();
            initRecyclerView();
        } else {
            if (NetUtil.isNetConnected(mActivity)) {
                loadLatestNews();
            }
        }
    }

    private void loadLatestNews() {
        //        获取知乎最新消息
        mOkUtil.okHttpZhihuGson(API.ZHIHU_NEWS_LATEST, new OkUtil
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
                    initRecyclerView();
                }
            }
        });
    }

    private void initSwipeRefreshLayout() {
        mContentSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android
                .R.color.holo_orange_light, android.R.color.holo_green_light, android.R.color
                .holo_red_dark);
        mContentSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initRecyclerView() {
        mAdapter = new DailyNewsRecyclerAdapter(mActivity, mNewsStories);
        mAdapter.setHeaderView(mShowConvenientBanner);
        mAdapter.setOnItemClickListener(this);
        mDailyNewsRecyclerView.setAdapter(mAdapter);
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

    @Override
    public void onRefresh() {
        if (NetUtil.isNetConnected(mActivity)) {
            mHandler.sendEmptyMessage(0x123);
        } else {
            mHandler.sendEmptyMessage(0x122);
        }
    }

    public interface OnBannerClickListener {
        void onBannerClickListener(ZhihuDailyNews.TopStoriesBean topStories);
    }
}
