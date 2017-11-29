package com.example.panpan.panpan_android.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.example.panpan.panpan_android.R;
import com.example.panpan.panpan_android.ui.dialog.CustomProgressDialog;
import com.example.panpan.panpan_android.ui.widget.LoadViewHelper;
import com.example.panpan.panpan_android.webapi.callback.OnRequestCallback;


public class BaseFragment extends Fragment implements OnRequestCallback {

    private LoadViewHelper mLoadViewHelper;
    private String mCurrUserType;
    private boolean mIsFirstCreate;
    public View mView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsFirstCreate = true;

    }
   
    @Override
    public void onResume() {
        super.onResume();
        if (mIsFirstCreate) {
            mIsFirstCreate = false;
        }
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
    protected void showLoadingView() {
        if (mLoadViewHelper != null && isAdded())
            mLoadViewHelper.showLoading(getString(R.string.com_prompt_loading));
    }
    
    /**
     * 显示加载失败
     *
     * @param listener
     */
    protected void showLoadError(final View.OnClickListener listener) {
        if (mLoadViewHelper != null && isAdded()) {
            mLoadViewHelper.showError(getString(R.string.loading_error_txt), getString(R.string.reload), R.mipmap.com_ic_loading_error, listener);
        }
    }
    
    /**
     * 显示网络错误
     */
    protected void showNetError() {
        if (mLoadViewHelper != null && isAdded()) {
            mLoadViewHelper.showError(getString(R.string.no_network), getString(R.string.com_check_network), R.mipmap.com_ic_no_network, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent("android.settings.WIFI_SETTINGS"));
                }
            });
        }
    }
    
    /**
     * 显示空数据页面
     */
    protected void showLoadEmpty(final View.OnClickListener listener) {
        if (mLoadViewHelper != null && isAdded()) {
            mLoadViewHelper.showError(getResources().getString(R.string.com_prompt_load_empty)
                    , getResources().getString(R.string.com_retry), 0, listener);
        }
    }
    
    /**
     * 显示空数据页面
     */
    protected void showLoadEmpty(String emptyMsg, @IdRes int imageId, final View.OnClickListener listener) {
        if (mLoadViewHelper != null && isAdded()) {
            mLoadViewHelper.showError(TextUtils.isEmpty(emptyMsg) ? getResources().getString(R.string.com_prompt_load_empty) : emptyMsg
                    , getResources().getString(R.string.com_retry), imageId, listener);
        }
    }
    
    /**
     * 显示空数据页面
     */
    protected void showLoadEmpty(String emptyMsg, int imageId) {
        if (mLoadViewHelper != null && isAdded()) {
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
     * 隐藏加载页面
     */
    protected void hideLoadView() {
        if (mLoadViewHelper != null)
            mLoadViewHelper.restore();
    }
    
    public boolean needNetworkRequest() {
        return true;
    }
    

    
    // 显示加载框
    public void showLoadingDialog(String msg, boolean cancelable) {
        hideLoadingDialog();
        CustomProgressDialog.show(getContext(), cancelable, msg);
    }
    
    // 隐藏加载框
    public void hideLoadingDialog() {
        CustomProgressDialog.close();
    }
    
    public String getLogTag() {
        return getClass().getSimpleName();
    }


    @Override
    public void onCacheArrived(int requestCode, int requestId, Object response) {

    }

    @Override
    public void onSuccess(int requestCode,int requestId, Object response) {

    }

    @Override
    public void onFailure(int responseCode, int requestCode, int requestId, String errMsg) {

    }
}
