package com.example.panpan.panpan_android.webapi;

import android.content.Context;

import com.example.panpan.panpan_android.entity.EduHomeViewEntity;
import com.example.panpan.panpan_android.net.ApiRequesterFactory;
import com.example.panpan.panpan_android.webapi.callback.OnRequestCallback;
import com.example.panpan.panpan_android.webapi.response.BaseResponseEase;

/**
 * Created by Administrator on 2017/11/22.
 */

public class PanpanWebApi extends BaseApi {
    private static final String SERVER_URL = "http://106.15.228.150:8186";

    private static PanpanWebApi mCpnWebApi;

    public static  PanpanWebApi getInstance(){

        synchronized (PanpanWebApi.class) {
            if (mCpnWebApi == null) {
                mCpnWebApi = new PanpanWebApi();
            }
        }
        return mCpnWebApi;

    }

    /**
     * 学院首页
     *
     * @return
     */
    public void getListData(Context mContext, OnRequestCallback onRequestCallback) {
       // EduReqParam param = new EduReqParam(mContext);
        ApiRequesterFactory.getInstance().post(
                generateRequestId()
                , 1
                , "http://www.kuaidi100.com/query?type=yunda&postid=3831238608767"
                , EduHomeViewEntity.class
                , null
                , onRequestCallback
                ,2000
        );
    }



}
