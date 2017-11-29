package com.example.panpan.panpan_android.net;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Map;


public class GsonSerializer implements ISerializer {

   private Gson mGson;

    //保护的默认构造子
    protected GsonSerializer(){
         mGson  = new Gson();
    }

    private static GsonSerializer mInstance = null;

    public static GsonSerializer getInstance() {

        if (mInstance == null) {
            mInstance = new GsonSerializer();

        }
        return mInstance;
    }

    @Override
    public String toJson(Map object) {
        return mGson.toJson(object);
    }

    @Override
    public String toJson(Object object) {
        return mGson.toJson(object);
    }

    @Override
    public Object fromJson(String json, Class<?> cls) {
        return mGson.fromJson(json, cls);
    }

    @Override
    public Object fromJson(String json, Type type) {
        return mGson.fromJson(json, type);
    }
}
