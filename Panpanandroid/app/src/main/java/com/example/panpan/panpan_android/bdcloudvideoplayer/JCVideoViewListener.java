package com.example.panpan.panpan_android.bdcloudvideoplayer;
/**
 * time   : 2017/10/18
 * desc   : 对外事件接口
 * version: 1.0
 */
public interface JCVideoViewListener {
    /**
     * 全屏和非全屏状态切换
     *
     * @param isFullScreen true：全屏
     */
    void onScreenChanged(boolean isFullScreen);
}
