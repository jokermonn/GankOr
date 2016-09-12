# 第四天教程 #

至此，我们已经完成了关于主内容显示的所有任务，我们再回到 MainActivity，细细一算，大概有五个点击事件要做：

- GankFragment 的图片点击，等比例放大显示成一个大的图片

- GankFragment 的文字点击，跳转至相应的视频页面

- ZhihuDailyNewsFragment 轮播器图片点击事件

- ZhihuDailyNewsFragment item 点击事件

- ZhihuHotNewsFragment item 点击事件

同时，我这里运用到了最后一个第三方库—— [InstaMaterial](https://github.com/frogermcs/InstaMaterial)，实际上也就是拿了它的一个 java 文件下来，代码如下：

	/**
	 * Created by Miroslaw Stanek on 18.01.15.
	 */
	public class RevealBackgroundView extends View {
	    public static final int STATE_NOT_STARTED = 0;
	    public static final int STATE_FILL_STARTED = 1;
	    public static final int STATE_FINISHED = 2;
	
	    private static final Interpolator INTERPOLATOR = new AccelerateInterpolator();
	    private static final int FILL_TIME = 600;
	
	    private int state = STATE_NOT_STARTED;
	
	    private Paint fillPaint;
	    private int currentRadius;
	    ObjectAnimator revealAnimator;
	
	    private int startLocationX;
	    private int startLocationY;
	
	
	    private OnStateChangeListener onStateChangeListener;
	
	    public RevealBackgroundView(Context context) {
	        super(context);
	        init();
	    }
	
	    public RevealBackgroundView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        init();
	    }
	
	    public RevealBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
	        super(context, attrs, defStyleAttr);
	        init();
	    }
	
	    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
	    public RevealBackgroundView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
	        super(context, attrs, defStyleAttr, defStyleRes);
	        init();
	    }
	
	    private void init() {
	        fillPaint = new Paint();
	        fillPaint.setStyle(Paint.Style.FILL);
	        fillPaint.setColor(Color.WHITE);
	    }
	
	    public void setFillPaintColor(int color) {
	        fillPaint.setColor(color);
	    }
	
	    public void startFromLocation(int[] tapLocationOnScreen) {
	        changeState(STATE_FILL_STARTED);
	        startLocationX = tapLocationOnScreen[0];
	        startLocationY = tapLocationOnScreen[1];
	        revealAnimator = ObjectAnimator.ofInt(this, "currentRadius", 0, getWidth() + getHeight()).setDuration(FILL_TIME);
	        revealAnimator.setInterpolator(INTERPOLATOR);
	        revealAnimator.addListener(new AnimatorListenerAdapter() {
	            @Override
	            public void onAnimationEnd(Animator animation) {
	                changeState(STATE_FINISHED);
	            }
	        });
	        revealAnimator.start();
	    }
	
	    public void setToFinishedFrame() {
	        changeState(STATE_FINISHED);
	        invalidate();
	    }
	
	    @Override
	    protected void onDraw(Canvas canvas) {
	        if (state == STATE_FINISHED) {
	            canvas.drawRect(0, 0, getWidth(), getHeight(), fillPaint);
	        } else {
	            canvas.drawCircle(startLocationX, startLocationY, currentRadius, fillPaint);
	        }
	    }
	
	    private void changeState(int state) {
	        if (this.state == state) {
	            return;
	        }
	
	        this.state = state;
	        if (onStateChangeListener != null) {
	            onStateChangeListener.onStateChange(state);
	        }
	    }
	
	    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
	        this.onStateChangeListener = onStateChangeListener;
	    }
	
	    public void setCurrentRadius(int radius) {
	        this.currentRadius = radius;
	        invalidate();
	    }
	
	    public static interface OnStateChangeListener {
	        void onStateChange(int state);
	    }
	}

其实看代码就知道了大概，它就是一个 view，模拟从一个 Activity 跳转到另一个 Activity 的动画过程，详细效果可以在 app 中看到，那么它如何应用呢？

假设我们从 Activity A 跳转到 Activity B，那么我们要在 Activity A 中获取到当前手指点击屏幕的位置，代码如下：

    private int[] getClickLocation(View v) {
        int[] clickLocation = new int[2];
        v.getLocationOnScreen(clickLocation);
        clickLocation[0] += v.getWidth() / 2;

        return clickLocation;
    }

第二步，将它放进 intent，传给 Activity B，同时在 ``startActivity()`` 之后，调用 ``overridePendingTransition()`` 来达到效果。

第三步，在 Activity B 的布局文件中，添加 RevealBackgroundView，代码如下：

	<com.joker.gankor.view.RevealBackgroundView
	        android:id="@+id/rbv_content"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"/>

