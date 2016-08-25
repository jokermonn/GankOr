package com.joker.gankor.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joker.gankor.R;
import com.joker.gankor.ui.BaseActivity;
import com.joker.gankor.utils.LazyUtil;

import java.util.ArrayList;
import java.util.List;

public class AboutMeActivity extends BaseActivity implements View.OnClickListener {
    private final String githubUrl = "https://github.com/jokerZLemon";
    public TranslateAnimation mAnimation;
    public Toolbar mToolBar;
    private List<TextView> mTextViews;
    private boolean isStop = false;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about_me);

        RelativeLayout contentLinearLayout = (RelativeLayout) findViewById(R.id.ll_content);
        mToolBar = (Toolbar) findViewById(R.id.tb_title);
        TextView tvrNameTextView = (TextView) findViewById(R.id.tvr_name);
        TextView tvlNameTextView = (TextView) findViewById(R.id.tvl_name);
        TextView tvrIntrTextView = (TextView) findViewById(R.id.tvr_intr);
        TextView tvlIntrTextView = (TextView) findViewById(R.id.tvl_intr);
        TextView tvrGithubTextView = (TextView) findViewById(R.id.tvr_github);
        TextView tvlGithubTextView = (TextView) findViewById(R.id.tvl_github);
        TextView tvrQqTextView = (TextView) findViewById(R.id.tvr_qq);
        TextView tvlQqTextView = (TextView) findViewById(R.id.tvl_qq);

        //        设置 toolBar
        mToolBar.setNavigationOnClickListener(this);
        contentLinearLayout.setOnClickListener(this);
        mToolBar.setTitle(getString(R.string.about_me));
        setSupportActionBar(mToolBar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        tvrNameTextView.setText(getString(R.string.name1));
        tvlNameTextView.setText(getString(R.string.name2));
        tvrIntrTextView.setText(getString(R.string.intr1));
        tvlIntrTextView.setText(getString(R.string.intr2));
        tvrGithubTextView.setText(getString(R.string.github));
        tvlGithubTextView.setText(githubUrl);
        tvrQqTextView.setText(getString(R.string.qq1));
        tvlQqTextView.setText(getString(R.string.qq2));

        tvrNameTextView.setOnClickListener(this);
        tvlNameTextView.setOnClickListener(this);
        tvrIntrTextView.setOnClickListener(this);
        tvlIntrTextView.setOnClickListener(this);
        tvrGithubTextView.setOnClickListener(this);
        tvlGithubTextView.setOnClickListener(this);
        tvrQqTextView.setOnClickListener(this);
        tvlQqTextView.setOnClickListener(this);

        mTextViews = new ArrayList<>();
        mTextViews.add(tvrNameTextView);
        mTextViews.add(tvlNameTextView);
        mTextViews.add(tvrIntrTextView);
        mTextViews.add(tvlIntrTextView);
        mTextViews.add(tvrGithubTextView);
        mTextViews.add(tvlGithubTextView);
        mTextViews.add(tvrQqTextView);
        mTextViews.add(tvlQqTextView);
    }

    @Override
    protected void initData() {
        startAnimation(0);
    }

    private void startAnimation(final int i) {
        int t = i % 2 == 0 ? -1 : 1;
        mTextViews.get(i).setVisibility(View.VISIBLE);
        mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.5f,
                Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, t * 0.5f, Animation
                .RELATIVE_TO_PARENT, 0f);
        mAnimation.setInterpolator(new OvershootInterpolator());
        mAnimation.setDuration(1000);
        mAnimation.setFillAfter(true);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int t = i + 1;
                if (t < mTextViews.size() && !isStop) {
                    startAnimation(t);
                } else {
                    LazyUtil.showToast("GitHub 和 qq 可以直接点击");
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mTextViews.get(i).startAnimation(mAnimation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvr_github:
            case R.id.tvl_github:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl)));
                break;
            case R.id.tvr_qq:
            case R.id.tvl_qq:
                ClipboardManager copy = (ClipboardManager) this
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                copy.setText("429094465");
                LazyUtil.showToast("qq 已复制到粘贴板");
                break;
            case R.id.ll_content:
                if (!isStop) {
                    isStop = true;
                    setAllVisible();
                } else if (mToolBar.getVisibility() != View.GONE) {
                    mToolBar.setVisibility(View.GONE);
                } else {
                    mToolBar.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    private void setAllVisible() {
        for (int i = 0; i < mTextViews.size(); i++) {
            if (mTextViews.get(i).getVisibility() != View.VISIBLE) {
                mTextViews.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}