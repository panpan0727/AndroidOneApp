package com.example.panpan.panpan_android.utils;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;


public class LogUtils {
    
    public static void init() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag("JCY_LOG")          // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return true;
            }
        });
    }
    
    public static void i(String tag, String msg) {
        Logger.i(msg);
    }
    
    public static void d(String tag, String msg) {
        Logger.d(msg);
    }
    
    public static void d(String msg) {
        Logger.d(msg);
    }
    
    public static void v(String tag, String msg) {
        Logger.v(msg);
    }
    
    public static void e(String tag, String msg) {
        Logger.e(msg);
    }
    
    public static void w(String tag, String msg) {
        Logger.w(msg);
    }
    
    public static void json(String msg) {
        Logger.json(msg);
    }
    
    public static void xml(String tag, String msg) {
        Logger.xml(msg);
    }
}