第四步，，再显示所有的 View，所以关键点就在这个动画上，代码如下：

	mContentRevealBackgroundView.setOnStateChangeListener(this);
    if (mLocation == null || savedInstanceState != null) {
        mContentRevealBackgroundView.setToFinishedFrame();
    } else {
        mContentRevealBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
                .OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mContentRevealBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                mContentRevealBackgroundView.startFromLocation(mLocation);
                return true;
            }
        });
     }

这里先对各个字段做一下解释：mContentRevealBackgroundView 是 RevealBackgroundView 对象，mLocation 是解析 Activity A 中通过 intent 传过来的手机点击屏幕位置。这样解释后代码应该就很容易看懂了，首先给 RevealBackgroundView 设置一个 ``OnStateChangeListener`` 监听器，倘若当前的状态是动画没有开始，我们在 Activity B 代码中，首先隐藏除 RevealBackgroundView 以外的所有 View，倘若当前的状态是动画结束，那么我们就让这些 View 都显示出来，代码如下：

	@Override
    public void onStateChange(int state) {
        if (state == RevealBackgroundView.STATE_FINISHED) {
            mContentNestedScrollView.setVisibility(View.VISIBLE);
            mContentAppBarLayout.setVisibility(View.VISIBLE);
        } else {
            mContentNestedScrollView.setVisibility(View.GONE);
            mContentAppBarLayout.setVisibility(View.GONE);
        }
    }

然后我们对 intent 中传过来的手指点击屏幕的位置进行解析，倘若这个值为空，或者 savedinstancestate 不为空，我们进入 ``RevealBackgroundView.setToFinishedFrame()`` 方法，这个方法点进去，其实很简单，其一将当前状态改变成 ``STATE_FINISHED``，然后重绘，实际上就是代表它并没有做这个动画过程。否则的话，就获取当前的视图树并注册一个 ``OnPreDrawListener`` 监听器，看名字就知道——绘画前调用的接口，所以在绘画前，我们首先取消注册这个监听器（因为在绘画过程中，这个接口会调用不止一次），同时进行 RevealBackgroundView 的动画过程并返回 true 即可。

说了这么多，那么具体在 MainActivity 中的代码如何呢，如下：

		//    知乎日报列表点击事件
	    @Override
	    public void onZhihuDailyItemClick(View view, ZhihuDailyNews.StoriesBean storiesBean) {
	        int[] clickLocation = getClickLocation(view);
	        startActivity(ZhihuDetailsActivity.newTopStoriesIntent(this, (API.ZHIHU_NEWS_FOUR + String.valueOf
	                (storiesBean.getId())), clickLocation));
	        this.overridePendingTransition(0, 0);
	    }
	
	    //    知乎日报头条点击事件
	    @Override
	    public void onBannerClickListener(ZhihuDailyNews.TopStoriesBean topStories) {
	        startActivity(ZhihuDetailsActivity.newTopStoriesIntent(this, (API.ZHIHU_NEWS_FOUR + String.valueOf
	                (topStories.getId())), null));
	    }
	
	    //    知乎热门列表点击事件
	    @Override
	    public void onZhihuHotItemClick(View view, ZhihuHotNews.RecentBean recentBean) {
	        int[] clickLocation = getClickLocation(view);
	        startActivity(ZhihuDetailsActivity.newTopStoriesIntent(this, (API.ZHIHU_NEWS_TWO + String.valueOf
	                (recentBean.getNewsId())), clickLocation));
	        this.overridePendingTransition(0, 0);
	    }
	
	    //    Gank 图片点击
	    @Override
	    public void onGankImageClick(View image, String url, String desc) {
	        Intent intent = PictureActivity.newIntent(MainActivity.this, url, desc);
	        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
	                MainActivity.this, image, PictureActivity.TRANSIT_PIC);
	        try {
	            ActivityCompat.startActivity(MainActivity.this, intent, optionsCompat.toBundle());
	        } catch (IllegalArgumentException e) {
	            e.printStackTrace();
	            startActivity(intent);
	        }
	    }
	
	    //    Gank 文字点击
	    @Override
	    public void onGankTextClick(String url) {
	        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	    }

		// 	获取点击位置
	    private int[] getClickLocation(View v) {
	        int[] clickLocation = new int[2];
	        v.getLocationOnScreen(clickLocation);
	        clickLocation[0] += v.getWidth() / 2;
	
	        return clickLocation;
	    }

如最后一个方法所示，所以还是挺简单的，我们将获取到的位置通过 intent 传给相应的下一个 Activity 即可。

