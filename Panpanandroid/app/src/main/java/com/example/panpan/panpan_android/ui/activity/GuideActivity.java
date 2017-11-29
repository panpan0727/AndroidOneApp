package com.example.panpan.panpan_android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.example.panpan.panpan_android.R;
import com.example.panpan.panpan_android.ui.adapter.ImageGuideAdapter;
import com.example.panpan.panpan_android.utils.DeviceUtils;
import com.example.panpan.panpan_android.utils.SharedPrefUtil;
import com.example.panpan.panpan_android.utils.StatusBarUtil;


public class GuideActivity extends BaseActivity {

    private static final String KEY_FIRST_LAUNCH = "first_launch";

    public ViewPager mGuideVP;
    public LinearLayout mDotLlt;
    private View mCurrentDot;

    private ImageGuideAdapter mAdapter;

    private int[] mImages = {R.mipmap.guide_image1, R.mipmap.guide_image2, R.mipmap.guide_image3};
    private String[] mTitles = {"第一页", "第二页", "第三页"};

    private boolean isLastPage = false;
    private boolean isDragPage = false;
    private boolean canJumpPage = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SharedPrefUtil.getState(this,Boolean.class,KEY_FIRST_LAUNCH)){
          startMainActivity();
        }

        SharedPrefUtil.saveState(this,KEY_FIRST_LAUNCH,true);
        setContentView(R.layout.activity_guide);
        StatusBarUtil.setFullScreen(getWindow());
        //初始化界面默认效果
        initView();

    }

    /**
     * 初始化界面默认效果
     */
    public void initView() {

        mGuideVP = (ViewPager) findViewById(R.id.vp_guide);
        mDotLlt = (LinearLayout) findViewById(R.id.llt_dot);
        initDot();
        initImage();
    }

    private void initImage() {
        mAdapter = new ImageGuideAdapter(this, mImages, mTitles, new ImageGuideAdapter.OnNextPageClickListener() {
            @Override
            public void onNext() {
                startMainActivity();
            }
        });
        mGuideVP.setAdapter(mAdapter);
        mGuideVP.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (isLastPage && isDragPage && positionOffsetPixels == 0) {
                    //当前页是最后一页，并且是拖动状态，并且像素偏移量为0
                    if (canJumpPage) {
                        canJumpPage = false;
                        startMainActivity();
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                isLastPage = position == mImages.length - 1;
                if (mCurrentDot != null) {
                    mCurrentDot.setSelected(false);
                }
                mCurrentDot = mDotLlt.getChildAt(position);
                mCurrentDot.setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                isDragPage = state == 1;
            }
        });
    }

    private void initDot() {
        for (int i = 0; i < mImages.length; i++) {
            View view = new View(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DeviceUtils.dp2px(this, 7), DeviceUtils.dp2px(this, 7));
            if (i != mImages.length - 1) {
                layoutParams.rightMargin = DeviceUtils.dp2px(this, 10);
            }
            view.setLayoutParams(layoutParams);
            view.setBackgroundResource(R.drawable.bg_guide_dot);
            mDotLlt.addView(view);
            if (i == 0) {
                view.setSelected(true);
                mCurrentDot = view;
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    /**
     * 开启主页
     */
    private void startMainActivity() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}