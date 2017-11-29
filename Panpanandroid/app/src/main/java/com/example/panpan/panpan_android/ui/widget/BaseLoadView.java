package com.example.panpan.panpan_android.ui.widget;

import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2017/11/21.
 */

public interface BaseLoadView {

    // 获取当前布局
    public abstract View getCurrentLayout();

    // 重置布局
    public abstract void resetView();

    // 显示布局
    public abstract void showLayout(View view);

    public abstract void showLayout(int layoutId);

    // 填充布局
    public abstract View inflate(int layoutId);

    public abstract Context getContext();

    public abstract View getView();




}