ZhihuDetailsActivity 代码如下：

	public class ZhihuDetailsActivity extends BaseActivity implements RevealBackgroundView
	        .OnStateChangeListener {
	    public static final String URL = "url";
	    public static final String LOCATION = "location";
	    public ZhihuDetails mTopDetails;
	    public int[] mLocation;
	    public RevealBackgroundView mContentRevealBackgroundView;
	    private String url;
	    private CacheUtil mCache;
	    private ImageView mTitleImageView;
	    private CollapsingToolbarLayout mTitleCollapsingToolbarLayout;
	    private WebView mContentWebView;
	    private NestedScrollView mContentNestedScrollView;
	    private AppBarLayout mContentAppBarLayout;
	
	    public static Intent newTopStoriesIntent(Activity activity, String url, int[] locationArr) {
	        Bundle bundle = new Bundle();
	        bundle.putString(URL, url);
	        bundle.putIntArray(LOCATION, locationArr);
	        Intent intent = new Intent(activity, ZhihuDetailsActivity.class);
	        intent.putExtras(bundle);
	
	        return intent;
	    }
	
	    @SuppressLint("SetJavaScriptEnabled")
	    @Override
	    protected void initView(Bundle savedInstanceState) {
	        setContentView(R.layout.activity_daily_details);
	        mContentRevealBackgroundView = (RevealBackgroundView) findViewById(R.id.rbv_content);
	        mTitleImageView = (ImageView) findViewById(R.id.iv_title);
	        Toolbar mTitleToolbar = (Toolbar) findViewById(R.id.tb_title);
	        mTitleCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ctl_title);
	        mContentAppBarLayout = (AppBarLayout) findViewById(R.id.abl_content);
	        mContentNestedScrollView = (NestedScrollView) findViewById(R.id.nsv_content);
	        mContentWebView = (WebView) findViewById(R.id.wb_content);
	        setSupportActionBar(mTitleToolbar);
	        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	        WebSettings settings = mContentWebView.getSettings();
	        settings.setJavaScriptEnabled(true);
	        //        加载缓存，如果不存在就加载网络数据
	        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
	        //        app cache
	        settings.setAppCacheEnabled(true);
	        //        dom storage
	        settings.setDomStorageEnabled(true);
	        //        database cache
	        settings.setDatabaseEnabled(true);
	        settings.setLoadWithOverviewMode(true);
	        settings.setSupportZoom(true);
	
	        parseIntent();
	
	        mContentRevealBackgroundView.setOnStateChangeListener(this);
	        if (mLocation == null || savedInstanceState != null) {
	            mContentRevealBackgroundView.setToFinishedFrame();
	        } else {
	            mContentRevealBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
	                    .OnPreDrawListener() {
	                @Override
	                public boolean onPreDraw() {
	                    mContentRevealBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
	                    mContentRevealBackgroundView.startFromLocation(mLocation);
	                    return true;
	                }
	            });
	        }
	    }
	
	    @Override
	    protected void initData() {
	        mCache = CacheUtil.getInstance(this);
	        loadContent();
	    }
	
	    private void parseIntent() {
	        Intent intent = getIntent();
	        Bundle bundle = intent.getExtras();
	        url = bundle.getString(URL);
	        mLocation = bundle.getIntArray(LOCATION);
	    }
	
	    private void loadContent() {
	        Gson mGson = new Gson();
	        if (!mCache.isCacheEmpty(url)) {
	            mTopDetails = mGson.fromJson(mCache.getAsString(url), ZhihuDetails.class);
	            initAppBarLayout();
	            loadWebView();
	        } else {
	            if (isNetConnect()) {
	                loadLatestData();
	            } else {
	                LazyUtil.showToast("网络没有连接哦");
	            }
	        }
	    }
	
	    private void loadLatestData() {
	        OkUtil.getInstance().okHttpZhihuGson(url, new OkUtil.ResultCallback<ZhihuDetails>
	                () {
	            @Override
	            public void onError(Call call, Exception e) {
	                e.printStackTrace();
	            }
	
	            @Override
	            public void onResponse(ZhihuDetails response, String json) {
	                if (response != null && mCache.isNewResponse(url, json)) {
	                    mTopDetails = response;
	                    mCache.put(url, json);
	                    initAppBarLayout();
	                    loadWebView();
	                }
	            }
	        });
	    }
	
	    private void initAppBarLayout() {
	        ImageUtil.getInstance().displayImage(mTopDetails.getImage(), mTitleImageView);
	        mTitleCollapsingToolbarLayout.setTitle(mTopDetails.getTitle());
	    }
	
	    private void loadWebView() {
	        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news\" " +
	                "type=\"text/css\">";
	        String html = "<html><head>" + css + "</head><body>" + mTopDetails.getBody() + "</body></html>";
	        html = html.replace("<div class=\"img-place-holder\">", "");
	        mContentWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
	    }
	
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
	                finish();
	                return true;
	            default:
	                break;
	        }
	        return super.onOptionsItemSelected(item);
	    }
	
	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        if (mContentWebView != null) {
	            mContentWebView.removeAllViews();
	            mContentWebView.destroy();
	        }
	    }
	
	    @Override
	    protected void onPause() {
	        super.onPause();
	        if (mContentWebView != null) {
	            mContentWebView.onPause();
	        }
	    }
	
	    @Override
	    protected void onResume() {
	        super.onResume();
	        if (mContentWebView != null) {
	            mContentWebView.onResume();
	        }
	    }
	
	    @Override
	    public void onStateChange(int state) {
	        if (state == RevealBackgroundView.STATE_NOT_STARTED) {
	            mContentNestedScrollView.setVisibility(View.GONE);
	            mContentAppBarLayout.setVisibility(View.GONE);
	        }
	        if (state == RevealBackgroundView.STATE_FINISHED) {
	            mContentNestedScrollView.setVisibility(View.VISIBLE);
	            mContentAppBarLayout.setVisibility(View.VISIBLE);
	        }
	    }
	}

