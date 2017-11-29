package com.example.panpan.panpan_android.ui.activity;

import android.util.Log;
import android.view.View;
import com.example.panpan.panpan_android.ui.fragment.BaseFragment;
import com.example.panpan.panpan_android.ui.fragment.GoodDetailFragment;

/**
 * Created by Administrator on 2017/11/21.
 */

public class GoodDetailActivity extends BaseSimpleTitleAndFragmentActivity {
    @Override
    protected String getCustomTitle() {
        return "商品详情页";
    }

    protected String getRightTitle() {
        return "右侧按钮";
    }
    @Override
    protected BaseFragment makeFragment() {
        return new GoodDetailFragment();
    }

    @Override
    public void onRightClick(View v) {

        Log.i("sssssss","onRightClick");
    }

}
