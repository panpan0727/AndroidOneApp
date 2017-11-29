package com.example.panpan.panpan_android.bdcloudvideoplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.panpan.panpan_android.R;


public class PlayAloneView extends RelativeLayout {
    JCVideoView mVideoView;
    ImageView backIV;
    /**
     * JCVideoView 在原父视图的信息
     */
    ViewGroup oldParent;
    int oldIndex;
    ViewGroup.LayoutParams oldParams;
    
    public PlayAloneView(@NonNull Context context) {
        this(context, null);
    }
    
    public PlayAloneView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }
    
    public PlayAloneView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    public void open(JCVideoView jcVideoView) {
        mVideoView = jcVideoView;
        
        oldParent = (ViewGroup) jcVideoView.getParent();
        oldParams = jcVideoView.getLayoutParams();
        oldIndex = oldParent.indexOfChild(jcVideoView);
        // 占位视图，防止位置变化
        oldParent.addView(new View(getContext()), oldIndex, new ViewGroup.LayoutParams(jcVideoView.getWidth(), jcVideoView.getHeight()));
        // 从原视图移除播放器
        oldParent.removeView(jcVideoView);
        
        // 将视频播放器加到当前视图
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT);
        addView(jcVideoView, 0, params);

        jcVideoView.setPlayAlone(true);
        setVisibility(View.VISIBLE);
    }

    /**
     * 设置标题栏的可见性
     * @param visibility
     */
    public void setTitleViewVisibility(int visibility) {
        if (backIV != null) {
            backIV.setVisibility(visibility);
        }
    }

    void close() {
        // 从当前视图移除播放器
        removeView(mVideoView);
        // 将视频播放器还原到旧的视图
        oldParent.addView(mVideoView, oldIndex, oldParams);
        // 移除占位视图
        oldParent.removeViewAt(oldIndex + 1);

        mVideoView.setPlayAlone(false);
        setVisibility(View.GONE);
    }

    void init() {
        setVisibility(View.GONE);
        setBackgroundColor(Color.BLACK);

        initTitleView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 初始化标题栏
     */
    void initTitleView() {
        backIV = new ImageView(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                getContext().getResources().getDimensionPixelSize(R.dimen.dp48),
                getContext().getResources().getDimensionPixelSize(R.dimen.dp48));
        params.addRule(ALIGN_PARENT_LEFT);
        params.addRule(ALIGN_PARENT_TOP);
        backIV.setScaleType(ImageView.ScaleType.CENTER);
        backIV.setImageResource(R.mipmap.com_video_back_white);
        backIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mVideoView.onBackPressed()) {
                    ((Activity)getContext()).finish();
                }
            }
        });
        addView(backIV, params);
    }
}
