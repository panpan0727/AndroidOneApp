package com.example.panpan.panpan_android.net;

import android.support.annotation.NonNull;

import com.example.panpan.panpan_android.entity.SimpleResult1;
import com.example.panpan.panpan_android.webapi.callback.OnRequestCallback;

import java.lang.reflect.Type;


public interface IApiReuqester {

    /**
     * post请求
     *
     * @param requestId   请求id 由业务层生成
     * @param requestCode 请求code
     * @param url         请求地址
     * @param data        data类型数据
     * @param base        基类
     * @param param       请求参数
     * @param listener    回调监听
     * @return request id
     */
  /*  int post(final int requestId
            , final int requestCode
            , final String url
            , @NonNull final Class<?> base
            ,  Class<?> data
            , @NonNull final Object param
            , final OnRequestCallback listener
    );*/
    
    /**
     * post请求
     *
     * @param requestId   请求id 由业务层生成
     * @param requestCode 请求code
     * @param url         请求地址
     * @param data        data类型数据
     * @param base        基类
     * @param param       请求参数
     * @param listener    回调监听
     * @param timeout     超时时间
     * @return request id
     */
    int post(final int requestId
            , final int requestCode
            , final String url
            , final Class<?> data
            , @NonNull final Object param
            , final OnRequestCallback listener
            , final long timeout
    );
    
//    <T> int post(final int requestId, int requestCode, String url, Type cls,Object param,Class response, OnRequestCallback<T> listener);

    /**
     * get请求
     *
     * @param requestId   请求id 由业务层生成
     * @param requestCode 请求code
     * @param url         请求地址
     * @param data        data类型数据
     * @param base        基类
     * @param listener    回调监听
     * @return request id
     */
    int get(final int requestId
            , final int requestCode
            , final String url
            , @NonNull final Class<?> base
            , final Type data
            , final OnRequestCallback listener
    );
    
//    IApiReuqester setConnectTimeout(long timeout);
    
}
