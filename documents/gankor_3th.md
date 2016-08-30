# 第三天教程 #

所以理所当然，接下来首先放出 GankFragment 的代码：
	
	/**
	 * A simple {@link Fragment} subclass.
	 * Created by joker on 2016/8/8.
	 */
	public class GankFragment extends ContentFragment {
	    public final static String GANK_WELFARE_JSON = "gank_welfare_json";
	    public final static String GANK_VIDEO_JSON = "gank_video_json";
	    public GankRecyclerAdapter mAdapter;
	    public StaggeredGridLayoutManager manager;
	    private List<GankWelfare.ResultsBean> mWelfare;
	    private HashMap<GankWelfare.ResultsBean, GankWelfare.ResultsBean> dataMap;
	    private int page = API.GANK_FIRST_PAGE;
	
	    public GankFragment() {
	        // Required empty public constructor
	    }
	
		// 
	    @Override
	    protected String getFirstPageUrl() {
	        return String.valueOf(API.GANK_FIRST_PAGE);
	    }
	
	    public void initUrl() {
	        page = API.GANK_FIRST_PAGE;
	    }
	
	    @Override
	    protected void initView(LayoutInflater inflater, ViewGroup container) {
	        dataMap = new LinkedHashMap<GankWelfare.ResultsBean, GankWelfare.ResultsBean>();
	        manager = new StaggeredGridLayoutManager(2,
	                StaggeredGridLayoutManager
	                        .VERTICAL);
	        mContentRecyclerView.setLayoutManager(manager);
	        mAdapter = new GankRecyclerAdapter(mContentRecyclerView
	                .getContext(), dataMap);
	        mAdapter.setImageListener(this);
	        mAdapter.setTextListener(this);
	        mContentRecyclerView.setAdapter(mAdapter);
	        mContentRecyclerView.setPullLoadListener(new PullLoadRecyclerView.onPullLoadListener() {
	            @Override
	            public void onPullLoad() {
	//                上拉加载
	                loadDataFromNet(String.valueOf(++page));
	            }
	        });
	    }
	
	
	    @Override
	    protected void initData() {
	        super.initData();
	
	//        缓存不为空时直接加载缓存，否则在联网情况下加载数据
	        if (!mCache.isCacheEmpty(GANK_WELFARE_JSON) && !mCache.isCacheEmpty(GANK_VIDEO_JSON)) {
	//            取出缓存
	            List<GankWelfare.ResultsBean> welfare = mGson.fromJson(mCache.getAsString(GANK_WELFARE_JSON),
	                    GankWelfare
	                            .class).getResults();
	            List<GankWelfare.ResultsBean> video = mGson.fromJson(mCache.getAsString(GANK_VIDEO_JSON),
	                    GankWelfare
	                            .class).getResults();
	
	            for (int i = 0; i < welfare.size(); i++) {
	                dataMap.put(welfare.get(i), video.get(i));
	            }
	            mAdapter.addDataMap(dataMap);
	        } else {
	            if (isNetConnect()) {
	//                缓存为空联网加载
	                loadDataFromNet(getFirstPageUrl());
	            } else {
	                LazyUtil.showToast("网络没有连接哦");
	            }
	        }
	    }
	
	    @Override
	    public void loadDataFromNet(final String url) {
	        mContentSwipeRefreshLayout.setRefreshing(true);
	        //        Gank 福利图片
	        mOkUtil.okHttpGankGson(API.GANK_WELFARE + url, new OkUtil
	                .ResultCallback<GankWelfare>() {
	            @Override
	            public void onError(Call call, Exception e) {
	                e.printStackTrace();
	            }
	
	            @Override
	            public void onResponse(GankWelfare response, String json) {
	                if (response != null && !response.isError()) {
	                    if (isFirstPage(url)) {
	                        mCache.put(GANK_WELFARE_JSON, json);
	                    }
	                    mWelfare = response.getResults();
	                    loadGankVideo(url);
	                }
	            }
	        });
	    }
	
	    private void loadGankVideo(final String url) {
	        //        Gank 休息视频
	        mOkUtil.okHttpGankGson(API.GANK_VIDEO + url, new OkUtil.ResultCallback<GankWelfare>
	                () {
	            @Override
	            public void onError(Call call, Exception e) {
	                e.printStackTrace();
	            }
	
	            @Override
	            public void onResponse(GankWelfare response, String json) {
	                if (response != null && !response.isError()) {
	                    if (isFirstPage(url)) {
	                        mAdapter.clearList();
	                        mCache.put(GANK_VIDEO_JSON, json);
	                        initUrl();
	                    }
	
	                    List<GankWelfare.ResultsBean> video = response.getResults();
	
	                    dataMap.clear();
	                    for (int i = 0; i < video.size(); i++) {
	                        dataMap.put(mWelfare.get(i), video.get(i));
	                    }
	                    mAdapter.addDataMap(dataMap);
	                }
	                mContentSwipeRefreshLayout.setRefreshing(false);
	                mContentRecyclerView.setIsLoading(false);
	            }
	        });
	    }
	
	    public void setTextListener(GankRecyclerAdapter.TextViewListener textListener) {
	        mTextListener = textListener;
	    }
	
	    public void setImageListener(GankRecyclerAdapter.ImageViewListener imageListener) {
	        mImageListener = imageListener;
	    }
	
	    @Override
	    public void onGankImageClick(View image, String url, String desc) {
	        if (mImageListener != null) {
	            mImageListener.onGankImageClick(image, url, desc);
	        }
	    }
	
	    @Override
	    public void onGankTextClick(String url) {
	        if (mTextListener != null) {
	            mTextListener.onGankTextClick(url);
	        }
	    }
	}

