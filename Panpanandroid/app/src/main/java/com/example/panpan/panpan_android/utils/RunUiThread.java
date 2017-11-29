package com.example.panpan.panpan_android.utils;

import android.os.Handler;
import android.os.Looper;

public class RunUiThread {
    
    public RunUiThread() {
        
    }
    
    public static Message run(Runnable r) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(r);
        return new Message(h);
    }
    
    public static Message run(Runnable r, long delayMillis) {
        Handler h = new Handler(Looper.getMainLooper());
        h.postDelayed(r, delayMillis);
        return new Message(h);
    }
    
    public static class Message {
        private Handler mHandler;
        
        public Message(Handler handler) {
            mHandler = handler;
        }
        
        public void cancel() {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
            }
        }
    }
}
