# 第二天教程 #

接下来就可以构建我们的第一个 activity 了，SplashActivity 的页面效果我们模仿知乎日报的 splash 页面，是图片放大的动画效果，布局文件代码如下：

	<?xml version="1.0" encoding="utf-8"?>
	<ImageView
	    android:id="@+id/iv_splash"
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:scaleType="centerCrop"
	    tools:context=".ui.activity.SplashActivity"/>

SplashActivity 代码如下：

	public class SplashActivity extends BaseActivity {
	    public static final String IMG = "img";
	    private ImageView mSplashImageView;
	    private CacheUtil mCache;
	
	    @TargetApi(Build.VERSION_CODES.KITKAT)
	    @Override
	    protected void initView(Bundle savedInstanceState) {
			// 全屏
	        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	        setContentView(R.layout.activity_splash);
	        mSplashImageView = (ImageView) findViewById(R.id.iv_splash);
	    }
	
	    @Override
	    protected void initData() {
	        mCache = CacheUtil.getInstance(this);
	
			//        缓存不为空加载缓存
	        if (!mCache.isCacheEmpty(IMG)) {
	            ImageUtil.getInstance().displayImage(mCache.getAsString(IMG), mSplashImageView);
	        }
			//        在联网情况下，写入新缓存，且如果旧缓存为空则显示图片
	        if (isNetConnect()) {
	            OkUtil.getInstance().okHttpZhihuJObject(API.ZHIHU_START, IMG, new OkUtil.JObjectCallback() {
	                @Override
	                public void onFailure(Call call, IOException e) {
	                    e.printStackTrace();
	                    startToMainActivity();
	                }
	
	                @Override
	                public void onResponse(Call call, String jObjectUrl) {
	                    if (jObjectUrl != null) {
	                        if (mCache.isCacheEmpty(IMG)) {
	                            ImageUtil.getInstance().displayImage(jObjectUrl, mSplashImageView);
	                        }
	                        mCache.put(IMG, jObjectUrl);
	                    } else {
	                        if (mCache.isCacheEmpty(IMG)) {
	                            startToMainActivity();
	                        }
	                    }
	                }
	            });
	        } else {
				//            没网没缓存
	            if (mCache.isCacheEmpty(IMG)) {
	                startToMainActivity();
	            }
	            LazyUtil.showToast("网络连接错误");
	        }
	
	        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation
	                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
	        scaleAnimation.setFillAfter(true);
	        scaleAnimation.setDuration(3000);
	        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
	            @Override
	            public void onAnimationStart(Animation animation) {
	            }
	
	            @Override
	            public void onAnimationEnd(Animation animation) {
	                startToMainActivity();
	            }
	
	            @Override
	            public void onAnimationRepeat(Animation animation) {
	            }
	        });
	        mSplashImageView.startAnimation(scaleAnimation);
	    }
	
	    private void startToMainActivity() {
	        startActivity(new Intent(this, MainActivity.class));
	        finish();
	    }
	}

这里的 api 都比较简单，我就不多做介绍了，逻辑流程也在代码中写得很清晰了。