``initView()`` 部分就不解释了，直接看到 ``initData()`` 部分，我们如果是第一次进入（或者在没有缓存的情况下），会进行网络请求，进入 ``loadDataFromNet(getFirstPageUrl());`` 方法，这里需要注意的是，干乎中的图片和文字链接分别来源于两个网络请求，那么这里很重要的一个问题就是：如果我们异步网络请求，那么是图片先拿得到呢？还是文字先拿得到呢？还是有别的什么情况？那有人会说，当然是图片后拿到啊！（至少之前我是这么认为的）。虽然说大多数情况下可能是这样的，但是也是有很多意外情况的，比如服务器脑抽，就是文字返回的慢一些，况且在做项目的过程中，我们需要严谨，杜绝一切可能会出现的 bug。所以这里我的思路是：第一次网络请求后再进行第二次网络请求，第二次网络请求后，将图片和文字以键值对的形式封装进 LinkedHashMap。这样就解决了数据源不同步获取会导致程序崩溃的问题。另外的话，就是在网络获取之后判断一下当前的网络请求是不是做的第一页的网络请求，如果是，我们就进行缓存，如果不是，那么我们就不进行缓存。

另外的话，为了解耦，我将所有 fragment 的点击事件统一交给 MainActivity 来处理。原因：因为要考虑 Fragment 的重复使用，所以必须降低 Fragment 与 Activity 的耦合，而且 Fragment 更不应该直接操作别的 Fragment，毕竟 Fragment 操作应该由它的管理者Activity 来决定——参考自鸿洋的 [Android Fragment 真正的完全解析（下）](http://blog.csdn.net/lmj623565791/article/details/37992017)。所以我们需要在管理 GankFragment 的 MainFragment 中实现 GankFragment 的 ``setTextListener()`` 方法和 ``setImageListener()`` 方法，同时在构建 GankFragment 的时候注册一下它们的点击事件，详情可以参见 [gankor_2nd](./gankor_2nd.md) 中 MainFragment 中那几行我注释的代码。同样的， MainFragment 还是不能处理它的点击事件，要再往上交给 MainActivity 来处理，所以我们在 MainFragment 中添加如下几行代码：

	public void setTextListener(GankRecyclerAdapter.TextViewListener textListener) {
        mTextListener = textListener;
    }

    public void setImageListener(GankRecyclerAdapter.ImageViewListener imageListener) {
        mImageListener = imageListener;
    }

    @Override
    public void onGankImageClick(View image, String url, String desc) {
        if (mImageListener != null) {
            mImageListener.onGankImageClick(image, url, desc);
        }
    }

    @Override
    public void onGankTextClick(String url) {
        if (mTextListener != null) {
            mTextListener.onGankTextClick(url);
        }
    }

最后在 MainActivity 中实现 MainFragment 的 ``setTextListener()`` 方法和 ``setImageListener()`` 方法即可。

GankRecyclerAdapter 代码如下：

	/**
	 * Created by joker on 2016/8/5.
	 */
	public class GankRecyclerAdapter extends RecyclerView.Adapter<GankRecyclerAdapter.ViewHolder> {
	    private LayoutInflater mInflater;
	    private List<GankWelfare.ResultsBean> mWelfare;
	    private List<GankWelfare.ResultsBean> mVideo;
	    private TextViewListener mTextListener;
	    private ImageViewListener mImageListener;
	
	    public GankRecyclerAdapter(Context context, HashMap<GankWelfare
	            .ResultsBean, GankWelfare.ResultsBean> map) {
	        mWelfare = new ArrayList<GankWelfare.ResultsBean>();
	        mVideo = new ArrayList<GankWelfare.ResultsBean>();
	        getMapKV(map);
	        mInflater = LayoutInflater.from(context);
	    }
	
	    @Override
	    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	        return new ViewHolder(mInflater.inflate(R.layout.fragment_gank_rvitem,
	                parent, false));
	    }
	
	    @Override
	    public void onBindViewHolder(final ViewHolder holder, final int position) {
	        ImageUtil.getInstance().displayImageOnLoading(mWelfare.get(position).getUrl(), holder
	                .imageView);
	        holder.textView.setText(mVideo.get(position).getDesc());
	
	        holder.imageView.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                if (mImageListener != null) {
	                    mImageListener.onGankImageClick(holder.imageView, mWelfare.get(position).getUrl(),
	                            mWelfare.get(position).getDesc());
	                }
	            }
	        });
	        holder.textView.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                if (mTextListener != null) {
	                    mTextListener.onGankTextClick(mVideo.get(position).getUrl());
	                }
	            }
	        });
	    }
	
	    @Override
	    public int getItemCount() {
	        return mVideo.size();
	    }
	
	    public void setTextListener(TextViewListener listener) {
	        this.mTextListener = listener;
	    }
	
	    public void setImageListener(ImageViewListener listener) {
	        this.mImageListener = listener;
	    }
	
	    public void addDataMap(HashMap<GankWelfare.ResultsBean, GankWelfare.ResultsBean> dataMap) {
	        getMapKV(dataMap);
	        notifyDataSetChanged();
	    }
	
	    public void clearList() {
	        mVideo.clear();
	        mWelfare.clear();
	    }
	
	    private void getMapKV(HashMap<GankWelfare.ResultsBean, GankWelfare.ResultsBean> map) {
	        for (Map.Entry entry : map.entrySet()) {
	            mWelfare.add((GankWelfare.ResultsBean) entry.getKey());
	            mVideo.add((GankWelfare.ResultsBean) entry.getValue());
	        }
	    }
	
	    public interface TextViewListener {
	        void onGankTextClick(String url);
	    }
	
	    public interface ImageViewListener {
	        void onGankImageClick(View image, String url, String desc);
	    }
	
	    public class ViewHolder extends RecyclerView.ViewHolder {
	        TextView textView;
	        RatioImageView imageView;
	
	        public ViewHolder(View itemView) {
	            super(itemView);
	            textView = (TextView) itemView.findViewById(R.id.tv_content);
	            imageView = (RatioImageView) itemView.findViewById(R.id.iv_content);
	            imageView.setOriginalSize(50, 50);
	        }
	    }
	}

