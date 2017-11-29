package com.example.panpan.panpan_android.net;


import com.example.panpan.panpan_android.net.impl.OkHttpRequester;


public class ApiRequesterFactory {
    
    private static IApiReuqester sInstance;
    
    public static final IApiReuqester getInstance() {
        if (sInstance == null) {
            synchronized (ApiRequesterFactory.class) {
                if (sInstance == null) {
                    sInstance = new OkHttpRequester();
                }
            }
        }
        return sInstance;
    }
}
