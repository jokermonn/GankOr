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

这里解释几个地方，其一：在 ``onBindViewHolder()`` 方法中注册点击事件，其二每次刷新数据源调用的 ``addDataMap(HashMap<> dataMap)`` 方法，然后分别取出 map 的键值对放入相应的 List 中刷新数据，其三就是这个 RatioImageView，其实它只是一个对 ImageView 稍稍做了一些改动的 ImageView，将 ImageView 的宽高固定且相等。可以阅读一下这篇文章[你所不知道的Activity转场动画——ActivityOptions](http://www.lxway.com/895445426.htm)，通俗来说，如果在 API>21 中需要使用这种点击动画的效果（点击 ImageView 然后扩充到整个屏幕），那么就要限制 ImageView 的宽高固定且相等。