package com.example.panpan.panpan_android.ui.adapter;

import java.util.List;


public interface RecyclerAdapter<T> {

    void appendData(List<T> data);

    void clearData();

    List<T> getData();

}
