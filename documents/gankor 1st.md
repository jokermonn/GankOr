 # 干乎教程	第一天 #
U-I-L 的使用需要手动下载一个 jar 包	[U-I-L 项目地址](https://github.com/nostra13/Android-Universal-Image-Loader)

其次我们在 ``build.gradle`` 导入相对应的包：

	compile 'com.android.support:support-v4:23.4.0'
	compile 'com.android.support:appcompat-v7:23.4.0'
	// 	友盟导入（注：友盟的使用不在教程范围内）
    compile files('libs/utdid4all-1.0.4.jar')
    compile files('libs/umeng-analytics-v6.0.1.jar')
	// 	UIL 导入
	compile files('libs/universal-image-loader-1.9.5.jar')
    //    内存泄漏检测工具
    debugCompile 'com.squareup.leakcanary:leakcanary-android:1.4-beta1'
    // or 1.4-beta1
    releaseCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.3.1'
    //    官方 material design 控件库
    compile 'com.android.support:design:23.4.0'
    //    OkHttp
    compile 'com.squareup.okhttp3:okhttp:3.4.1'
    //    Gson
    compile 'com.google.code.gson:gson:2.7'
    //    轮播器
    compile 'com.bigkoo:convenientbanner:2.0.5'

完成以上工作就可以对我们的项目进行实现了，首先对 ``AndroidManiFfest.xml`` 写入相对应的权限

	<!-- 网络权限 -->
    <uses-permission android:name="android.permission.INTERNET"/>
    <!-- 读取sd卡 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

对于 U-I-L 框架，我们需要在 Application 中初始化它的配置，这样方便我们在全局中都可以直接使用：

	public class GankOrApplication extends Application {
	    public static Context mContext;
	
	    @Override
	    public void onCreate() {
	        super.onCreate();
	        initImageLoader(getApplicationContext());
	        LeakCanary.install(this);
	        mContext = getApplicationContext();
	    }
	
	    private void initImageLoader(Context context) {
	        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(context);
	        ImageLoader.getInstance().init(configuration);
	    }
	}

U-I-L 有一个默认构建配置的方法，基本上可以满足我们的软件需求，当然 U-I-L 也是支持高度定制自己的配置，详情请[见此](https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Configuration)

软件使用中，我们需要对所接收到的图片进行缓存，同样的 U-I-L 已经帮我们封装的很好，我们可以创建一个自己的工具类，对需要缓存并显示的图片进行简易的封装：

	public class ImageUtil {
	    private static ImageUtil instance = null;
	    private ImageLoader mImageLoader;
	
	    private ImageUtil() {
	        mImageLoader = ImageLoader.getInstance();
	    }
	
	    public static synchronized ImageUtil getInstance() {
	        if (instance == null) {
	            instance = new ImageUtil();
	        }
	        return instance;
	    }
	
	    //    显示图片
	    public void displayImage(String url, ImageView view) {
	        DisplayImageOptions options = new DisplayImageOptions.Builder()
	                .cacheInMemory(true)
	                .cacheOnDisk(true)
	                .build();
	        mImageLoader.displayImage(url, view, options);
	    }
	
		//    添加加载时显示图片，可有效解决快速滑动 recyclerView 异步加载图片错位
	    public void displayImageOnLoading(String url, ImageView view) {
	        DisplayImageOptions options = new DisplayImageOptions.Builder()
	                .cacheInMemory(true)
	                .cacheOnDisk(true)
	                .showImageOnLoading(R.drawable.download_defualt)
	                .showImageForEmptyUri(R.drawable.download_defualt)
	                .build();
	        mImageLoader.displayImage(url, view, options);
	    }
	
	    //    取出缓存图片路径
	    public String getAbsolutePath(String url) {
	        return mImageLoader.getDiskCache().get(url).getAbsolutePath();
	    }
	
	    //    判断是否有缓存
	    public boolean isExist(String url) {
	        return mImageLoader.getDiskCache().get(url).exists();
	    }

	 //      保存图片
	    public void saveImage(Activity activity, String name, String url) {
	        //        6.0 检查权限
	        if (Build.VERSION.SDK_INT >= 23) {
	            int write = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
	            int read = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
	            if (write != PackageManager.PERMISSION_GRANTED || read != PackageManager
	                    .PERMISSION_GRANTED) {
	                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
	                        Manifest
	                        .permission.READ_EXTERNAL_STORAGE}, 300);
	            }
	        }
	        File saveFile = new File(Environment.getExternalStorageDirectory(), "GankOr");
	        if (!saveFile.exists()) {
	            saveFile.mkdirs();
	        }
	        if (copyImage(saveFile.getAbsolutePath() + "//" + name + ".jpg",
	                url)) {
	            LazyUtil.showToast(String.format(activity.getString(R.string.picture_save_on), saveFile
	                    .getAbsolutePath()));
	        } else {
	            LazyUtil.showToast("保存失败");
	        }
	    }

	 	//    保存图片
	    public boolean copyImage(String newPath, String url) {
	        String oldPath = ImageUtil.getInstance().getAbsolutePath(url);
	        InputStream inStream = null;
	        FileOutputStream fs = null;
	        try {
	            int bytesum = 0;
	            int byteread = 0;
	            File oldfile = new File(oldPath);
	            if (oldfile.exists()) { //文件存在时
	                inStream = new FileInputStream(oldPath); //读入原文件
	                fs = new FileOutputStream(newPath);
	                byte[] buffer = new byte[1024];
	                while ((byteread = inStream.read(buffer)) != -1) {
	                    bytesum += byteread; //字节数 文件大小
	                    fs.write(buffer, 0, byteread);
	                }
	            }
	            return true;
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            LazyUtil.close(inStream);
	            LazyUtil.close(fs);
	        }
	        return false;
	    }
	}

