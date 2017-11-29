package com.example.panpan.panpan_android.webapi;


import com.example.panpan.panpan_android.constant.ExceptionCode;
import com.example.panpan.panpan_android.webapi.callback.OnRequestCallback;
import com.example.panpan.panpan_android.webapi.response.BaseResponse;


public abstract class BaseApi implements OnRequestCallback {
    
    /**
     * 生成requestId
     */
    private static int mRequestId = 0;
    
    /**
     * 底层请求回调
     */
    
    
    /**
     * 业务层成功逻辑路由处理（对于请求过程成功后进行 code 判断，0，路由到onFailure） //todo: 判断逻辑
     *
     * @param
     * @param requestCode
     * @param response
     */
    @Override
    public void onSuccess( int requestCode, int requestId, Object response) {

    }
    
    /**
     * 业务层成功逻辑路由处理
     *
     * @param responseCode
     * @param requestCode
     * @param errMsg
     */
    @Override
    public void onFailure(int responseCode, int requestCode, int requestId, String errMsg) {

    }
    @Override
    public void onCacheArrived(int requestCode, int requestId, Object response){


    }


    /**
     * 生成访问url
     *
     * @param suffix
     * @return
     */
    protected static String generalUrl(String prefix, String suffix) {
        return String.format("%s%s", prefix, suffix);
    }
    
    /**
     * 生成requestId
     *
     * @return
     */
    protected int generateRequestId() {
        int id = mRequestId++;
        if (mRequestId > 0x7ffffff0)
            mRequestId = 0;
        
        return id;
    }
}