SplashActivity 执行完动画后，进入 MainActivity，MainActivity 代码如下：

	public class MainActivity extends BaseActivity {
	
	    public MainFragment mContentGank;
	    public MainFragment mContentZhihu;
	    private Toolbar mTitleToolbar;
	    private TabLayout mTitleTabLayout;
	    private NavigationView mContentNavigationView;
	    private DrawerLayout mMainDrawerLayout;
	    private long firstTime;
	    private int mLastItemId;
	
	    @Override
	    protected void initView(Bundle savedInstanceState) {
	        setContentView(R.layout.activity_main);
	        mTitleToolbar = (Toolbar) findViewById(R.id.tb_title);
	        mTitleTabLayout = (TabLayout) findViewById(R.id.tl_title);
	        mContentNavigationView = (NavigationView) findViewById(R.id.nv_content);
	        mMainDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main);
	
	//        设置导航栏顶部图片
	        View view = mContentNavigationView.getHeaderView(0);
	        ImageView header = (ImageView) view.findViewById(R.id.nav_head);
	        ImageUtil.getInstance().displayImage(CacheUtil.getInstance(this).getAsString(SplashActivity.IMG),
	                header);
	
	//        设置 toolBar
	        setSupportActionBar(mTitleToolbar);
	        final ActionBar ab = getSupportActionBar();
	        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
	        ab.setDisplayHomeAsUpEnabled(true);

	        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mMainDrawerLayout,
	                mTitleToolbar, R.string.meizhi, R.string.meizhi);
	        mMainDrawerLayout.addDrawerListener(actionBarDrawerToggle);
	        actionBarDrawerToggle.syncState();
	    }
	
	    @Override
	    protected void initData() {
	        setupDrawerContent();
	        mContentNavigationView.setCheckedItem(0);
	    }
	
	    private void setupDrawerContent() {
	        mLastItemId = mContentNavigationView.getMenu().getItem(0).getItemId();
	        changeFragments(mLastItemId);
	        mContentNavigationView.setNavigationItemSelectedListener(new NavigationView
	                .OnNavigationItemSelectedListener() {
	            @Override
	            public boolean onNavigationItemSelected(MenuItem item) {
	                mMainDrawerLayout.closeDrawers();
	                if (item.getItemId() == R.id.menu_introduce) {
	                    startActivity(new Intent(MainActivity.this, AboutMeActivity.class));
	                    item.setChecked(false);
	                } else {
	                    if (item.getItemId() != mLastItemId) {
	                        item.setChecked(true);
	                        changeFragments(item.getItemId());
	                        mLastItemId = item.getItemId();
	                    }
	                }
	                return true;
	            }
	        });
	    }
	
	    public void changeFragments(int itemId) {
	        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	        hideAll(transaction);
	        switch (itemId) {
	            case R.id.nav_knowledge:
	//                      知乎界面
	                if (mContentZhihu != null) {
	                    transaction.show(mContentZhihu);
	                } else {
	                    mContentZhihu = MainFragment.newInstance(MainFragment
	                            .MENU_ZHIHU);
	                    mContentZhihu.setOnBannerClickListener(this);
	                    mContentZhihu.setOnDailyItemClickListener(this);
	                    mContentZhihu.setOnBannerClickListener(this);
	                    mContentZhihu.setOnHotItemClickListener(this);
	                    transaction.add(R.id.fl_content, mContentZhihu);
	                }
	                initToolbar(MainFragment
	                        .MENU_ZHIHU);
	                break;
	            case R.id.nav_beauty:
	//                      妹纸界面
	                if (mContentGank != null) {
	                    transaction.show(mContentGank);
	                } else {
	                    mContentGank = MainFragment.newInstance(MainFragment
	                            .MENU_GANK);
	                    mContentGank.setTextListener(this);
	                    mContentGank.setImageListener(this);
	                    transaction.add(R.id.fl_content, mContentGank);
	                }
	                initToolbar(MainFragment
	                        .MENU_GANK);
	                break;
	            default:
	                break;
	        }
	        transaction.commit();
	    }
	
	//						隐藏所有 fragment
	    private void hideAll(FragmentTransaction transaction) {
	        if (mContentZhihu != null) {
	            transaction.hide(mContentZhihu);
	        }
	        if (mContentGank != null) {
	            transaction.hide(mContentGank);
	        }
	    }
	
	    //    暴露给 fragment 连接 tabLayout
	    public void setupViewPager(ViewPager viewPager) {
	        mTitleTabLayout.setSelectedTabIndicatorColor(Color.WHITE);
	        mTitleTabLayout.setupWithViewPager(viewPager);
	    }
	
	    public void hideTabLayout(boolean hide) {
	        if (hide) {
	            mTitleTabLayout.setVisibility(View.GONE);
	        } else {
	            mTitleTabLayout.setVisibility(View.VISIBLE);
	        }
	    }
	
	    public void initToolbar(String args) {
	        if (args.equals(MainFragment.MENU_GANK)) {
	            hideTabLayout(true);
	            setToolbarTitle("妹纸");
	        } else {
	            hideTabLayout(false);
	            setToolbarTitle("知乎");
	        }
	    }

	    public void setToolbarTitle(String title) {
	        getSupportActionBar().setTitle(title);
	    }
	
	    @Override
	    public void onBackPressed() {
	        if (mContentNavigationView.isShown()) {
	            mMainDrawerLayout.closeDrawers();
	            return;
	        }
	        long secondTime = System.currentTimeMillis();
	        if (secondTime - firstTime > 2000) {
	            Snackbar sb = Snackbar.make(mContentNavigationView, "再按一次退出", Snackbar.LENGTH_SHORT);
	            sb.getView().setBackgroundColor(getResources().getColor(R.color.red_300));
	            sb.show();
	            firstTime = secondTime;
	        } else {
	            finish();
	        }
	    }
	}