其布局文件代码如下：

	<LinearLayout
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    android:layout_width="match_parent"
	    android:layout_height="wrap_content"
	    android:clickable="true"
	    android:orientation="vertical">
	
	    <com.joker.gankor.view.RatioImageView
	        android:id="@+id/iv_content"
	        android:layout_width="match_parent"
	        android:layout_height="wrap_content"
	        android:adjustViewBounds="true"
	        android:scaleType="centerCrop"/>
	
	    <TextView
	        android:id="@+id/tv_content"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"
	        android:layout_marginTop="10dp"/>
	
	</LinearLayout>

这里解释几个地方，其一：在 ``onBindViewHolder()`` 方法中注册点击事件，其二每次刷新数据源调用的 ``addDataMap(HashMap<> dataMap)`` 方法，然后分别取出 map 的键值对放入相应的 List 中刷新数据，其三就是这个 RatioImageView，其实它只是一个对 ImageView 稍稍做了一些改动的 ImageView，将 ImageView 的宽高固定且相等。可以阅读一下这篇文章[你所不知道的Activity转场动画—— ActivityOptions](http://www.lxway.com/895445426.htm)，通俗来说，如果在 API>21 中需要使用这种点击动画的效果（点击 ImageView 然后扩充到整个屏幕），那么就要限制 ImageView 的宽高固定且相等。

然后就是 GankWelfare，代码如下：

	public class GankWelfare {
	    /**
	     * error : false
	     * results : [{"_id":"57a159ee421aa91e2606476b","createdAt":"2016-08-03T10:41:50.299Z","desc":"8-3",
	     * "publishedAt":"2016-08-03T11:12:47.159Z","source":"chrome","type":"福利","url":"http://ww3.sinaimg
	     * .cn/large/610dc034jw1f6gcxc1t7vj20hs0hsgo1.jpg","used":true,"who":"代码家"},
	     * {"_id":"579ff9d0421aa90d39e709be","createdAt":"2016-08-02T09:39:28.23Z","desc":"8.2",
	     * "publishedAt":"2016-08-02T11:40:01.363Z","source":"chrome","type":"福利","url":"http://ww4.sinaimg
	     * .cn/large/610dc034jw1f6f5ktcyk0j20u011hacg.jpg","used":true,"who":"代码家"},
	     * {"_id":"579eb4b4421aa90d2fc94ba0","createdAt":"2016-08-01T10:32:20.10Z","desc":"8.1",
	     * "publishedAt":"2016-08-01T12:00:57.45Z","source":"chrome","type":"福利","url":"http://ww1.sinaimg
	     * .cn/large/610dc034jw1f6e1f1qmg3j20u00u0djp.jpg","used":true,"who":"代码家"},
	     * {"_id":"579ab0a8421aa90d36e960b4","createdAt":"2016-07-29T09:26:00.838Z","desc":"7.29",
	     * "publishedAt":"2016-07-29T09:37:39.219Z","source":"chrome","type":"福利","url":"http://ww3.sinaimg
	     * .cn/large/610dc034jw1f6aipo68yvj20qo0qoaee.jpg","used":true,"who":"代码家"},
	     * {"_id":"57995869421aa90d43bbf042","createdAt":"2016-07-28T08:57:13.293Z","desc":"葛优躺",
	     * "publishedAt":"2016-07-28T18:17:20.567Z","source":"chrome","type":"福利","url":"http://ww3.sinaimg
	     * .cn/large/610dc034jw1f69c9e22xjj20u011hjuu.jpg","used":true,"who":"代码家"},
	     * {"_id":"57981ee6421aa90d36e96090","createdAt":"2016-07-27T10:39:34.818Z","desc":"王子文",
	     * "publishedAt":"2016-07-27T11:27:16.610Z","source":"chrome","type":"福利","url":"http://ww3.sinaimg
	     * .cn/large/610dc034jw1f689lmaf7qj20u00u00v7.jpg","used":true,"who":"代码家"},
	     * {"_id":"5796b970421aa90d2fc94b4e","createdAt":"2016-07-26T09:14:24.76Z","desc":"今天两个妹子",
	     * "publishedAt":"2016-07-26T10:30:11.357Z","source":"chrome","type":"福利","url":"http://ww3.sinaimg
	     * .cn/large/c85e4a5cjw1f671i8gt1rj20vy0vydsz.jpg","used":true,"who":"代码家"},
	     * {"_id":"5794df0e421aa90d39e70939","createdAt":"2016-07-24T23:30:22.399Z","desc":"7.25",
	     * "publishedAt":"2016-07-25T11:43:57.769Z","source":"chrome","type":"福利","url":"http://ww2.sinaimg
	     * .cn/large/610dc034jw1f65f0oqodoj20qo0hntc9.jpg","used":true,"who":"代码家"},
	     * {"_id":"57918b5c421aa90d2fc94b35","createdAt":"2016-07-22T10:56:28.274Z","desc":"恐龙爪子萌妹子",
	     * "publishedAt":"2016-07-22T11:04:44.305Z","source":"web","type":"福利","url":"http://ww2.sinaimg
	     * .cn/large/c85e4a5cgw1f62hzfvzwwj20hs0qogpo.jpg","used":true,"who":"代码家"},
	     * {"_id":"578f93c4421aa90de83c1bf4","createdAt":"2016-07-20T23:07:48.480Z","desc":"7.21",
	     * "publishedAt":"2016-07-20T16:09:07.721Z","source":"chrome","type":"福利","url":"http://ww4.sinaimg
	     * .cn/large/610dc034jw1f60rw11f5mj20iy0sg0u2.jpg","used":true,"who":"daimajia"}]
	     */
	
	    private boolean error;
	    /**
	     * _id : 57a159ee421aa91e2606476b
	     * createdAt : 2016-08-03T10:41:50.299Z
	     * desc : 8-3
	     * publishedAt : 2016-08-03T11:12:47.159Z
	     * source : chrome
	     * type : 福利
	     * url : http://ww3.sinaimg.cn/large/610dc034jw1f6gcxc1t7vj20hs0hsgo1.jpg
	     * used : true
	     * who : 代码家
	     */
	
	    private List<ResultsBean> results;
	
	    public boolean isError() {
	        return error;
	    }
	
	    public void setError(boolean error) {
	        this.error = error;
	    }
	
	    public List<ResultsBean> getResults() {
	        return results;
	    }
	
	    public void setResults(List<ResultsBean> results) {
	        this.results = results;
	    }
	
	    public static class ResultsBean {
	        @SerializedName("_id")
	        private String id;
	
	        private String createdAt;
	        private String desc;
	        private String publishedAt;
	        private String source;
	        private String type;
	        private String url;
	        private boolean used;
	        private String who;
	
	        public String getId() {
	            return id;
	        }
	
	        public void setId(String id) {
	            this.id = id;
	        }
	
	        public String getCreatedAt() {
	            return createdAt;
	        }
	
	        public void setCreatedAt(String createdAt) {
	            this.createdAt = createdAt;
	        }
	
	        public String getDesc() {
	            return desc;
	        }
	
	        public void setDesc(String desc) {
	            this.desc = desc;
	        }
	
	        public String getPublishedAt() {
	            return publishedAt;
	        }
	
	        public void setPublishedAt(String publishedAt) {
	            this.publishedAt = publishedAt;
	        }
	
	        public String getSource() {
	            return source;
	        }
	
	        public void setSource(String source) {
	            this.source = source;
	        }
	
	        public String getType() {
	            return type;
	        }
	
	        public void setType(String type) {
	            this.type = type;
	        }
	
	        public String getUrl() {
	            return url;
	        }
	
	        public void setUrl(String url) {
	            this.url = url;
	        }
	
	        public boolean isUsed() {
	            return used;
	        }
	
	        public void setUsed(boolean used) {
	            this.used = used;
	        }
	
	        public String getWho() {
	            return who;
	        }
	
	        public void setWho(String who) {
	            this.who = who;
	        }
	    }
	}

实际上就是 gank 接口返回数据的对象，在 Android Studio 中，我们可以使用 [GsonFormat](https://github.com/zzz40500/GsonFormat) 来帮助我们进行解析返回的 json 转换成 javabean，使用起来还是相当的方便，这里另注：Gson 提供 ``@SerializedName`` 注解，可以让我们对字段重命名。

接下来就是 ZhihuDailyNewsFragment 了，其代码如下：

	public class ZhihuDailyNewsFragment extends ContentFragment implements com.bigkoo.convenientbanner.listener
	        .OnItemClickListener,
	        DailyNewsRecyclerAdapter.OnDailyItemClickListener, SwipeRefreshLayout.OnRefreshListener {
	    public final static String DAILY_NEWS_JSON = "daily_news_json";
	    public DailyNewsRecyclerAdapter mAdapter;
	    public String mDate;
	    private ConvenientBanner mShowConvenientBanner;
	    private DailyNewsRecyclerAdapter.OnDailyItemClickListener mItemListener;
	    private List<ZhihuDailyNews.TopStoriesBean> mTopStories;
	    private List<ZhihuDailyNews.StoriesBean> mNewsStories;
	    private OnBannerClickListener mBannerListener;
	
	    public ZhihuDailyNewsFragment() {
	        // Required empty public constructor
	    }
	
	    @Override
	    protected String getFirstPageUrl() {
	        return API.ZHIHU_LATEST;
	    }
	
	    @Override
	    protected void initView(LayoutInflater inflater, ViewGroup container) {
	        mNewsStories = new ArrayList<ZhihuDailyNews.StoriesBean>();
	        mTopStories = new ArrayList<ZhihuDailyNews.TopStoriesBean>();
	        mContentRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
	        View header = inflater.inflate(R.layout.daily_news_header_view, mContentRecyclerView, false);
	        mShowConvenientBanner = (ConvenientBanner) header.findViewById(R.id.cb_show);
	        mAdapter = new DailyNewsRecyclerAdapter(mActivity, mNewsStories);
	        mAdapter.setHeaderView(mShowConvenientBanner);
	        mAdapter.setOnDailyItemClickListener(this);
	        mContentRecyclerView.setAdapter(mAdapter);
	        mContentRecyclerView.setPullLoadListener(new PullLoadRecyclerView.onPullLoadListener() {
	            @Override
	            public void onPullLoad() {
	                loadDataFromNet(API.ZHIHU_BEFORE + mDate);
	            }
	        });
	//        initBanner();
	    }
	
	    public void initBanner() {
	        mShowConvenientBanner.setPages(new CBViewHolderCreator() {
	            @Override
	            public Object createHolder() {
	                return new ZhihuTopNewsHolderView();
	            }
	        }, mTopStories)
	                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
	                .setPageIndicator(new int[]{R.drawable.indicator_gray, R.drawable.indicator_red})
	                .setOnItemClickListener(this);
	//        设置 banner 滑动速度
	        mShowConvenientBanner.setScrollDuration(1500);
	    }
	
	    @Override
	    protected void initData() {
	        super.initData();
	
	        if (!mCache.isCacheEmpty(DAILY_NEWS_JSON)) {
	            ZhihuDailyNews dailyNews = mGson.fromJson(mCache.getAsString(DAILY_NEWS_JSON), ZhihuDailyNews
	                    .class);
	            mTopStories = dailyNews.getTopStories();
	            mDate = dailyNews.getDate();
	//            RecyclerView item 更新
	            mAdapter.addListData(dailyNews.getStories());
	//            RecyclerView 头布局更新
	//            mShowConvenientBanner.notifyDataSetChanged();
	            initBanner();
	        } else {
	            if (isNetConnect()) {
	                loadDataFromNet(API.ZHIHU_LATEST);
	            } else {
	                LazyUtil.showToast("网络没有连接哦");
	            }
	        }
	    }
	
	    @Override
	    public void loadDataFromNet(final String url) {
	        mContentSwipeRefreshLayout.setRefreshing(true);
	        //        获取知乎最新消息
	        mOkUtil.okHttpZhihuGson(API.ZHIHU_NEWS_FOUR + url, new OkUtil
	                .ResultCallback<ZhihuDailyNews>() {
	            @Override
	            public void onError(Call call, Exception e) {
	                e.printStackTrace();
	            }
	
	            @Override
	            public void onResponse(ZhihuDailyNews response, String json) {
	                if (response != null) {
	                    if (isFirstPage(url)) {
	                        if (mCache.isNewResponse(DAILY_NEWS_JSON, json)) {
	                            //  知乎头条消息
	                            mTopStories = response.getTopStories();
	                            //                    mShowConvenientBanner.notifyDataSetChanged();
	                            initBanner();
	                            mCache.put(DAILY_NEWS_JSON, json);
	                        }
	                        mAdapter.clearList();
	                    }
	                    mDate = response.getDate();
	                    //  最新消息
	                    mNewsStories = response.getStories();
	                    mAdapter.addListData(mNewsStories);
	                    mContentSwipeRefreshLayout.setRefreshing(false);
	                    mContentRecyclerView.setIsLoading(false);
	                }
	            }
	        });
	    }
	
	    // 开始自动翻页
	    @Override
	    public void onResume() {
	        super.onResume();
	        mShowConvenientBanner.startTurning(4500);
	    }
	
	    // 暂停自动翻页
	    @Override
	    public void onPause() {
	        super.onPause();
	        mShowConvenientBanner.stopTurning();
	    }
	
	    @Override
	    public void setUserVisibleHint(boolean isVisibleToUser) {
	        super.setUserVisibleHint(isVisibleToUser);
	        if (isVisibleToUser && isDataLoaded && isViewCreated) {
	            mShowConvenientBanner.startTurning(4000);
	        }
	        if (!isVisibleToUser && isViewCreated) {
	            mShowConvenientBanner.stopTurning();
	        }
	    }
	
	    //    Banner 点击事件
	    @Override
	    public void onItemClick(int position) {
	        if (mBannerListener != null) {
	            mBannerListener.onBannerClickListener(mTopStories.get(position));
	        }
	    }
	
	    public void setOnItemClickListener(DailyNewsRecyclerAdapter.OnDailyItemClickListener itemClickListener) {
	        mItemListener = itemClickListener;
	    }
	
	    public void setOnBannerClickListener(OnBannerClickListener bannerClickListener) {
	        mBannerListener = bannerClickListener;
	    }
	
	    @Override
	    public void onZhihuDailyItemClick(View view, ZhihuDailyNews.StoriesBean bean) {
	        if (mItemListener != null) {
	            mItemListener.onZhihuDailyItemClick(view, bean);
	        }
	    }
	
	    public interface OnBannerClickListener {
	        void onBannerClickListener(ZhihuDailyNews.TopStoriesBean topStories);
	    }
	}

这里用到了一个轮播器的第三方库 [Android-ConvenientBanner](https://github.com/saiwu-bigkoo/Android-ConvenientBanner)，使用起来也是相当的简单，直接看一下作者的 demo 就可以啦。放上轮播器的 adapter 代码：

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

另外RecyclerView 不像 ListView 那般可以直接添加 HeaderView，所以我们需要从它的 adapter 下手，其 adapter 代码如下：

	public class DailyNewsRecyclerAdapter extends RecyclerView.Adapter<DailyNewsRecyclerAdapter.ViewHolder> {
	    private static final int TYPE_ITEM = 0;
	    private static final int TYPE_HEADER = 1;
	    private List<ZhihuDailyNews.StoriesBean> mBean;
	    private LayoutInflater mInflater;
	    private OnDailyItemClickListener mListener;
	    private View mHeaderView;
	
	    public DailyNewsRecyclerAdapter(Context context, List<ZhihuDailyNews.StoriesBean> storiesBeen) {
	        mBean = storiesBeen;
	        mInflater = LayoutInflater.from(context);
	    }
	
	    @Override
	    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	        if (viewType == TYPE_HEADER) {
	            return new ViewHolder(mHeaderView);
	        }
	        return new ViewHolder(mInflater.inflate(R.layout.zhihu_news_item, parent, false));
	    }
	
	    @Override
	    public void onBindViewHolder(ViewHolder holder, int position) {
	        if (getItemViewType(position) == TYPE_HEADER) {
	            return;
	        }
	        final int newPosition = getNewPosition(holder);
	        ImageUtil.getInstance().displayImageOnLoading(mBean.get(newPosition).getImages().get(0), holder
	                .mImageView);
	        holder.mTextView.setText(mBean.get(newPosition).getTitle());
	        holder.item.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                if (mListener != null) {
	                    mListener.onZhihuDailyItemClick(v, mBean.get(newPosition));
	                }
	            }
	        });
	    }
	
	    @Override
	    public int getItemViewType(int position) {
	        return mHeaderView != null && position == 0 ? TYPE_HEADER : TYPE_ITEM;
	    }
	
	    //    添加头布局后，count 应该 +1
	    @Override
	    public int getItemCount() {
	        return mHeaderView == null ? mBean.size() : mBean.size() + 1;
	    }
	
	    //    添加头布局后的新 position
	    public int getNewPosition(RecyclerView.ViewHolder viewHolder) {
	        int position = viewHolder.getAdapterPosition();
	        return mHeaderView == null ? position : position - 1;
	    }
	
	    public void addListData(List<ZhihuDailyNews.StoriesBean> bean) {
	        mBean.addAll(bean);
	        notifyDataSetChanged();
	    }
	
	    public void clearList() {
	        mBean.clear();
	    }
	
	    public void setHeaderView(View headerView) {
	        mHeaderView = headerView;
	    }
	
	    public void setOnDailyItemClickListener(OnDailyItemClickListener listener) {
	        mListener = listener;
	    }
	
	    public interface OnDailyItemClickListener {
	        void onZhihuDailyItemClick(View view, ZhihuDailyNews.StoriesBean bean);
	    }
	
	    public class ViewHolder extends RecyclerView.ViewHolder {
	        TextView mTextView;
	        ImageView mImageView;
	        View item;
	
	        public ViewHolder(View itemView) {
	            super(itemView);
	            if (itemView == mHeaderView) {
	                return;
	            }
	            mTextView = (TextView) itemView.findViewById(R.id.tv_item);
	            mImageView = (ImageView) itemView.findViewById(R.id.iv_item);
	            item = itemView;
	        }
	    }
	}

