package com.joker.gankor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.joker.gankor.R;
import com.joker.gankor.model.ZhihuDailyNews;
import com.joker.gankor.utils.ImageUtil;

/**
 * Created by joker on 2016/8/8.
 */
public class ZhihuTopNewsHolderView implements Holder<ZhihuDailyNews.TopStoriesBean> {
    private ImageView mHeaderImageView;
    private TextView mHeaderTextView;

    @Override
    public View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.daily_news_header_item,
                null);
        mHeaderImageView = (ImageView) view.findViewById(R.id.iv_header);
        mHeaderTextView = (TextView) view.findViewById(R.id.tv_header);
        mHeaderImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, ZhihuDailyNews.TopStoriesBean bean) {
        ImageUtil.getInstance().displayImage(bean.getImage(), mHeaderImageView);
        mHeaderTextView.setText(bean.getTitle());
    }
}