布局文件如下：

	<?xml version="1.0" encoding="utf-8"?>
	<android.support.v4.widget.DrawerLayout
	    android:id="@+id/dl_main"
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:fitsSystemWindows="true"
	    tools:context=".ui.activity.MainActivity">
	
	    <android.support.design.widget.CoordinatorLayout
	        android:layout_width="match_parent"
	        android:layout_height="wrap_content"
	        android:fitsSystemWindows="true">
	
	        <android.support.design.widget.AppBarLayout
	            android:layout_width="match_parent"
	            android:layout_height="wrap_content">
	
	            <android.support.v7.widget.Toolbar
	                android:id="@+id/tb_title"
	                android:layout_width="match_parent"
	                android:layout_height="?attr/actionBarSize"
	                android:background="?attr/colorPrimary"
	                app:layout_scrollFlags="scroll|enterAlways"/>
	
	            <android.support.design.widget.TabLayout
	                android:id="@+id/tl_title"
	                android:layout_width="match_parent"
	                android:layout_height="wrap_content"
	                android:minHeight="@dimen/tab_layout_height"
	                app:layout_scrollFlags="scroll|enterAlways|exitUntilCollapsed"/>
	        </android.support.design.widget.AppBarLayout>
	
	        <FrameLayout
	            android:id="@+id/fl_content"
	            android:layout_width="match_parent"
	            android:layout_height="match_parent"
	            app:layout_behavior="@string/appbar_scrolling_view_behavior"/>
	
	    </android.support.design.widget.CoordinatorLayout>
	
	    <android.support.design.widget.NavigationView
	        app:itemTextColor="@color/nav_item"
	        android:id="@+id/nv_content"
	        android:layout_width="wrap_content"
	        android:layout_height="match_parent"
	        android:layout_gravity="start"
	        android:background="@color/white"
	        android:fitsSystemWindows="true"
	        app:headerLayout="@layout/nav_header_main"
	        app:menu="@menu/menu_drawer"
	        app:theme="@style/GankOrTheme"/>
	
	</android.support.v4.widget.DrawerLayout>

