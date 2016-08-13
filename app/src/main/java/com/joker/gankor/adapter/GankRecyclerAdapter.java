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

import java.util.List;

/**
 * Created by joker on 2016/8/5.
 */
public class GankRecyclerAdapter extends RecyclerView.Adapter<GankRecyclerAdapter.ViewHolder> {
    private Context mContext;
    private List<GankWelfare.ResultsBean> mWelfare;
    private List<GankWelfare.ResultsBean> mVideo;
    private ImageUtil mImageUtil;
    private TextViewListener mTextListener;
    private ImageViewListener mImageListener;
    private RecyclerView mRecyclerView;

    public GankRecyclerAdapter(Context context, List<GankWelfare.ResultsBean> welfare,
                               List<GankWelfare.ResultsBean> video) {
        mContext = context;
        mWelfare = welfare;
        mVideo = video;
        mImageUtil = ImageUtil.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mRecyclerView == null) {
            mRecyclerView= (RecyclerView) parent;
        }
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.fragment_gank_rvitem,
                parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mImageUtil.displayImageOnLoading(mWelfare.get(position).getUrl(), holder.imageView);
        holder.textView.setText(mVideo.get(position).getDesc());
//        mRecyclerView.findViewWithTag(mWelfare.get(position).getUrl());

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
        return mWelfare.size();
    }

    public void setTextListener(TextViewListener listener) {
        this.mTextListener = listener;
    }

    public void setImageListener(ImageViewListener listener) {
        this.mImageListener = listener;
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