package com.joker.gankor;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by joker on 2016/8/4.
 */
public class GankOrApplication extends Application {
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
//        LeakCanary.install(this);
        mContext = getApplicationContext();
        MobclickAgent.setScenarioType(mContext, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(context);
        ImageLoader.getInstance().init(configuration);
    }
}