其布局文件代码如下：

	<RelativeLayout
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    android:layout_width="match_parent"
	    android:layout_height="wrap_content"
	    android:background="@color/white"
	    android:orientation="horizontal"
	    android:padding="10dp">
	
	    <ImageView
	        android:id="@+id/iv_item"
	        android:layout_width="80dp"
	        android:layout_height="80dp"
	        android:layout_centerVertical="true"
	        android:scaleType="centerCrop"/>
	
	    <TextView
	        android:id="@+id/tv_item"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:layout_alignParentEnd="true"
	        android:layout_alignParentRight="true"
	        android:layout_alignParentTop="true"
	        android:layout_toRightOf="@id/iv_item"
	        android:paddingLeft="8dp"
	        android:textSize="18sp"/>
	
	</RelativeLayout>

adapter 的代码也是相当的简单，当然，如果你有任何问题提出 [issure](https://github.com/jokerZLemon/GankOr/issues) 即可，我一定耐心解答。

ZhihuDailyNews 代码如下：

	/**
	 * Created by joker on 2016/8/8.
	 */
	public class ZhihuDailyNews {
	
	    /**
	     * date : 20160808
	     * stories : [{"images":["http://pic1.zhimg.com/c27847ba9ac7c11ce195bc5155357bf4.jpg"],"type":0,
	     * "id":8656865,"ga_prefix":"080809","title":"从经济学角度来看，「走一步看一步」是不是能达到最优？"},{"images":["http://pic1
	     * .zhimg.com/e75c9b74b748ada732ef5301b2c6b518.jpg"],"type":0,"id":8660318,"ga_prefix":"080808",
	     * "title":"如果身体里有一个细胞癌变了，就一定会发展成癌症吗？"},{"images":["http://pic2.zhimg
	     * .com/497ecbb84920b655151a1142b63b9675.jpg"],"type":0,"id":8660020,"ga_prefix":"080807",
	     * "title":"理想中的商业模式，应该是什么样的？"},{"title":"英文阅读中，有哪些值得注意的文化背景知识？","ga_prefix":"080807",
	     * "images":["http://pic3.zhimg.com/c551af07c0ecfec4c8137fa1660bd206.jpg"],"multipic":true,"type":0,
	     * "id":8660423},{"images":["http://pic3.zhimg.com/41e17842513870249afa6747d192fa26.jpg"],"type":0,
	     * "id":8658498,"ga_prefix":"080807","title":"印度人口多，经济增长快，为什么奥运会上表现不太好？"},{"images":["http://pic4
	     * .zhimg.com/f84dc12935112fe36dc8d0a2db71a91f.jpg"],"type":0,"id":8660448,"ga_prefix":"080807",
	     * "title":"读读日报 24 小时热门 TOP 5 · 地铁公交上班族如何读书？"},{"images":["http://pic3.zhimg
	     * .com/29aa1a049b2408bbf4211be4e49c1a7a.jpg"],"type":0,"id":8658968,"ga_prefix":"080806","title":"瞎扯
	     * · 如何正确地吐槽"}]
	     * top_stories : [{"image":"http://pic1.zhimg.com/988f127baf4dd0885e54994e5c2d8a08.jpg","type":0,
	     * "id":8660448,"ga_prefix":"080807","title":"读读日报 24 小时热门 TOP 5 · 地铁公交上班族如何读书？"},
	     * {"image":"http://pic2.zhimg.com/782daacef0bc8fb35c00afc45f6d8145.jpg","type":0,"id":8660318,
	     * "ga_prefix":"080808","title":"如果身体里有一个细胞癌变了，就一定会发展成癌症吗？"},{"image":"http://pic4.zhimg
	     * .com/cc0f6aa9ff5b1dd1f76c9d6abb9d2b63.jpg","type":0,"id":8658538,"ga_prefix":"080718",
	     * "title":"整点儿奥运 · 自打看了奥运会，心脏一下就强健了"},{"image":"http://pic4.zhimg
	     * .com/b2aa14b50213da6d6d2e5c6a96a07d03.jpg","type":0,"id":8659110,"ga_prefix":"080717",
	     * "title":"知乎好问题 · 独处的时候，如何保持自律？"},{"image":"http://pic2.zhimg.com/e15f4d8396a1573928fa510e711046e5
	     * .jpg","type":0,"id":8652741,"ga_prefix":"080715","title":"《玩具总动员 3》里还有龙猫彩蛋？这可是迪士尼自己说的"}]
	     */
	
	    private String date;
	    /**
	     * images : ["http://pic1.zhimg.com/c27847ba9ac7c11ce195bc5155357bf4.jpg"]
	     * type : 0
	     * id : 8656865
	     * ga_prefix : 080809
	     * title : 从经济学角度来看，「走一步看一步」是不是能达到最优？
	     */
	
	    private List<StoriesBean> stories;
	    /**
	     * image : http://pic1.zhimg.com/988f127baf4dd0885e54994e5c2d8a08.jpg
	     * type : 0
	     * id : 8660448
	     * ga_prefix : 080807
	     * title : 读读日报 24 小时热门 TOP 5 · 地铁公交上班族如何读书？
	     */
	
	    @SerializedName("top_stories")
	    private List<TopStoriesBean> topStories;
	
	    public String getDate() {
	        return date;
	    }
	
	    public void setDate(String date) {
	        this.date = date;
	    }
	
	    public List<StoriesBean> getStories() {
	        return stories;
	    }
	
	    public void setStories(List<StoriesBean> stories) {
	        this.stories = stories;
	    }
	
	    public List<TopStoriesBean> getTopStories() {
	        return topStories;
	    }
	
	    public void setTopStories(List<TopStoriesBean> topStories) {
	        this.topStories = topStories;
	    }
	
	    public static class StoriesBean {
	        private int type;
	        private int id;
	        private String title;
	        private List<String> images;
	
	        public int getType() {
	            return type;
	        }
	
	        public void setType(int type) {
	            this.type = type;
	        }
	
	        public int getId() {
	            return id;
	        }
	
	        public void setId(int id) {
	            this.id = id;
	        }
	
	        public String getTitle() {
	            return title;
	        }
	
	        public void setTitle(String title) {
	            this.title = title;
	        }
	
	        public List<String> getImages() {
	            return images;
	        }
	
	        public void setImages(List<String> images) {
	            this.images = images;
	        }
	    }
	
	    public static class TopStoriesBean {
	        private String image;
	        private int type;
	        private int id;
	        private String title;
	
	        public String getImage() {
	            return image;
	        }
	
	        public void setImage(String image) {
	            this.image = image;
	        }
	
	        public int getType() {
	            return type;
	        }
	
	        public void setType(int type) {
	            this.type = type;
	        }
	
	        public int getId() {
	            return id;
	        }
	
	        public void setId(int id) {
	            this.id = id;
	        }
	
	        public String getTitle() {
	            return title;
	        }
	
	        public void setTitle(String title) {
	            this.title = title;
	        }
	    }
	}

那么 HotNewsFragment 的代码更是简单了：

	/**
	 * A simple {@link Fragment} subclass.
	 */
	public class ZhihuHotNewsFragment extends ContentFragment implements SwipeRefreshLayout.OnRefreshListener,
	        HotNewsRecyclerAdapter.OnHotItemClickListener {
	    public final static String HOT_NEWS_JSON = "hot_news_json";
	    public HotNewsRecyclerAdapter mAdapter;
	    private HotNewsRecyclerAdapter.OnHotItemClickListener mItemListener;
	
	    public ZhihuHotNewsFragment() {
	        // Required empty public constructor
	    }
	
	    @Override
	    protected String getFirstPageUrl() {
	        return "";
	    }
	
	    @Override
	    protected void initView(LayoutInflater inflater, ViewGroup container) {
	        List<ZhihuHotNews.RecentBean> mRecent = new ArrayList<ZhihuHotNews.RecentBean>();
	        mContentRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
	        mAdapter = new HotNewsRecyclerAdapter(mActivity, mRecent);
	        mAdapter.setOnHotItemClickListener(this);
	        mContentRecyclerView.setAdapter(mAdapter);
	    }
	
	    @Override
	    protected void initData() {
	        super.initData();
	
	        if (!mCache.isCacheEmpty(HOT_NEWS_JSON)) {
	            mAdapter.changeListData(mGson.fromJson(mCache.getAsString(HOT_NEWS_JSON), ZhihuHotNews
	                    .class).getRecent());
	        } else {
	            if (isNetConnect()) {
	                loadDataFromNet("");
	            } else {
	                LazyUtil.showToast("网络没有连接哦");
	            }
	        }
	    }
	
	    @Override
	    public void loadDataFromNet(String url) {
	        //        获取知乎热门消息
	        mOkUtil.okHttpZhihuGson(API.ZHIHU_HOT_NEWS, new OkUtil.ResultCallback<ZhihuHotNews>() {
	                    @Override
	                    public void onError(Call call, Exception e) {
	                        e.printStackTrace();
	                    }
	
	                    @Override
	                    public void onResponse(ZhihuHotNews response, String json) {
	                        if (response != null) {
	                            if (mCache.isNewResponse(HOT_NEWS_JSON, json)) {
	                                mCache.put(HOT_NEWS_JSON, json);
	                                mAdapter.changeListData(response.getRecent());
	                            }
	                        }
	                        mContentSwipeRefreshLayout.setRefreshing(false);
	                    }
	                }
	        );
	    }
	
	    @Override
	    public void onZhihuHotItemClick(View view, ZhihuHotNews.RecentBean bean) {
	        if (mItemListener != null) {
	            mItemListener.onZhihuHotItemClick(view, bean);
	        }
	    }
	
	    public void setOnItemClickListener(HotNewsRecyclerAdapter.OnHotItemClickListener itemClickListener) {
	        mItemListener = itemClickListener;
	    }
	}

adapter 代码如下：

	/**
	 * Created by joker on 2016/8/14.
	 */
	public class HotNewsRecyclerAdapter extends RecyclerView.Adapter<HotNewsRecyclerAdapter.ViewHolder> {
	    private List<ZhihuHotNews.RecentBean> mBean;
	    private LayoutInflater mInflater;
	    private OnHotItemClickListener mListener;
	
	    public HotNewsRecyclerAdapter(Context context, List<ZhihuHotNews.RecentBean> bean) {
	        mBean = bean;
	        mInflater = LayoutInflater.from(context);
	    }
	
	    @Override
	    public HotNewsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	        return new ViewHolder(mInflater.inflate(R.layout.zhihu_news_item, parent, false));
	    }
	
	    @Override
	    public void onBindViewHolder(HotNewsRecyclerAdapter.ViewHolder holder, final int position) {
	        ImageUtil.getInstance().displayImageOnLoading(mBean.get(position).getThumbnail(), holder
	                .mImageView);
	        holder.mTextView.setText(mBean.get(position).getTitle());
	        holder.item.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                if (mListener != null) {
	                    mListener.onZhihuHotItemClick(v, mBean.get(position));
	                }
	            }
	        });
	    }
	
	    @Override
	    public int getItemCount() {
	        return mBean.size();
	    }
	
	    public void changeListData(List<ZhihuHotNews.RecentBean> bean) {
	        mBean = bean;
	        notifyDataSetChanged();
	    }
	
	    public void setOnHotItemClickListener(OnHotItemClickListener listener) {
	        mListener = listener;
	    }
	
	    public interface OnHotItemClickListener {
	        void onZhihuHotItemClick(View view, ZhihuHotNews.RecentBean bean);
	    }
	
	    public class ViewHolder extends RecyclerView.ViewHolder {
	        TextView mTextView;
	        ImageView mImageView;
	        View item;
	
	        public ViewHolder(View itemView) {
	            super(itemView);
	            mTextView = (TextView) itemView.findViewById(R.id.tv_item);
	            mImageView = (ImageView) itemView.findViewById(R.id.iv_item);
	            item = itemView;
	        }
	    }
	}

