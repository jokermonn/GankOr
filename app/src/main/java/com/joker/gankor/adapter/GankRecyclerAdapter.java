package com.joker.gankor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joker.gankor.R;
import com.joker.gankor.model.GankWelfare;
import com.joker.gankor.utils.ImageUtil;
import com.joker.gankor.view.RatioImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joker on 2016/8/5.
 */
public class GankRecyclerAdapter extends RecyclerView.Adapter<GankRecyclerAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<GankWelfare.ResultsBean> mWelfare;
    private List<GankWelfare.ResultsBean> mVideo;
    private TextViewListener mTextListener;
    private ImageViewListener mImageListener;

    public GankRecyclerAdapter(Context context, HashMap<GankWelfare
            .ResultsBean, GankWelfare.ResultsBean> map) {
        mWelfare = new ArrayList<GankWelfare.ResultsBean>();
        mVideo = new ArrayList<GankWelfare.ResultsBean>();
        getMapKV(map);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.fragment_gank_rvitem,
                parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ImageUtil.getInstance().displayImageOnLoading(mWelfare.get(position).getUrl(), holder
                .imageView);
        holder.textView.setText(mVideo.get(position).getDesc());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageListener != null) {
                    mImageListener.onGankImageClick(holder.imageView, mWelfare.get(position).getUrl(),
                            mWelfare.get(position).getDesc());
                }
            }
        });
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTextListener != null) {
                    mTextListener.onGankTextClick(mVideo.get(position).getUrl());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideo.size();
    }

    public void setTextListener(TextViewListener listener) {
        this.mTextListener = listener;
    }

    public void setImageListener(ImageViewListener listener) {
        this.mImageListener = listener;
    }

    public void addDataMap(HashMap<GankWelfare.ResultsBean, GankWelfare.ResultsBean> dataMap) {
        getMapKV(dataMap);
        notifyDataSetChanged();
    }

    public void clearList() {
        mVideo.clear();
        mWelfare.clear();
    }

    private void getMapKV(HashMap<GankWelfare.ResultsBean, GankWelfare.ResultsBean> map) {
        for (Map.Entry entry : map.entrySet()) {
            mWelfare.add((GankWelfare.ResultsBean) entry.getKey());
            mVideo.add((GankWelfare.ResultsBean) entry.getValue());
        }
    }

    public interface TextViewListener {
        void onGankTextClick(String url);
    }

    public interface ImageViewListener {
        void onGankImageClick(View image, String url, String desc);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RatioImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_content);
            imageView = (RatioImageView) itemView.findViewById(R.id.iv_content);
            imageView.setOriginalSize(50, 50);
        }
    }
}