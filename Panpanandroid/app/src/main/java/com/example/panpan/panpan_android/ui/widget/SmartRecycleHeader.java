package com.example.panpan.panpan_android.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.scwang.smartrefresh.layout.internal.pathview.PathsView;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;



public class SmartRecycleHeader extends RelativeLayout implements RefreshHeader {
    public static String REFRESH_HEADER_PULLDOWN = "下拉可以刷新";
    public static String REFRESH_HEADER_REFRESHING = "正在刷新";
    public static String REFRESH_HEADER_RELEASE = "释放立即刷新";

    private Date mLastTime;
    private TextView mHeaderText;
    private TextView mLastUpdateText;
    private PathsView mArrowView;
    private ImageView mProgressView;
    private ProgressDrawable mProgressDrawable;
    private DateFormat mFormat = new SimpleDateFormat("上次更新 M-d HH:mm", Locale.CHINA);
    private SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;

    //<editor-fold desc="RelativeLayout">
    public SmartRecycleHeader(Context context) {
        super(context);
        this.initView(context, null, 0);
    }

    public SmartRecycleHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs, 0);
    }

    public SmartRecycleHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        DensityUtil density = new DensityUtil();

        setMinimumHeight(density.dip2px(80));

        mProgressDrawable = new ProgressDrawable();
        mProgressDrawable.setColor(0xff333333);
        mProgressView = new ImageView(context);
        mProgressView.setImageDrawable(mProgressDrawable);
        LayoutParams lpProgress = new LayoutParams(density.dip2px(20), density.dip2px(20));
        lpProgress.leftMargin = density.dip2px(80);
        lpProgress.addRule(CENTER_VERTICAL);
        lpProgress.addRule(ALIGN_PARENT_LEFT);
        addView(mProgressView, lpProgress);

        mArrowView = new PathsView(context);
        mArrowView.parserColors(0xff666666);
        mArrowView.parserPaths("M20,12l-1.41,-1.41L13,16.17V4h-2v12.17l-5.58,-5.59L4,12l8,8 8,-8z");
        addView(mArrowView, lpProgress);

        LinearLayout layout = new LinearLayout(context, attrs, defStyleAttr);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.VERTICAL);
        mHeaderText = new TextView(context);
        mHeaderText.setText(REFRESH_HEADER_PULLDOWN);
        mHeaderText.setTextColor(0xff666666);
        mHeaderText.setTextSize(16);

        mLastUpdateText = new TextView(context);
        mLastUpdateText.setText(mFormat.format(new Date()));
        mLastUpdateText.setTextColor(0xff7c7c7c);
        mLastUpdateText.setTextSize(12);

        mLastUpdateText.setVisibility(View.GONE);

        LinearLayout.LayoutParams lpHeaderText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpHeaderText.leftMargin = density.dip2px(20);
        lpHeaderText.rightMargin = density.dip2px(20);
        layout.addView(mHeaderText, lpHeaderText);
        LinearLayout.LayoutParams lpUpdateText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layout.addView(mLastUpdateText, lpUpdateText);

        LayoutParams lpHeaderLayout = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpHeaderLayout.addRule(CENTER_IN_PARENT);
        addView(layout,lpHeaderLayout);

        if (isInEditMode()) {
            mArrowView.setVisibility(GONE);
            mHeaderText.setText(REFRESH_HEADER_REFRESHING);
        } else {
            mProgressView.setVisibility(GONE);
        }


        TypedArray ta = context.obtainStyledAttributes(attrs, com.scwang.smartrefresh.layout.R.styleable.ClassicsHeader);

        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(com.scwang.smartrefresh.layout.R.styleable.ClassicsHeader_srlClassicsSpinnerStyle,mSpinnerStyle.ordinal())];

        int primaryColor = ta.getColor(com.scwang.smartrefresh.layout.R.styleable.ClassicsHeader_srlPrimaryColor, 0);
        int accentColor = ta.getColor(com.scwang.smartrefresh.layout.R.styleable.ClassicsHeader_srlAccentColor, 0);
        if (primaryColor != 0) {
            if (accentColor != 0) {
                setPrimaryColors(primaryColor, accentColor);
            } else {
                setPrimaryColors(primaryColor);
            }
        } else if (accentColor != 0) {
            setPrimaryColors(0, accentColor);
        }

        ta.recycle();
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
    }
    
    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
        
    }
    
    @Override
    public void onPullingDown(float percent, int offset, int headHeight, int extendHeight) {
    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {

    }

    @Override
    public void onRefreshReleased(RefreshLayout layout, int headerHeight, int extendHeight) {

    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        mProgressDrawable.start();
    }
    
    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        mProgressDrawable.stop();
        return 0;
    }

    @Override
    public void setPrimaryColors(int... colors) {
        if (colors.length > 1) {
            setBackgroundColor(colors[0]);

            mArrowView.parserColors(0x7f010003);
            mHeaderText.setTextColor(0x7f010004);
            mProgressDrawable.setColor(0x7f010004);

            mLastUpdateText.setTextColor(colors[1]&0x00ffffff|0x99000000);
        } else if (colors.length > 0) {
            setBackgroundColor(colors[0]);
            if (colors[0] == 0xffffffff) {
                mArrowView.parserColors(0xff666666);
                mHeaderText.setTextColor(0xff666666);
                mProgressDrawable.setColor(0xff666666);
                mLastUpdateText.setTextColor(0xff666666&0x00ffffff|0x99000000);
            } else {
                mArrowView.parserColors(0xffffffff);
                mHeaderText.setTextColor(0xffffffff);
                mProgressDrawable.setColor(0xffffffff);
                mLastUpdateText.setTextColor(0xaaffffff);
            }
        }
    }

    @NonNull
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return mSpinnerStyle;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
                restoreRefreshLayoutBackground();
            case PullDownToRefresh:
                mHeaderText.setText(REFRESH_HEADER_PULLDOWN);
                mArrowView.setVisibility(VISIBLE);
                mProgressView.setVisibility(GONE);
                mArrowView.animate().rotation(0);
                break;
            case Refreshing:
                mHeaderText.setText(REFRESH_HEADER_REFRESHING);
                mProgressView.setVisibility(VISIBLE);
                mArrowView.setVisibility(GONE);
                break;
            case ReleaseToRefresh:
                mHeaderText.setText(REFRESH_HEADER_RELEASE);
                mArrowView.animate().rotation(180);
                replaceRefreshLayoutBackground(refreshLayout);
                break;
        }
    }
    //</editor-fold>

    //<editor-fold desc="background">
    private Runnable restoreRunable;
    private void restoreRefreshLayoutBackground() {
        if (restoreRunable != null) {
            restoreRunable.run();
            restoreRunable = null;
        }
    }

    private void replaceRefreshLayoutBackground(final RefreshLayout refreshLayout) {
        if (restoreRunable == null && mSpinnerStyle == SpinnerStyle.FixedBehind) {
            restoreRunable = new Runnable() {
                Drawable drawable = refreshLayout.getLayout().getBackground();
                @Override
                public void run() {
                    refreshLayout.getLayout().setBackgroundDrawable(drawable);
                }
            };
            refreshLayout.getLayout().setBackgroundDrawable(getBackground());
        }
    }
    //</editor-fold>

    //<editor-fold desc="API">
    public SmartRecycleHeader setLastUpdateTime(Date time) {
        mLastTime = time;
        mLastUpdateText.setText(mFormat.format(time));
        return this;
    }

    public SmartRecycleHeader setTimeFormat(DateFormat format) {
        mFormat = format;
        mLastUpdateText.setText(mFormat.format(mLastTime));
        return this;
    }

    public SmartRecycleHeader setSpinnerStyle(SpinnerStyle style) {
        this.mSpinnerStyle = style;
        return this;
    }
    //</editor-fold>
    
    
    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }
}
