package com.example.panpan.panpan_android.net.impl;

public abstract class BaseRequester {
    
    /**
     * 生成requestId
     */
    private int mRequestId = 0;
    
    protected int generateRequestId() {
        int id = mRequestId++;
        if (mRequestId > 0x7ffffff0)
            mRequestId = 0;
        
        return id;
    }
}
