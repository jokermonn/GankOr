package com.joker.gankor.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.joker.gankor.R;
import com.joker.gankor.adapter.PicturePagerAdapter;
import com.joker.gankor.model.GankWelfare;
import com.joker.gankor.ui.BaseActivity;
import com.joker.gankor.utils.ImageUtil;
import com.joker.gankor.utils.LazyUtil;

import java.util.ArrayList;

public class PictureActivity extends BaseActivity implements View.OnClickListener, ViewPager
        .OnPageChangeListener {
    public static final String RESULTS_BEAN = "results_bean";
    public static final String IMG_POSITION = "img_position";
    public static final String TRANSIT_PIC = "transit_pic";
    private ArrayList<GankWelfare.ResultsBean> bean;
    private int firstPosition;
    private int currentPosition;
    private Toolbar mTitleToolbar;
    private ViewPager mPicViewPager;
    public PicturePagerAdapter mAdapter;

    public static Intent newIntent(Context context, ArrayList<GankWelfare.ResultsBean> bean, int position) {
        Intent intent = new Intent(context, PictureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RESULTS_BEAN, bean);
        bundle.putInt(IMG_POSITION, position);
        intent.putExtras(bundle);

        return intent;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void initView(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_picture);
        mPicViewPager = (ViewPager) findViewById(R.id.vp_pic);
        mTitleToolbar = (Toolbar) findViewById(R.id.tb_title);

        mTitleToolbar.setNavigationOnClickListener(this);
        setSupportActionBar(mTitleToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        parseIntent();

        mAdapter = new PicturePagerAdapter(this, bean);
        mAdapter.addList(bean);
        mPicViewPager.setAdapter(mAdapter);
        mPicViewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();

        mPicViewPager.setCurrentItem(firstPosition);
        getSupportActionBar().setTitle(bean.get(firstPosition).getDesc());
    }

    private void parseIntent() {
        Bundle bundle = getIntent().getExtras();
        bean = bundle.getParcelableArrayList(RESULTS_BEAN);
        firstPosition = bundle.getInt(IMG_POSITION, 0);
    }

    @Override
    public void onClick(View v) {

    }

    public void changeToolbar() {
        if (mTitleToolbar.isShown()) {
            mTitleToolbar.setVisibility(View.INVISIBLE);
        } else {
            mTitleToolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_save:
                ImageUtil.getInstance().saveImage(this, bean.get(currentPosition).getDesc(), bean.get
                        (currentPosition).getUrl());
                return true;
            case R.id.menu_share:
                LazyUtil.showToast("暂时不支持分享功能哦");
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
        getSupportActionBar().setTitle(bean.get(currentPosition).getDesc());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}