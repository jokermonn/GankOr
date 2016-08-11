package com.joker.gankor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joker.gankor.R;
import com.joker.gankor.model.ZhihuDailyNews;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by joker on 2016/8/8.
 */
public class DailyNewsRecyclerAdapter extends RecyclerView.Adapter<DailyNewsRecyclerAdapter.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private Context mContext;
    private List<ZhihuDailyNews.StoriesBean> mBean;
    private LayoutInflater mInflater;
    private OnItemClickListener mListener;
    private View mHeaderView;

    public DailyNewsRecyclerAdapter(Context context, List<ZhihuDailyNews.StoriesBean> storiesBeen) {
        mContext = context;
        mBean = storiesBeen;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new ViewHolder(mHeaderView);
        }
        return new ViewHolder(mInflater.inflate(R.layout.daily_news_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            return;
        }
        ImageLoader.getInstance().displayImage(mBean.get(position).getImages().get(0), holder.mImageView);
        holder.mTextView.setText(mBean.get(position).getTitle());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mBean.size();
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(ZhihuDailyNews.StoriesBean bean);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTextView;
        ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView) {
                return;
            }
            mTextView = (TextView) itemView.findViewById(R.id.tv_item);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onClick(mBean.get(getAdapterPosition()));
            }
        }
    }
}
