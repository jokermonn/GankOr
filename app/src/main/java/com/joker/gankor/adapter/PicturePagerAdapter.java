package com.joker.gankor.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.joker.gankor.R;
import com.joker.gankor.model.GankWelfare;
import com.joker.gankor.ui.activity.PictureActivity;
import com.joker.gankor.utils.ImageUtil;

import java.util.List;

/**
 * Created by joker on 2016/8/31.
 */
public class PicturePagerAdapter extends PagerAdapter {
    private List<GankWelfare.ResultsBean> bean;
    private LayoutInflater mInflater;
    private Context mContext;

    public PicturePagerAdapter(Context context, List<GankWelfare.ResultsBean> bean) {
        this.bean = bean;
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getCount() {
        return bean.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView view = (ImageView) mInflater.inflate(R.layout.activity_picture_item, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof PictureActivity) {
                    ((PictureActivity) mContext).changeToolbar();
                }
            }
        });
        ImageUtil.getInstance().displayImage(bean.get(position).getUrl(), view);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

    public void addList(List<GankWelfare.ResultsBean> beanList) {
        bean.addAll(beanList);
        notifyDataSetChanged();
    }
}