这里涉及到了官方的 material design 库，在此我推荐几个学习的资料 [Android Design Support Library使用详解（徐宜生）](http://blog.csdn.net/eclipsexys/article/details/46349721)，[android CoordinatorLayout使用（张兴业）](http://blog.csdn.net/xyz_lmn/article/details/48055919) 以及翻译自CodePath 上  [关于 CoordinatorLayout](./CoordinatorLayout.md) 。最后一篇是我自行翻译的，所以不带原文的图片，文档中附原文链接，可参考原文文档中的图片，看完这三篇文章并且能够理解的话，看懂上面的代码就不成问题了。

所以我这里大概的实现思路就是，主页面就是以 material design 为主的几个控件以及一个 FrameLayout，动态来添加 fragment，来达到我所需要的效果。在点击事件中我们可以看到，无论是妹纸界面还是知乎界面，其实差别就是在于 ``MainFragment.newInstance(String string);`` 中的这个参数不一样。所以重点就在于 MainFragment 了，其代码如下：

	/**
	 * A simple {@link Fragment} subclass.
	 */
	public class MainFragment extends BaseFragment {
	    public final static String MENU_GANK = "menu_gank";
	    public final static String MENU_ZHIHU = "menu_zhihu";
	    public final static String MENU_ID = "menu_id";
	    public GankFragment mGankFragment;
	    public ZhihuDailyNewsFragment mDailyNewsFragment;
	    public ZhihuHotNewsFragment mHotNewsFragment;
	    private MainAdapter adapter;
	    private ViewPager mContentViewPager;
	    private List<Fragment> mFragments = new ArrayList<>();
	    private List<String> mTitles = new ArrayList<>();
	
	    public MainFragment() {
	        // Required empty public constructor
	    }
	
	    public static MainFragment newInstance(String menuId) {
	        Bundle args = new Bundle();
	        args.putString(MENU_ID, menuId);
	        MainFragment fragment = new MainFragment();
	        fragment.setArguments(args);
	        return fragment;
	    }
	
	    @Nullable
	    @Override
	    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
	            savedInstanceState) {
	        super.onCreateView(inflater, container, savedInstanceState);
	        View view = inflater.inflate(R.layout.fragment_main, container, false);
	        mContentViewPager = (ViewPager) view.findViewById(R.id.vp_content);
	        adapter = new MainAdapter(getChildFragmentManager(), mFragments, mTitles);
	        initFragments();
	
	        return view;
	    }
	
	    public void initFragments() {
	        String args = getArguments().getString(MENU_ID);
	
	        if (MENU_GANK.equals(args)) {
	            mGankFragment = new GankFragment();
	            mGankFragment.setImageListener(this);
	            mGankFragment.setTextListener(this);
	
	            mFragments.add(mGankFragment);
	            mTitles.add("妹纸");
	
	            adapter.changeDataList(mTitles, mFragments);
	        } else {
	            mDailyNewsFragment = new ZhihuDailyNewsFragment();
	            mDailyNewsFragment.setOnItemClickListener(this);
	            mDailyNewsFragment.setOnBannerClickListener(this);
	            mHotNewsFragment = new ZhihuHotNewsFragment();
	            mHotNewsFragment.setOnItemClickListener(this);
	
	            mFragments.add(mDailyNewsFragment);
	            mFragments.add(mHotNewsFragment);
	            mTitles.add("知乎日报");
	            mTitles.add("热门消息");
	
	            adapter.changeDataList(mTitles, mFragments);
	        }
	        mContentViewPager.setAdapter(adapter);
			// 调用 MainActivity 的 setupViewPager() 方法
	        ((MainActivity) mActivity).setupViewPager(mContentViewPager);
	    }
	}

这里可以看到，实际上我们做的事情就是，把传过来的参数进行匹配，如果是 ``MENU_GANK`` 的话，那么就是显示妹纸页面，如果是 ``MENU_ZHIHU`` 那么我们就显示知乎界面。这里将 MainFragment 的创建封装成一个静态方法，这样的好处显而易见，任何需要创建 MainFragment 的地方都调用这个方法，需要传入什么参数，也都一目了然。

其布局文件代码如下：

	<android.support.v4.view.ViewPager
	    android:id="@+id/vp_content"
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    tools:context=".ui.fragment.MainFragment"/>

这里其实可以看得出来， MainFragment 其实是 GankFragment 和 DailyNewsFragment， HotNewsFragment 的父 fragment，我们在 MainFragment 里面其实就是放了一个 ViewPager，然后在 ViewPager 里面在放置相应的 fragment。所以我们这里使用的是 fragment 嵌套 fragment 思路。

