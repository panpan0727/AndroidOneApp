package com.example.panpan.panpan_android.webapi.response;

import java.io.Serializable;

public interface BaseResponse extends Serializable {
    
    Object getResultData();
    String getErrorMsg();
    int getResultCode();
    boolean isSuccess();
    
}
