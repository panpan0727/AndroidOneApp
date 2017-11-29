package com.example.panpan.panpan_android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.panpan.panpan_android.R;

import java.lang.ref.WeakReference;


public class CustomProgressDialog extends Dialog {
    private static WeakReference<Context> mContext;
    
    private TextView mMessage;
    private ImageView mImage;
    private RelativeLayout mRootLayout;
    
    private static CustomProgressDialog mToast;
    
    public static void show(Context context, boolean canCancel, String text) {
        if (context == null) return;
        if (mToast != null && mContext.get() != null && mContext.get().equals(context)) {
            mToast.setCancelable(canCancel);
            mToast.setCanceledOnTouchOutside(canCancel);
            mToast.show();
        } else {
            mContext = new WeakReference<>(context);
            mToast = new CustomProgressDialog(mContext.get());
            mToast.setCancelable(canCancel);
            mToast.setCanceledOnTouchOutside(canCancel);
            mToast.show();
        }
        mToast.mMessage.setText(text);
    }
    
    public CustomProgressDialog(Context context) {
        super(context, R.style.LoadingDialog);
        init();
    }
    
    public static void close() {
        if (mToast != null) {
            mToast.dismiss();
            mToast = null;
        }else{
            mToast = null;
        }
    }
    
    private void init() {
        setContentView(R.layout.dialog_loading);
        mMessage = (TextView) findViewById(R.id.textView1);
    }
    
    
}
