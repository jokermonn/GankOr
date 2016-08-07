package com.joker.gankor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joker.gankor.R;
import com.joker.gankor.model.GankWelfare;
import com.joker.gankor.utils.ImageUtil;

import java.util.List;

/**
 * Created by joker on 2016/8/5.
 */
public class GankRecyclerViewAdapter extends RecyclerView.Adapter<GankRecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private List<GankWelfare.ResultsBean> mWelfare;
    private List<GankWelfare.ResultsBean> mVideo;
    private ImageUtil mImageUtil;
    private TextViewListener mTextListener;
    private ImageViewListener mImageListener;


    public GankRecyclerViewAdapter(Context context, List<GankWelfare.ResultsBean> welfare,
                                   List<GankWelfare.ResultsBean> video) {
        mContext = context;
        mWelfare = welfare;
        mVideo = video;
        mImageUtil = ImageUtil.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.fragment_gank_rvitem,
                parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        mImageUtil.displayImage(mWelfare.get(position).getUrl(), holder.imageView);
        holder.textView.setText(mVideo.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        return mWelfare.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_content);
            imageView = (ImageView) itemView.findViewById(R.id.iv_content);
            textView.setOnClickListener(this);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_content:
                    if (mTextListener != null) {
                        mTextListener.onClick(v, getAdapterPosition());
                    }
                    break;
                case R.id.iv_content:
                    if (mImageListener != null) {
                        mImageListener.onClick(v, getAdapterPosition());
                    }
                default:
                    break;
            }
        }
    }

    public void setTextListener(TextViewListener listener) {
        this.mTextListener = listener;
    }

    public void setImageListener(ImageViewListener listener) {
        this.mImageListener = listener;
    }

    public interface TextViewListener {
        void onClick(View view, int position);
    }

    public interface ImageViewListener {
        void onClick(View view, int position);
    }
}