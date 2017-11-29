package com.example.panpan.panpan_android.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.example.panpan.panpan_android.R;
import com.example.panpan.panpan_android.ui.widget.LoadViewHelper;
import com.example.panpan.panpan_android.utils.StatusBarUtil;

/**
 * Created by Administrator on 2017/11/21.
 */

public class BaseActivity extends FragmentActivity {

    protected Context mContext;
    private LoadViewHelper mLoadViewHelper;
    private boolean mIsFirstCreate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
      /*  if (needNetworkRequest()) {
            mRequestListener = new CommonRequestListener(this);
        }*/
        mIsFirstCreate = true;

        //PushAgent.getInstance(this).onAppStart();
       // ARouter.getInstance().inject(this);
    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
    }

    /**
     * 设置状态栏
     */
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.com_FF541B));
    }

    /**
     * 初始化LoadViewHelper
     *
     * @param view
     */
    protected void initLoadViewHelper(View view) {
        mLoadViewHelper = new LoadViewHelper(view);
    }

    /**
     * 加载中页面
     */
    protected void showLoadView() {
        if (mLoadViewHelper != null)
            mLoadViewHelper.showLoading(getString(R.string.com_prompt_loading));
    }

    /**
     * 显示加载失败
     *
     * @param listener
     */
    protected void showLoadError(final View.OnClickListener listener) {
        if (mLoadViewHelper != null) {
            mLoadViewHelper.showError(getString(R.string.loading_error_txt), getString(R.string.reload), R.mipmap.com_ic_loading_error, listener);
        }
    }

    /**
     * 显示网络错误
     *
     * @param listener
     */
    protected void showNetError(final View.OnClickListener listener) {
        if (mLoadViewHelper != null) {
            mLoadViewHelper.showError(getString(R.string.no_network), getString(R.string.com_check_network), R.mipmap.com_ic_no_network, listener);
        }
    }

    /**
     * 显示空数据页面
     */
    protected void showLoadEmpty(final View.OnClickListener listener) {
        if (mLoadViewHelper != null) {
            mLoadViewHelper.showError(getResources().getString(R.string.com_prompt_load_empty)
                    , getResources().getString(R.string.com_retry), 0, listener);
        }
    }

    /**
     * 显示空数据页面
     */
    protected void showLoadEmpty(String emptyMsg, @IdRes int imageId, final View.OnClickListener listener) {
        if (mLoadViewHelper != null) {
            mLoadViewHelper.showError(TextUtils.isEmpty(emptyMsg) ? getResources().getString(R.string.com_prompt_load_empty) : emptyMsg
                    , getResources().getString(R.string.com_retry), imageId, listener);
        }
    }

    /**
     * 显示空数据页面
     */
    protected void showLoadEmpty(String emptyMsg, int imageId) {
        if (mLoadViewHelper != null) {
            mLoadViewHelper.showEmpty(emptyMsg, imageId);
        }
    }

    /**
     * 显示空数据页面(通用)
     */
    protected void showLoadEmpty() {
        showLoadEmpty(getString(R.string.com_nodata), R.mipmap.com_nodata);
    }

    /**
     * 显示特定布局
     */
    protected void showLayout(int layoutId) {
        if (mLoadViewHelper != null) {
            mLoadViewHelper.showLayout(layoutId);
        }
    }

    protected void showLayout(View layout){
        if (mLoadViewHelper != null) {
            mLoadViewHelper.showLayout(layout);
        }
    }

    /**
     * 隐藏加载页面
     */
    protected void hideLoadView() {
        if (mLoadViewHelper != null)
            mLoadViewHelper.restore();
    }

    /**
     * 显示空数据页面
     */

    protected void showLoadingEmpty(String emptyDes, int resId) {
        if (mLoadViewHelper != null)
            mLoadViewHelper.showEmpty(emptyDes, resId);
    }



}
