package com.joker.gankor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joker.gankor.R;
import com.joker.gankor.model.ZhihuDailyNews;
import com.joker.gankor.utils.ImageUtil;

import java.util.List;

/**
 * Created by joker on 2016/8/8.
 */
public class DailyNewsRecyclerAdapter extends BaseAdapter {
    private Context mContext;
    private List<ZhihuDailyNews.StoriesBean> mBean;
    private LayoutInflater mInflater;

    public DailyNewsRecyclerAdapter(Context context, List<ZhihuDailyNews.StoriesBean> storiesBeen) {
        mContext = context;
        mBean = storiesBeen;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mBean.size();
    }

    @Override
    public Object getItem(int position) {
        return mBean.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.daily_news_item, parent, false);
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.tv_item);
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.iv_item);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        System.out.println(mBean.get(position).getImages().get(0));
        ImageUtil.getInstance().displayImage(mBean.get(position).getImages().get(0), viewHolder.mImageView);
        viewHolder.mTextView.setText(mBean.get(position).getTitle());

        return convertView;
    }

    static class ViewHolder {
        TextView mTextView;
        ImageView mImageView;
    }
}
