package com.example.panpan.panpan_android.ui.widget;

import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.panpan.panpan_android.R;



public class LoadViewHelper {

    private BaseLoadView helper;


    AnimationDrawable loadingAnimation;

    // 构造函数
    public LoadViewHelper(View view) {

        this(new ViewGroupLayout(view));
    }

    // 构造函数
    public LoadViewHelper(BaseLoadView helper) {
        super();
        this.helper = helper;
    }

    // 加载错误
    public void showError(String errorText, String buttonText, @IdRes int resId,
                          View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.com_view_load_error);
        TextView textView = (TextView) layout.findViewById(R.id.textView1);
        ImageView imageView = (ImageView) layout.findViewById(R.id.iv_img);
        textView.setText(errorText);
        imageView.setImageResource(resId);
        TextView button = (TextView) layout.findViewById(R.id.button1);
        button.setText(buttonText);
        button.setOnClickListener(onClickListener);
        helper.showLayout(layout);
    }

    // 数据为空
    public void showEmpty(String emptyDes, int resId) {
        View layout = helper.inflate(R.layout.com_view_load_empty);
        Button button = (Button) layout.findViewById(R.id.button1);
        button.setText(emptyDes);
        if (resId > 0) {
            ImageView img = (ImageView) layout.findViewById(R.id.imageView);
            img.setImageResource(resId);
        }
        helper.showLayout(layout);
    }

    // 加载指定布局
    public void showLayout(@IdRes int layoutId) {
        View layout = helper.inflate(layoutId);
        helper.showLayout(layout);
    }

    // 加载指定布局
    public void showLayout(View layout) {
        helper.showLayout(layout);
    }

    // 正在加载中
    public void showLoading(String loadText) {
        View layout = helper.inflate(R.layout.com_view_load_loading);
        TextView textView = (TextView) layout.findViewById(R.id.textView1);
        textView.setText(loadText);
        helper.showLayout(layout);
    }

    public void restore() {
        helper.resetView();
        if (loadingAnimation != null) {
            loadingAnimation.stop();
            loadingAnimation = null;
        }
    }
}
