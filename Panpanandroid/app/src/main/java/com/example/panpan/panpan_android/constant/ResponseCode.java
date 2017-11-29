package com.example.panpan.panpan_android.constant;

/**
 * Api请求回复代码
 */
public class ResponseCode {
    
    /**
     * 请求成功
     */
    public static final int SUCCESS = 0;
    
    /**
     * 鉴权失败
     */
    public static final int ERROR_FAIL = 60000;
    
    /**
     * 鉴权TOKEN不存在
     */
    public static final int ERROR_TOKEN_NOT_EXIST = 60001;
    
    /**
     * 刷新token失败
     */
    public static final int ERROR_REFRESH_FAIL = 60002;
    
    /**
     * refresh token不合法
     */
    public static final int ERROR_REFRESH_TOKEN_ILLEGAL = 60003;
    
    /**
     * 下架信息1
     */
    public static final int SOLD_OUT1 = 30003;
    
    /**
     * 下架信息2
     */
    public static final int SOLD_OUT2 = 30041;
    
    /**
     * 下架信息3 视频状态错误
     */
    public static final int SOLD_OUT3 = 30011;
    
    /**
     * 下架信息4 剧集状态错误
     */
    public static final int SOLD_OUT4 = 30012;

    /**
     * 金诚人: 个人信息
     */
    public static final int CODE_GET_USER_INFO = 1002;

    /**
     * 金诚人: 验证OA密码
     */
    public static final int CODE_VERIFY_PWD = 1018;

    /**
     * 金诚人: 公告列表
     */
    public static final int CODE_GET_ANNOUNCEMENT = 1006;
}