布局文件如下：

	<RelativeLayout
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    android:layout_width="match_parent"
	    android:layout_height="wrap_content"
	    android:background="@color/white"
	    android:orientation="horizontal"
	    android:padding="10dp">
	
	    <ImageView
	        android:id="@+id/iv_item"
	        android:layout_width="80dp"
	        android:layout_height="80dp"
	        android:layout_centerVertical="true"
	        android:scaleType="centerCrop"/>
	
	    <TextView
	        android:id="@+id/tv_item"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:layout_alignParentEnd="true"
	        android:layout_alignParentRight="true"
	        android:layout_alignParentTop="true"
	        android:layout_toRightOf="@id/iv_item"
	        android:paddingLeft="8dp"
	        android:textSize="18sp"/>
	
	</RelativeLayout>

ZhihuHotNews 代码如下：

	/**
	 * Created by joker on 2016/8/8.
	 */
	public class ZhihuHotNews {
	
	    /**
	     * news_id : 8649660
	     * url : http://news-at.zhihu.com/api/2/news/8649660
	     * thumbnail : http://pic4.zhimg.com/67a1d21a65421fe2ab5fcb1ad4ea7087.jpg
	     * title : 名字里有「莲」，长得也很像莲，但睡莲真的不是莲
	     */
	
	    private List<RecentBean> recent;
	
	    public List<RecentBean> getRecent() {
	        return recent;
	    }
	
	    public void setRecent(List<RecentBean> recent) {
	        this.recent = recent;
	    }
	
	    public static class RecentBean {
	        @SerializedName("news_id")
	        private int newsId;
	        private String url;
	        private String thumbnail;
	        private String title;
	
	        public int getNewsId() {
	            return newsId;
	        }
	
	        public void setNewsId(int newsId) {
	            this.newsId = newsId;
	        }
	
	        public String getUrl() {
	            return url;
	        }
	
	        public void setUrl(String url) {
	            this.url = url;
	        }
	
	        public String getThumbnail() {
	            return thumbnail;
	        }
	
	        public void setThumbnail(String thumbnail) {
	            this.thumbnail = thumbnail;
	        }
	
	        public String getTitle() {
	            return title;
	        }
	
	        public void setTitle(String title) {
	            this.title = title;
	        }
	    }
	}