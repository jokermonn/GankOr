package com.joker.gankor.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
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
public class ZhihuDailyNewsFragment extends BaseFragment implements OnItemClickListener,
        DailyNewsRecyclerAdapter.ItemClickListener {
    private ConvenientBanner mShowConvenientBanner;
    private DailyNewsRecyclerAdapter.ItemClickListener mListener;
    private List<ZhihuDailyNews.TopStoriesBean> mTopStories;
    private List<ZhihuDailyNews.StoriesBean> mNewsStories;
    private RecyclerView mDailyNewsRecyclerView;
    private SwipeRefreshLayout mContentSwipeRefreshLayout;
    private DailyNewsRecyclerAdapter mAdapter;

    public ZhihuDailyNewsFragment() {
        // Required empty public constructor
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhihu_daily_news, container, false);
        mDailyNewsRecyclerView = (RecyclerView) view.findViewById(R.id.rv_daily_news);
        mDailyNewsRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mContentSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_content);
        View header = inflater.inflate(R.layout.daily_news_header_view, mDailyNewsRecyclerView, false);
        mShowConvenientBanner = (ConvenientBanner) header.findViewById(R.id.cb_show);
        ((MainActivity) mActivity).hideTabLayout(false);
        ((MainActivity) mActivity).setToolbarScroll(false);
        ((MainActivity) mActivity).setToolbarTitle("知乎");

        return view;
    }

    @Override
    protected void initData() {
        super.initData();

//        获取知乎最新消息
        OkUtil.getInstance().okHttpZhihuGson(API.ZHIHU_NEWS_LATEST, new OkUtil
                .ResultCallback<ZhihuDailyNews>() {
            @Override
            public void onError(Call call, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(ZhihuDailyNews response, String json) {
//                知乎头条消息
                mTopStories = response.getTopStories();
//                最新消息
                mNewsStories = response.getStories();
                initBanner();
                initRecyclerView();
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new DailyNewsRecyclerAdapter(mActivity, mNewsStories);
        mAdapter.setHeaderView(mShowConvenientBanner);
        mAdapter.setItemClickListener(this);
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

    }

    public void setItemClickListener(DailyNewsRecyclerAdapter.ItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(ZhihuDailyNews.StoriesBean bean) {
        if (mListener != null) {
            mListener.onClick(bean);
        }
    }
}
