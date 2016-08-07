package com.joker.gankor.utils;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by joker on 2016/8/5.
 */
public class OkUtil {
    private static OkUtil instance = null;
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

    //    获取 Gank Gson
    public void OkHttpGankGson(String url, final Class<?> subclass, final GsonCallback callback) {
        final Request request = new Request.Builder()
                .url(API.GANK_BASIC_URL + url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                String string = response.body().string();
                final Object o = new Gson().fromJson(string, subclass);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(call, o);
                    }
                });
            }
        });
    }

    //    获取知乎 Gson
    public void OkHttpZhihuGson(String url, final Class<?> subclass, final GsonCallback callback) {
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
                final Object o = new Gson().fromJson(response.body().string(), subclass);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(call, o);
                    }
                });
            }
        });
    }

    //    获取知乎 JsonObject
    public void OkHttpZhihuJObject(String url, final String object, final JObjectCallback callback) {
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

    public void OkHttpGankGsonTest(String url, final ResultCallback callback) {
        final Request request = new Request.Builder()
                .url(API.GANK_BASIC_URL + url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(null, e);
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                String string = response.body().string();
                final Object o = mGson.fromJson(string, callback.mType);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(o);
                    }
                });
            }
        });
    }

    public interface GsonCallback {
        void onFailure(Call call, IOException e);

        void onResponse(Call call, Object object);
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

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(T response);
    }
}