知乎接口返回的 json 数据中，包含了页面的 css 样式，而且这个样式是固定的，所以我们完全可以将它写入到本地文件中，css 样式代码如下我就不给了，可以在 json 数据中查看，或者在项目的 app->src->main->assets->css->news 中查看。然后在 webview 加载中将它引入即可。

解决掉后三个点击事件，我们就剩下前面 GankFragment 两个点击事件了，其中，视频跳转我直接启用了系统的页面，具体代码可以直接看上面了，我就不做过多的解释，在后期的迭代中，将会考虑使用 webview 来加载。

最后就是我们的 PictureActivity 了，代码如下：

	public class PictureActivity extends BaseActivity implements View.OnClickListener {
	    public static final String EXTRA_IMAGE_URL = "image_url";
	    public static final String EXTRA_IMAGE_TITLE = "image_title";
	    public static final String TRANSIT_PIC = "picture";
	    private String mImageUrl;
	    private String mName;
	    private ImageView mPicImageView;
	    private Toolbar mTitleToolbar;
	
	    public static Intent newIntent(Context context, String url, String desc) {
	        Intent intent = new Intent(context, PictureActivity.class);
	        intent.putExtra(PictureActivity.EXTRA_IMAGE_URL, url);
	        intent.putExtra(PictureActivity.EXTRA_IMAGE_TITLE, desc);
	        return intent;
	    }
	
	    @TargetApi(Build.VERSION_CODES.KITKAT)
	    @Override
	    protected void initView(Bundle savedInstanceState) {
	        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	        setContentView(R.layout.activity_picture);
	        mTitleToolbar = (Toolbar) findViewById(R.id.tb_title);
	        mPicImageView = (ImageView) findViewById(R.id.iv_pic);
	
	        mTitleToolbar.setNavigationOnClickListener(this);
	        setSupportActionBar(mTitleToolbar);
	        final ActionBar ab = getSupportActionBar();
	        ab.setDisplayHomeAsUpEnabled(true);
	        mPicImageView.setOnClickListener(this);
	
	        ViewCompat.setTransitionName(mPicImageView, TRANSIT_PIC);
	    }
	
	    @Override
	    protected void initData() {
	        super.initData();
	        parseIntent();
	        getSupportActionBar().setTitle(mName);
	
	        loadPic(mImageUrl, mPicImageView);
	    }
	
	    private void parseIntent() {
	        mImageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
	        mName = getIntent().getStringExtra(EXTRA_IMAGE_TITLE);
	    }
	
	    private void loadPic(String imageUrl, ImageView imageView) {
	        ImageUtil.getInstance().displayImage(imageUrl, imageView);
	    }
	
	    @Override
	    public void onClick(View v) {
	        switch (v.getId()) {
	            case R.id.iv_pic:
	                changeToolbar();
	                break;
	            default:
	                break;
	        }
	    }
	
	    private void changeToolbar() {
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
	                ImageUtil.getInstance().saveImage(this, mName, mImageUrl);
	                return true;
	            case R.id.menu_share:
	                LazyUtil.showToast("暂时不支持分享功能哦");
	                return true;
	            default:
	                break;
	        }
	        return super.onOptionsItemSelected(item);
	    }
	}

简单地解释一下：从 MainActivity 中传入图片的 url，然后在 PictureActivity 中解析 intent，获取到传过来的图片的 url 直接使用 U-I-L 来显示该图片即可。至此，v1.0 版本的干乎教程就到此结束了，大致内容我都已经讲解了，如果还有任何问题，欢迎提出 [issure](https://github.com/jokerZLemon/GankOr/issues)，我一定会热心解答。

至此，感谢所有支持干乎的朋友们，接下来我将进行干乎 v1.1 版本的升级。