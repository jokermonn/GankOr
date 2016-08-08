package com.joker.gankor.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.joker.gankor.R;
import com.joker.gankor.adapter.GankRecyclerViewAdapter;
import com.joker.gankor.model.GankWelfare;
import com.joker.gankor.ui.BaseFragment;
import com.joker.gankor.ui.activity.MainActivity;
import com.joker.gankor.utils.API;
import com.joker.gankor.utils.OkUtil;
import com.joker.gankor.view.SpacesItemDecoration;

import java.util.List;

import okhttp3.Request;

/**
 * A simple {@link Fragment} subclass.
 */
public class GankFragment extends BaseFragment implements GankRecyclerViewAdapter.TextViewListener,
        GankRecyclerViewAdapter.ImageViewListener {
    private RecyclerView mGankRecyclerView;
    private List<GankWelfare.ResultsBean> mWelfare;
    private List<GankWelfare.ResultsBean> mVideo;
    private GankRecyclerViewAdapter mAdapter;
    private int page = 1;

    public GankFragment() {
        // Required empty public constructor
    }

    @Override
    protected void initData() {
        //        Gank 福利图片
        OkUtil.getInstance().okHttpGankGson(API.GANK_WELFARE + page, new OkUtil
                .ResultCallback<GankWelfare>() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(GankWelfare response) {
                if (response != null && !response.isError()) {
                    mWelfare = response.getResults();
                    loadVideo();
                }
            }
        });
    }

    //        Gank 休息视频
    private void loadVideo() {
        OkUtil.getInstance().okHttpGankGson(API.GANK_VIDEO + page, new OkUtil.ResultCallback<GankWelfare>
                () {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(GankWelfare response) {
                if (response != null && !response.isError()) {
                    mVideo = response.getResults();
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

        mAdapter = new GankRecyclerViewAdapter(mGankRecyclerView
                .getContext(),
                mWelfare, mVideo);
        mAdapter.setImageListener(this);
        mAdapter.setTextListener(this);
        mGankRecyclerView.setAdapter(mAdapter);
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
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.tv_content:
                Toast.makeText(mActivity, mVideo.get(position).getUrl(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_content:
                Toast.makeText(mActivity, mWelfare.get(position).getDesc(), Toast.LENGTH_SHORT).show();
            default:
                break;
        }
    }
}
