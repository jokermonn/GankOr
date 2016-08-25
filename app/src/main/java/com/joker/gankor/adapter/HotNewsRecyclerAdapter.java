package com.joker.gankor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joker.gankor.R;
import com.joker.gankor.model.ZhihuHotNews;
import com.joker.gankor.utils.ImageUtil;

import java.util.List;

/**
 * Created by joker on 2016/8/14.
 */
public class HotNewsRecyclerAdapter extends RecyclerView.Adapter<HotNewsRecyclerAdapter.ViewHolder> {
    private List<ZhihuHotNews.RecentBean> mBean;
    private LayoutInflater mInflater;
    private OnHotItemClickListener mListener;

    public HotNewsRecyclerAdapter(Context context, List<ZhihuHotNews.RecentBean> bean) {
        mBean = bean;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public HotNewsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.zhihu_news_item, parent, false));
    }

    @Override
    public void onBindViewHolder(HotNewsRecyclerAdapter.ViewHolder holder, final int position) {
        ImageUtil.getInstance().displayImageOnLoading(mBean.get(position).getThumbnail(), holder
                .mImageView);
        holder.mTextView.setText(mBean.get(position).getTitle());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onZhihuHotItemClick(v, mBean.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBean.size();
    }

    public void changeListData(List<ZhihuHotNews.RecentBean> bean) {
        mBean = bean;
        notifyDataSetChanged();
    }

    public void setOnHotItemClickListener(OnHotItemClickListener listener) {
        mListener = listener;
    }

    public interface OnHotItemClickListener {
        void onZhihuHotItemClick(View view, ZhihuHotNews.RecentBean bean);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView mImageView;
        View item;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_item);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_item);
            item = itemView;
        }
    }
}
