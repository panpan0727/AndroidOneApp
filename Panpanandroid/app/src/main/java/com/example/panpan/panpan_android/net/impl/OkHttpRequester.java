package com.example.panpan.panpan_android.net.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.panpan.panpan_android.constant.ExceptionCode;
import com.example.panpan.panpan_android.net.CertificatesManager;
import com.example.panpan.panpan_android.net.GsonSerializer;
import com.example.panpan.panpan_android.net.IApiReuqester;
import com.example.panpan.panpan_android.net.ParameterizedTypeImpl;
import com.example.panpan.panpan_android.net.SSLCertificatesInit;
import com.example.panpan.panpan_android.net.SerializerFactory;
import com.example.panpan.panpan_android.utils.LogUtils;
import com.example.panpan.panpan_android.utils.RunUiThread;
import com.example.panpan.panpan_android.utils.Utility;
import com.example.panpan.panpan_android.webapi.callback.OnRequestCallback;
import com.example.panpan.panpan_android.webapi.response.JsonParser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OkHttpRequester extends BaseRequester implements IApiReuqester {

    private static final String ENCODE_UTF8 = "UTF-8";
    private static final String CONTENT_TYPE = "application/json";
    private static final String GZIP = "gzip";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String DEBUG_FORMAT = "RESP CODE: %1$s, RESQ CODE %2$s, JSON:%3$s, EXCEPTION:%4$s";

    /**
     * K: 超时时间
     * V: 对应的OkHttpClient实例
     */
    private Map<Long, OkHttpClient> mInstanceMap = new Hashtable<>();

    private OkHttpClient getInstance(long timeout) {
        if (mInstanceMap.containsKey(new Long(timeout))) {
            return mInstanceMap.get(new Long(timeout));
        } else {
            SSLSocketFactory[] socketFactory = new SSLSocketFactory[1];
            X509TrustManager[] trustManager = new X509TrustManager[1];
            SSLCertificatesInit.init(socketFactory, trustManager, CertificatesManager.getPayCerInputStream());
            OkHttpClient client = new OkHttpClient()
                    .newBuilder()
                    .sslSocketFactory(socketFactory[0], trustManager[0])
                    .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                    .readTimeout(timeout, TimeUnit.MILLISECONDS)
                    .build();
            mInstanceMap.put(new Long(timeout), client);
            return client;
        }
    }


    @Override
    public int post(final int requestId, final int requestCode, final String url, final Class<?> data, @NonNull final Object param, final OnRequestCallback listener, final long timeout) {
        try {
            RequestBody requestBody = RequestBody.create(JSON, GsonSerializer.getInstance().toJson(param));
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .addHeader("content-type", CONTENT_TYPE)
                    .post(requestBody);
            Request request = builder.build();

            Log.d("url", url);

            getInstance(timeout).newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(final Call call, final IOException e) {
                    try {
                        if (call.request().body() != null) {
                            Log.d("ApiRequester", String.format(DEBUG_FORMAT, String.valueOf("0"), String.valueOf(requestCode), String.valueOf(call.request().body().toString()), ""));
                        }
                        RunUiThread.run(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    try {
                                        listener.onFailure(ExceptionCode.NO_INTERNET, requestCode, requestId, "数据请求失败");
                                    } catch (Exception e) {
                                        Log.d("ApiRequester", String.format(DEBUG_FORMAT, String.valueOf("0"), String.valueOf(requestCode), "", e.getMessage()));
                                    }
                                }
                            }
                        });
                    } catch (final Exception e1) {
                        Log.d("ApiRequester", String.format(DEBUG_FORMAT, String.valueOf("-1"), String.valueOf(requestCode), String.valueOf(e1.getMessage()), ""));
                        RunUiThread.run(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    try {
                                        listener.onFailure(ExceptionCode.THROW_EXCEPTION, requestCode, requestId, "数据请求失败");
                                    } catch (Exception e) {
                                        Log.d("ApiRequester", String.format(DEBUG_FORMAT, String.valueOf("0"), String.valueOf(requestCode), "", e.getMessage()));
                                    }
                                }
                            }
                        });
                    }
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    try {
                      final String json = response.body() == null ? "null" : response.body().string();
                        LogUtils.json(json);
                        final Object respObj;
                        Class realData = data;
                        if (realData == null) realData = Object.class;
                        //  respObj = SerializerFactory.getInstance().fromJson(json, new ParameterizedTypeImpl(new Type[]{realData}, null, base));

                        respObj = JsonParser.getInstance().fromJson(json, realData);

                        RunUiThread.run(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    if (200 == response.code()) {
                                        listener.onSuccess(requestCode, requestId, respObj);
                                        Utility.logJson(json,url);
                                    } else if (response.code() >= 500) {
                                        listener.onFailure(response.code(), requestCode, requestId, "服务器错误");
                                    } else {
                                        listener.onFailure(response.code(), requestCode, requestId, "网络错误");
                                    }
                                }
                            }
                        });
                    } catch (final Exception e2) {
                        Log.d("ApiRequester", "Response Exception -> " + String.format(DEBUG_FORMAT, String.valueOf(response.code()), String.valueOf(requestCode), "null", String.valueOf(e2.getMessage())));
                        RunUiThread.run(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    try {
                                        listener.onFailure(ExceptionCode.THROW_EXCEPTION, requestCode, requestId, "数据解析异常");
                                    } catch (Exception e) {
                                        Log.d("ApiRequester", String.format(DEBUG_FORMAT, String.valueOf("0"), String.valueOf(requestCode), "", e.getMessage()));
                                    }
                                }
                            }
                        });
                    }
                }
            });
        } catch (final Exception e3) {
            LogUtils.d("ApiRequester", String.format(DEBUG_FORMAT, "", String.valueOf(requestCode), String.valueOf(e3.getMessage()), ""));
            RunUiThread.run(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        try {
                            listener.onFailure(ExceptionCode.THROW_EXCEPTION, requestCode, requestId, e3.getMessage());
                        } catch (Exception e) {
                            LogUtils.d("ApiRequester", String.format(DEBUG_FORMAT, String.valueOf("0"), String.valueOf(requestCode), "", e.getMessage()));
                        }
                    }
                }
            });
        }
        return requestId;
    }


    @Override
    public int get(final int requestId, final int requestCode, final String url, @NonNull final Class<?> base, final Type data, final OnRequestCallback listener) {
        try {
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .addHeader("content-type", CONTENT_TYPE);
            Request request = builder.build();
            LogUtils.d("url", url);
            getInstance(Long.parseLong("10000")).newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(final Call call, final IOException e) {
                    try {
                        if (call.request().body() != null) {
                            LogUtils.d("ApiRequester", String.format(DEBUG_FORMAT, String.valueOf("0"), String.valueOf(requestCode), String.valueOf(call.request().body().toString()), ""));
                        }
                        RunUiThread.run(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    try {
                                        listener.onFailure(ExceptionCode.NO_INTERNET, requestCode, requestId, "数据请求失败");
                                    } catch (Exception e) {
                                        LogUtils.d("ApiRequester", String.format(DEBUG_FORMAT, String.valueOf("0"), String.valueOf(requestCode), "", e.getMessage()));
                                    }
                                }
                            }
                        });
                    } catch (final Exception e1) {
                        LogUtils.d("ApiRequester", String.format(DEBUG_FORMAT, String.valueOf("-1"), String.valueOf(requestCode), String.valueOf(e1.getMessage()), ""));
                        RunUiThread.run(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    try {
                                        listener.onFailure(ExceptionCode.THROW_EXCEPTION, requestCode, requestId, "数据请求失败");
                                    } catch (Exception e) {
                                        LogUtils.d("ApiRequester", String.format(DEBUG_FORMAT, String.valueOf("0"), String.valueOf(requestCode), "", e.getMessage()));
                                    }
                                }
                            }
                        });
                    }
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    try {
                        String json = response.body() == null ? "null" : response.body().string();
                        LogUtils.json(json);
                        final Object respObj;
                        Type realData = data;
                        if (realData == null) realData = Object.class;
                        respObj = SerializerFactory.getInstance().fromJson(json, new ParameterizedTypeImpl(new Type[]{realData}, null, base));
                        RunUiThread.run(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    try {
                                        if (200 == response.code()) {
                                            listener.onSuccess(requestCode, requestId, respObj);
                                        } else if (response.code() >= 500) {
                                            listener.onFailure(response.code(), requestCode, requestId, "服务器错误");
                                        } else {
                                            listener.onFailure(response.code(), requestCode, requestId, "网络错误");
                                        }
                                    } catch (Exception e) {
                                        LogUtils.d("ApiRequester", String.format(DEBUG_FORMAT, String.valueOf("0"), String.valueOf(requestCode), "", e.getMessage()));
                                    }
                                }
                            }
                        });
                    } catch (final Exception e2) {
                        LogUtils.d("ApiRequester", "Response Exception -> " + String.format(DEBUG_FORMAT, String.valueOf(response.code()), String.valueOf(requestCode), "null", String.valueOf(e2.getMessage())));
                        RunUiThread.run(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    try {
                                        listener.onFailure(ExceptionCode.THROW_EXCEPTION, requestCode, requestId, "数据解析异常");
                                    } catch (Exception e) {
                                        LogUtils.d("ApiRequester", String.format(DEBUG_FORMAT, String.valueOf("0"), String.valueOf(requestCode), "", e.getMessage()));
                                    }
                                }
                            }
                        });
                    }
                }
            });
        } catch (final Exception e3) {
            LogUtils.d("ApiRequester", String.format(DEBUG_FORMAT, "", String.valueOf(requestCode), String.valueOf(e3.getMessage()), ""));
            RunUiThread.run(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        try {
                            listener.onFailure(ExceptionCode.THROW_EXCEPTION, requestCode, requestId, e3.getMessage());
                        } catch (Exception e) {
                            LogUtils.d("ApiRequester", String.format(DEBUG_FORMAT, String.valueOf("0"), String.valueOf(requestCode), "", e.getMessage()));
                        }
                    }
                }
            });
        }
        return requestId;
    }

}
