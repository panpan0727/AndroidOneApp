package com.example.panpan.panpan_android.webapi.response;


public class BaseResponseEase<T> {
    
 /*   *//**
     * 返回码
     *//*
    public int code;
    *//**
     * 错误信息
     *//*
    public String errmsg;
    */



    /***
     * 数据实体
     */
    public T data;

    public T getResultData() {
        return data;
    }
    

}
