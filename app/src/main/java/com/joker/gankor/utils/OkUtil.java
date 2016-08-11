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
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
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
                            callback.onResponse(o,string);
                        }
                    });
                }
            }
        });
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

        public abstract void onResponse(T response,String json);
    }
}
