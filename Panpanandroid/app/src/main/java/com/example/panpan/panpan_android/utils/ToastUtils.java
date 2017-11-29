package com.example.panpan.panpan_android.utils;

import android.content.Context;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.panpan.panpan_android.R;


/**
 * 全局统一的toast
 */
public class ToastUtils {
    private static Toast mToast;
    private static TextView tvMessage;
    private static ImageView ivIcon;
    
    /**
     * 使用之前必须先在application中初始化
     *
     * @param context
     */
    public static void init(Context context) {
        mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.com_layout_toast, null);
        mToast.setView(v);
        tvMessage = (TextView) v.findViewById(R.id.tv_message);
        ivIcon = (ImageView) v.findViewById(R.id.iv_icon);
        tvMessage.setText("");
    }
    
    /**
     * 弹出toast 默认居中
     *
     * @param msg
     */
    public static void show(String msg) {
        if (mToast == null || TextUtils.isEmpty(msg)) {
            return;
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        tvMessage.setText(msg);
        ivIcon.setImageResource(0);
        ivIcon.setVisibility(View.GONE);
        mToast.show();
    }
    
    /**
     * 弹出toast 默认居中
     *
     * @param resId
     */
    public static void show(int resId) {
        if (mToast == null) {
            return;
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        tvMessage.setText(resId);
        ivIcon.setImageResource(0);
        ivIcon.setVisibility(View.GONE);
        mToast.show();
    }
    
    /**
     * 在底部显示toast
     *
     * @param context
     * @param msg
     */
    public static void showBottom(Context context, String msg) {
        if (mToast == null) {
            return;
        }
        mToast.setGravity(Gravity.CENTER, 0, SizeUtils.getRealHeight(context, 400));
        tvMessage.setText(msg);
        ivIcon.setImageResource(0);
        ivIcon.setVisibility(View.GONE);
        mToast.show();
    }
    
    /**
     * 显示带图标的toast
     *
     * @param msg
     * @param iconId
     */
    public static void show(String msg, @IdRes int iconId) {
        if (mToast == null) {
            return;
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        tvMessage.setText(msg);
        ivIcon.setImageResource(iconId);
        ivIcon.setVisibility(View.VISIBLE);
        mToast.show();
    }
    
    /**
     * toast取消
     */
    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }
}
