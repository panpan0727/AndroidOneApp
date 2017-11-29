package com.example.panpan.panpan_android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.panpan.panpan_android.R;
import com.example.panpan.panpan_android.ui.fragment.BaseFragment;
import com.example.panpan.panpan_android.ui.widget.CommonTitleBar;

/**
 * Created by Administrator on 2017/11/21.
 */

public abstract class BaseSimpleTitleAndFragmentActivity extends BaseActivity implements CommonTitleBar.OnTitleBarClickListener {

    protected CommonTitleBar mTitleBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getInflateLayoutId());
        mTitleBar = (CommonTitleBar) findViewById(R.id.v_title_bar);
        mTitleBar.setTitleText(getCustomTitle());
        mTitleBar.setRightText(getRightTitle());
        mTitleBar.setOnTitleBarClickListener(this);
        BaseFragment fragment = makeFragment();
        setFragmentArguments(fragment);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.getLogTag())
                .commit();
    }

    public int getInflateLayoutId() {
        return R.layout.com_titlebar_with_fragment;
    }

    protected void setFragmentArguments(BaseFragment fragment) {
        fragment.setArguments(getIntent().getExtras());
    }

    protected abstract String getCustomTitle();

    protected abstract BaseFragment makeFragment();

    protected String getRightTitle() {
        return null;
    }

    @Override
    public void onLeftClick(View v) {
        onBackPressed();
    }

    @Override
    public void onRightClick(View v) {

    }


}