适配器 MainAdapter 代码如下：

	/**
	 * Created by joker on 2016/8/4.
	 */
	public class MainAdapter extends FragmentStatePagerAdapter {
	    private List<Fragment> mFragments;
	    private List<String> mTitles;
	
	    public MainAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
	        super(fm);
	        mFragments = fragments;
	        mTitles = titles;
	    }
	
	    @Override
	    public Fragment getItem(int position) {
	        return mFragments.get(position);
	    }
	
	    @Override
	    public int getCount() {
	        return mFragments.size();
	    }
	
	    @Override
	    public CharSequence getPageTitle(int position) {
	        return mTitles.get(position);
	    }
	
	    public void changeDataList(List<String> titles, List<Fragment> fragments) {
	        mTitles = titles;
	        mFragments = fragments;
	    }
	}

在使用 ViewPager 嵌套 fragment 的时候，官方更建议我们使用 FragmentStatePagerAdapter 或者 FragmentPageAdapter，这两者的主要区别在于 FragmentPageAdapter 类内的每一个生成的 Fragment 都将保存在内存之中，所以适用于那些相对静态的页，数量也比较少的那种；如果需要处理有很多页，并且数据动态性较大、占用内存较多的情况，应该使用FragmentStatePagerAdapter。详情可见这篇[FragmentPagerAdapter与FragmentStatePagerAdapter区别](http://www.cnblogs.com/lianghui66/p/3607091.html)

接下来，我们就是构建 GankFragment， ZhihuDailyNewsFragment， ZhihuHotNewsFragment 了，实际上，这三者的逻辑业务是差不多的，所以我们不妨构建一个它们共同的基类 fragment 继承自 BaseFragment，我们命名为 ContentFragment，代码如下：

	/**
	 * 懒加载 fragment
	 * A simple {@link Fragment} subclass.
	 * Created by joker on 2016/8/8.
	 */
	public abstract class ContentFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
	    //    数据是否加载完毕
	    protected boolean isDataLoaded = false;
	    //    视图是否创建完毕
	    protected boolean isViewCreated = false;
	    protected OkUtil mOkUtil;
	    protected CacheUtil mCache;
	    protected Gson mGson;
	    protected PullLoadRecyclerView mContentRecyclerView;
	    protected SwipeRefreshLayout mContentSwipeRefreshLayout;
	    protected Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	                case 0x122:
	                    mContentSwipeRefreshLayout.setRefreshing(false);
	                    LazyUtil.showToast("网络没有连接哦");
	                    break;
	                case 0x121:
	//                    下拉刷新
	                    loadDataFromNet(getFirstPageUrl());
	                default:
	                    break;
	            }
	        }
	    };
	
	    public ContentFragment() {
	        // Required empty public constructor
	    }
		
		// 获取最初的 url （刷新或者第一次加载时的 url）
	    protected abstract String getFirstPageUrl();
	
	    protected boolean isFirstPage(String url) {
	        return getFirstPageUrl().equals(url);
	    }
	
	    /**
	     * 1. 缓存为空时第一次加载缓存 或者刷新
	     * 2. 上拉加载更多
	     *
	     * @param url 前者使用 getFirstPageUrl() 后者需自己传入 是固定值
	     */
	    protected abstract void loadDataFromNet(String url);
	
	    @Nullable
	    @Override
	    @CallSuper
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        super.onCreateView(inflater, container, savedInstanceState);
	        // Inflate the layout for this fragment
	        View view = inflater.inflate(R.layout.fragment_content, container, false);
	        mContentRecyclerView = (PullLoadRecyclerView) view.findViewById(R.id.rv_content);
	        mContentSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_content);
	        SpacesItemDecoration decoration = new SpacesItemDecoration((int) (Math.random() * 5 + 15));
	        mContentRecyclerView.addItemDecoration(decoration);
	
	        initView(inflater, container);
	        initSwipeRefreshLayout();
	
	        isViewCreated = true;
	
	        return view;
	    }
	
	    protected abstract void initView(LayoutInflater inflater, ViewGroup container);
	
	    protected void initSwipeRefreshLayout() {
	        mContentSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android
	                .R.color.holo_orange_light, android.R.color.holo_green_light, android.R.color
	                .holo_red_dark);
	        mContentSwipeRefreshLayout.setOnRefreshListener(this);
	    }
	
	    @CallSuper
	    protected void initData() {
	        isDataLoaded = true;
	        mOkUtil = OkUtil.getInstance();
	        mCache = CacheUtil.getInstance(mActivity);
	        mGson = new Gson();
	    }
	
	    @CallSuper
	    @Override
	    public void setUserVisibleHint(boolean isVisibleToUser) {
	        super.setUserVisibleHint(isVisibleToUser);
	
	        if (isVisibleToUser && !isDataLoaded && isViewCreated) {
	//            ViewPager 其他页面的 fragment，我们进行判断后再加载数据
	            initData();
	        }
	    }
	
	    @Override
	    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	
	        //          对于第一个直接呈现在用户面前的 fragment， 我们需要加载数据
	        if (getUserVisibleHint()) {
	            initData();
	        }
	    }
	
	    public boolean isNetConnect() {
	        return mActivity.isNetConnect();
	    }
	
	    @Override
	    public void onRefresh() {
	        if (isNetConnect()) {
	            mHandler.sendEmptyMessage(0x121);
	        } else {
	            mHandler.sendEmptyMessage(0x122);
	        }
	    }
	
	    @Override
	    public void onStop() {
	        super.onStop();
	        LazyUtil.log(getClass().getName(), "    onStop");
	        OkUtil.getInstance().cancelAll(mOkUtil);
	        if (mContentSwipeRefreshLayout.isRefreshing()) {
	            mContentSwipeRefreshLayout.setRefreshing(false);
	        }
	    }
	
	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	        LazyUtil.log(getClass().getName() + "    onDestroy");
	//        RefWatcher refWatcher = GankOrApplication.getRefWatcher(mActivity.getApplicationContext());
	//        refWatcher.watch(this);
	        mActivity = null;
	    }
}

