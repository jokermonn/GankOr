package com.joker.gankor.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import com.joker.gankor.utils.LazyUtil;
import com.joker.gankor.utils.NetUtil;

/**
 * Created by joker on 2016/8/15.
 */
public class PullLoadRecyclerView extends RecyclerView {

    private int mLastVisiblePosition;
    private boolean isLoading = false;
    private PullLoadRecyclerView.onPullLoadListener onPullLoadListener;

    public PullLoadRecyclerView(Context context) {
        super(context);
    }

    public PullLoadRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PullLoadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] position = manager.findLastCompletelyVisibleItemPositions(new
                    int[manager
                    .getSpanCount()]);
            mLastVisiblePosition = getMaxPosition(position);
        } else if (getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) getLayoutManager();
            mLastVisiblePosition = manager.findLastCompletelyVisibleItemPosition();
        }

        if (onPullLoadListener != null && mLastVisiblePosition + 1 == getAdapter().getItemCount() &&
                !isLoading && dy > 0) {
            if (!NetUtil.isNetConnect(getContext())) {
                LazyUtil.showToast(getContext(), "网络没有连接，不能加载噢");
                return;
            }
            isLoading = true;
            onPullLoadListener.onPullLoad();
        }
    }

    public int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    //    上拉加载更多接口
    public void setPullLoadListener(onPullLoadListener onPullLoadListener) {
        this.onPullLoadListener = onPullLoadListener;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public interface onPullLoadListener {
        void onPullLoad();
    }
}
