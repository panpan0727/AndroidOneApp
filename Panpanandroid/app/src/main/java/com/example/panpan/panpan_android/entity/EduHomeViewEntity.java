package com.example.panpan.panpan_android.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/11/22.
 */

public class EduHomeViewEntity extends SimpleResult1 {

    public String nu;
    public String ischeck;
    public String condition;
    public String com;
    public String state;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * time : 2017-11-20 21:50:13
         * ftime : 2017-11-20 21:50:13
         * context : [江苏苏州高新区公司狮山路电信寄存点分部]快件已被 已签收 签收
         * location : 江苏苏州高新区公司狮山路电信寄存点分部
         */

        public String time;
        public String ftime;
        public String context;
        public String location;


    }
}