其布局文件如下：

	<android.support.v4.widget.SwipeRefreshLayout
	    android:id="@+id/srl_content"
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:tools="http://schemas.android.com/tools"
	    android:layout_width="match_parent"
	    android:layout_height="wrap_content"
	    tools:context=".ui.fragment.ContentFragment">
	
	    <com.joker.gankor.view.PullLoadRecyclerView
	        android:id="@+id/rv_content"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"
	        android:background="@color/fragment_background"/>
	
	</android.support.v4.widget.SwipeRefreshLayout>

在这里，我们又自定义了一个可上拉加载更多的 RecyclerView，代码如下：

	/** 上拉加载更多 RecyclerView
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
	                LazyUtil.showToast( "网络没有连接，不能加载噢");
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

关于上拉加载更多的 RecyclerView 可以参考这篇[RecyclerView实例-实现可下拉刷新上拉加载更多并可切换线性流和瀑布流模式（1）](http://www.cnblogs.com/xiaoyaoxia/p/4977125.html)，简单来说，就是在它的滑动事件中首先判断当前的 layoutManager，其次就是针对当前最后一个可见的 item 的 position 进行监听，如果当前传入的 listener 不为空，且当前最后一个可见的 item 是 adpater 里面最后的一个 item， 且当前不是在加载状态中，且当前是屏幕向上滑动的状态，那么我们就可以调用上拉加载更多接口的 ``onPullLoad()`` 方法啦。

对于日常需求中，我们在很多情况下对于 ViewPager 的预加载功能感到很烦恼，而这个懒加载的 fragment 就很好的解决了这个问题，所谓懒加载，即 fragment 的 UI 对用户可见时才加载数据，具体可参考这篇[Fragment 懒加载实战](http://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650820834&idx=1&sn=694a94615494bfcaed07188e2601724a&scene=23&srcid=0808vHgojfq1vTzIpSDNBhwq#rd)。在 MainFragment 中我们除了对需要用的 Okhttp， Gson， Cache 对象初始化，需要用到的布局文件初始化之外，最重要的就是在 fragment 销毁的时候将资源都销毁，例如网络请求的取消（防止影响当前页面网络请求阻塞），``SwipeRefreshLayout.setRefreshing(false);``（防止网络过慢导致一直不能获取到网络请求）