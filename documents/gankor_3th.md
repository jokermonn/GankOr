# 干乎第三天 #

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