简单地构建一个单例模式来获取 ImageLoader 对象，当然，嫌麻烦的可以直接将 ``display()`` 方法设置为 ``static``。在这里新手会很好奇，为什么还要做一遍封装，而不是拿来直接使用 ``ImageLoader.displayImage()`` 诸如此类的呢?现在假设一个场景，假设我当前没有做二次封装，直接使用 ImageLoader，当临时我们需要换掉这个库，例如使用 [picasso](https://github.com/square/picasso) 或者 [glide](https://github.com/bumptech/glide) 等第三方库来替换我们的 ImageLoader，那么我们就要在项目里面，找到所有使用过 ImageLoader 的地方，然后更改参数，对象名等等。但是我做了二次封装之后就不会这样麻烦，我只需要把当前的 ImageUtil 里面的实现改成 [picasso](https://github.com/square/picasso) 或者 [glide](https://github.com/bumptech/glide) 即可，甚至连参数都并不会有很大改动。这就是封装的特性：是指利用抽象数据类型将数据和基于数据的操作封装在一起，使其构成一个不可分割的独立实体，数据被保护在抽象数据类型的内部，尽可能地隐藏内部的细节，只保留一些对外接口使之与外部发生联系。这样说很复杂，简而言之，所有需要调用 ImageUtil 的地方，只需要传入相应的参数，但是具体内部的实现是对外不公布的。

这样我们就可以在相对应需要使用 ImageLoder 的地方调用如下代码：

	ImageUtil.getInstance.displayImage(String url, ImageView view);

同理，我们对 OkHttp 也进行简单地封装：

	public class OkUtil {
	    private static OkUtil instance = null;
	    private Call mCall;
	    private OkHttpClient mOkHttpClient;
	    private Handler mHandler;
	    private Gson mGson;
	
	    private OkUtil() {
	        mOkHttpClient = new OkHttpClient();
	        mHandler = new Handler(Looper.getMainLooper());
	        mGson = new Gson();
	    }
	
	    public static synchronized OkUtil getInstance() {
	        if (instance == null) {
	            instance = new OkUtil();
	        }
	        return instance;
	    }
	
	    //    获取知乎 Gson
	    public void okHttpZhihuGson(String url, final ResultCallback callback) {
	        final Request request = new Request.Builder()
	                .url(API.ZHIHU_BASIC_URL + url)
	                .build();
	        Call call = mOkHttpClient.newCall(request);
	        call.enqueue(new Callback() {
	            @Override
	            public void onFailure(Call call, IOException e) {
	                callback.onError(call, e);
	            }
	
	            @Override
	            public void onResponse(final Call call, Response response) throws IOException {
	                final String string = response.body().string();
	                final Object o = mGson.fromJson(string, callback.mType);
	                mHandler.post(new Runnable() {
	                    @Override
	                    public void run() {
	                        callback.onResponse(o, string);
	                    }
	                });
	            }
	        });
	    }
	
	    //    获取知乎 JsonObject
	    public void okHttpZhihuJObject(String url, final String object, final JObjectCallback callback) {
	        final Request request = new Request.Builder()
	                .url(API.ZHIHU_BASIC_URL + url)
	                .build();
	        Call call = mOkHttpClient.newCall(request);
	        call.enqueue(new Callback() {
	            @Override
	            public void onFailure(Call call, IOException e) {
	                callback.onFailure(call, e);
	            }
	
	            @Override
	            public void onResponse(final Call call, Response response) throws IOException {
	                try {
	                    JSONObject jsonObject = new JSONObject(response.body().string());
	                    final String string = jsonObject.getString(object);
	                    mHandler.post(new Runnable() {
	                        @Override
	                        public void run() {
	                            callback.onResponse(call, string);
	                        }
	                    });
	                } catch (JSONException e) {
	                    e.printStackTrace();
	                }
	            }
	        });
	    }
	
	    //    获取 Gank Gson
	    public void okHttpGankGson(String url, final ResultCallback callback) {
	        final Request request = new Request.Builder()
	                .url(API.GANK_BASIC_URL + url)
	                .build();
	        mCall = mOkHttpClient.newCall(request);
	        mCall.enqueue(new Callback() {
	            @Override
	            public void onFailure(Call call, IOException e) {
	                callback.onError(call, e);
	            }
	
	            @Override
	            public void onResponse(final Call call, Response response) throws IOException {
	                if (response.isSuccessful()) {
	                    final String string = response.body().string();
	                    final Object o = mGson.fromJson(string, callback.mType);
	                    mHandler.post(new Runnable() {
	                        @Override
	                        public void run() {
	                            callback.onResponse(o, string);
	                        }
	                    });
	                }
	            }
	        });
	    }
	
	    //    取消全部网络请求
	    public void cancelAll(OkUtil instance) {
	        if (instance != null && mCall != null && !mCall.isCanceled()) {
	            instance.mCall.cancel();
	        }
	    }
	
	    public interface JObjectCallback {
	        void onFailure(Call call, IOException e);
	
	        void onResponse(Call call, String jObjectUrl);
	    }
	
	    public static abstract class ResultCallback<T> {
	        Type mType;
	
	        public ResultCallback() {
	            mType = getSuperclassTypeParameter(getClass());
	        }
	
	        static Type getSuperclassTypeParameter(Class<?> subclass) {
	            Type superclass = subclass.getGenericSuperclass();
	            if (superclass instanceof Class) {
	                throw new RuntimeException("Missing type parameter.");
	            }
	            ParameterizedType parameterized = (ParameterizedType) superclass;
	
	            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
	        }
	
	        public abstract void onError(Call call, Exception e);
	
	        public abstract void onResponse(T response, String json);
	    }
	}

关于 okhttp 的学习，以及它的封装，鸿洋的这篇[博客](http://blog.csdn.net/lmj623565791/article/details/47911083)还是相当不错的。我也是简单地参考了一部分，对其中的一部分进行了截取，经过简易地封装后，假设我们需要对知乎的 json 进行解析，那么可以这样使用：

	//        获取知乎最新消息
	        OkUtil.getInstance().okHttpZhihuGson(API.ZHIHU_NEWS_LATEST, new OkUtil
	                .ResultCallback<ZhihuDailyNews>() {
	            @Override
	            public void onError(Call call, Exception e) {
	                e.printStackTrace();
	            }
	
	            @Override
	            public void onResponse(ZhihuDailyNews response) {
	                ZhihuDailyNews news = response;
	//                知乎头条消息
	                mTopStories = news.getTopStories();
	            }
	        });
	    }

第一个参数传入相应的 url，第二部分传入一个 ``ResultCallback<实体类>`` 参数就可以回调了

最后，我们再构建一个 ``NetUtil``，检查当前网络是否连接，代码如下：

	public class NetUtil {
	    public static boolean isNetConnect(@NonNull Context context) {
	        ConnectivityManager service = (ConnectivityManager) context.getApplicationContext().getSystemService(Context
	                .CONNECTIVITY_SERVICE);
	        NetworkInfo info = service.getActiveNetworkInfo();
	
	        return info != null && info.isAvailable();
	    }
	}

第三个封装的是缓存类 [ASimpleCache](https://github.com/yangfuhai/ASimpleCache)，首先我们要下载 ASimpleCache 到我们的项目下，接下来封装代码如下：

	/**
	 * 基于 ASimpleCache 的二次封装
	 * Created by joker on 2016/8/14.
	 */
	public class CacheUtil {
	    private static CacheUtil instance = null;
	    private ASimpleCache mCache;
	
	    private CacheUtil(Context context) {
	        mCache = ASimpleCache.get(context);
	    }
	
	    public static synchronized CacheUtil getInstance(Context context) {
	        if (instance == null) {
	            instance = new CacheUtil(context);
	        }
	        return instance;
	    }
	
	    //    ASimpleCache 中的源码
	    public String json2String(String json) {
	        BufferedReader in = null;
	        try {
	            in = new BufferedReader(new StringReader(json));
	            String readString = "";
	            String currentLine;
	            while ((currentLine = in.readLine()) != null) {
	                readString += currentLine;
	            }
	            return readString;
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        } finally {
	            if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	
	    public boolean isCacheEmpty(String key) {
	        return TextUtils.isEmpty(mCache.getAsString(key));
	    }
	
	    public boolean isNewResponse(String key, String newJson) {
	        return (!TextUtils.isEmpty(mCache.getAsString(key)) &&
	                !mCache.getAsString(key).equals(json2String(newJson))) || TextUtils.isEmpty(mCache
	                .getAsString(key));
	    }
	
	    public String getAsString(String key) {
	        return mCache.getAsString(key);
	    }
	
	    public void put(String key, String value) {
	        mCache.put(key, value);
	    }
	}

这是国内一位开发者所构建的第三方库，其实说是库，不如说就是一个文件，它轻量到只有一个 java 文件构成，对于这种小型应用，也是能够基本上满足应用需求了。

然后就是我们的 API 了：

	public class API {
	
	    //    Gank API
	    public static final String GANK_BASIC_URL = "http://gank.io/api/data/";
	    public static final String GANK_WELFARE = "福利/15/";
	    public static final String GANK_VIDEO = "休息视频/15/";
	    public static final int GANK_FIRST_PAGE = 1;
	
	    //    知乎 API
	    public static final String ZHIHU_BASIC_URL = "http://news-at.zhihu.com/api/";
	    public static final String ZHIHU_START = "4/start-image/1080*1776";
	    public static final String ZHIHU_LATEST = "latest";
	    public static final String ZHIHU_BEFORE = "before/";
	    public static final String ZHIHU_NEWS_FOUR = "4/news/";
	    public static final String ZHIHU_NEWS_TWO = "2/news/";
	    public static final String ZHIHU_HOT_NEWS = "3/news/hot";
	}

对于我们常用的几个方法，我也进行了建议封装，取名为 LazyUtil 了，更为大型的项目中，应该严格秉持着单一原则，即一个类只干一件事，充分解耦，LazyUtil 代码如下：

	public class LazyUtil {
	    private static Toast toast;
	
	    //    toast 优化显示
	    public static void showToast(String content) {
	        if (toast == null) {
	            toast = Toast.makeText(GankOrApplication.mContext, content, Toast.LENGTH_SHORT);
	        } else {
	            toast.setText(content);
	        }
	        toast.show();
	    }
	
	    //    Closeable close() 方法封装，增强代码可读性
	    public static void close(Closeable closeable) {
	        try {
	            if (closeable != null) {
	                closeable.close();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	    public static void log(String log) {
	        Log.e("TAG", log);
	    }
	
	    public static void log(String tag, String log) {
	        Log.e(tag, log);
	    }
	}

接下来我们构建基类 Acitivity，这是一个良好的习惯，在该基类中我们主要用于重写一些共有的逻辑，好处是显而易见的对于一些Activity的共有逻辑我们不必要在每个Activity中都重新写一遍，只需要在基类Activity中写一遍就好了。同样地，关于基类 Activity 的学习，我这里也有一篇[文章](http://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650820702&idx=1&sn=f58abdeeb6453d73be2031e5ba736add&scene=23&srcid=0808aE9QWenHbRTqj5rzuOnb#rd)推荐

基类 Activity 代码如下：

	public abstract class BaseActivity extends AppCompatActivity {
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        LazyUtil.log(getClass().getName(), "     onCreate");
	        setActivityState(this);
	
	        initView(savedInstanceState);
	        initData();
	    }
	
	    public void setActivityState(Activity activity) {
	//        设置 APP 只能竖屏显示
	        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
	
	    protected void initData() {
	    }
	
	    protected abstract void initView(Bundle savedInstanceState);
	
	    public boolean isNetConnect() {
	        return NetUtil.isNetConnect(this);
	    }
	
	    @Override
	    protected void onStop() {
	        super.onStop();
	        LazyUtil.log(getClass().getName(), "     onStop");
	    }
	
	    @Override
	    protected void onStart() {
	        super.onStart();
	        LazyUtil.log(getClass().getName(), "     onStart");
	    }
	
	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        LazyUtil.log(getClass().getName(), "     onDestroy");
	    }
	
	    @Override
	    protected void onResume() {
	        super.onResume();
	        LazyUtil.log(getClass().getName(), "     onResume");
	    }
	
	    @Override
	    protected void onRestart() {
	        super.onRestart();
	        LazyUtil.log(getClass().getName(), "     onRestart");
	    }
	
	    @Override
	    protected void onPause() {
	        super.onPause();
	        LazyUtil.log(getClass().getName(), "     onPause");
	    }
	}

我在这里对 Activity 的生命周期进行了覆写，打印相对应的日志，对于后期排解 bug 有很大的帮助。

同样地，我们也构建一个基类 Fragment，代码如下：

	public class BaseFragment extends Fragment {
	
	    protected BaseActivity mActivity;
	
	    public BaseFragment() {
	        // Required empty public constructor
	    }
	
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
	        mActivity = (BaseActivity) getActivity();
	        return super.onCreateView(inflater, container, savedInstanceState);
	    }
	}

我们只是在 onCreateView 方法中获取到了当前与 fragment 相关的 activity，到这里，基类 activity 和 fragment 的封装就已经完成了。对于后期的 activity 和 fragment 基本上继承自基类就可以了。