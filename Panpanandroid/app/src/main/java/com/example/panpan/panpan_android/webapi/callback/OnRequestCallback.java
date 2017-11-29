package com.example.panpan.panpan_android.webapi.callback;



public interface OnRequestCallback {
    
    /**
     * 网络请求缓存处理
     *
     * @param requestCode
     * @param requestId
     * @param response
     */
    void onCacheArrived(int requestCode, int requestId, Object response);
    
    /**
     * 网络成功请求回调
     *
     * @param requestCode
     * @param requestId
     * @param response
     */
    void onSuccess(int requestCode, int requestId, Object response);
    
    /**
     * 网络请求失败回调
     *
     * @param responseCode
     * @param requestCode
     * @param requestId
     * @param errMsg
     */
    void onFailure(int responseCode, int requestCode, int requestId, String errMsg);
    
}
