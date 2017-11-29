package com.example.panpan.panpan_android.bdcloudvideoplayer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.cloud.media.player.IMediaPlayer;
import com.example.panpan.panpan_android.R;
import com.example.panpan.panpan_android.utils.NetUtils;


import java.util.Timer;
import java.util.TimerTask;

/**
 * time   : 2017/10/17
 * desc   : 视频组件，注意：需要设置 android:configChanges="orientation|keyboardHidden|screenSize"，否则横屏无效果
 * version: 1.0
 */
public class JCVideoView extends FrameLayout implements IMediaPlayer.OnPreparedListener
        , IMediaPlayer.OnCompletionListener
        , IMediaPlayer.OnErrorListener
        , IMediaPlayer.OnInfoListener
        , IMediaPlayer.OnBufferingUpdateListener
        , BDCloudVideoView.OnPlayerStateListener {

    PlayerController mController;
    RelativeLayout mSufferViewLayout;
    TextView mVideoPlayAgainBtn;
    TextView mVideoPlayNoticeText;
    RelativeLayout mPlayAgainLayout;
    LinearLayout mProgressLayout;
    ImageView mProgressImg;
    TextView mTimeNowText;
    TextView mTimeTotalText;
    GestureDetector gestureDetector;
    
    String mVideoUrl;
    BDCloudVideoView mPlayer;
    LifeCircleFragment mLifeCircleFragment;
    
    public static final String AK = "c9901ce913f447dfbf8c32a1c70efb3b";
    boolean firstScroll = false;
    /**
     * 非Wifi是否播放，true：播放
     */
    boolean isWithoutWifiCouldPlay = false;
    /**
     * 是否自动播放，true：自动播放，但如果不是 wifi 环境，还取决于 isWithoutWifiCouldPlay 的值
     */
    boolean mAutoPlay = true;
    int STEP_PROGRESS = 1;
    int playingTime;
    int videoTotalTime;
    Timer barTimer;
    /**
     * 视频宽高比，只对竖屏方向有效
     */
    float mHeightRatio = 5f / 8f;
    
    /**
     * 占位视图
     */
    View mCoverView;
    JCVideoViewListener mListener;
    /**
     * 当前是否在独立播放模式
     */
    boolean playAlone;
    PlayAloneView mAloneView;
    /**
     * 是否使用自带的标题栏
     */
    boolean mUseInnerTitle = true;
    
    /**
     * 竖屏
     */
    public static final int ORIENTATION_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    /**
     * 横屏
     */
    public static final int ORIENTATION_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    /**
     * 当前屏幕方向，ORIENTATION_PORTRAIT or ORIENTATION_LANDSCAPE
     */
    int mCurrentOrientation = ORIENTATION_PORTRAIT;
    
    public JCVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }
    
    public JCVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    /**
     * 设置屏幕方向
     *
     * @param activity
     * @param screenOrientation
     */
    public void setRequestedOrientation(Activity activity, int screenOrientation) {
        if (screenOrientation == ORIENTATION_PORTRAIT) {
            // 竖屏
            mCurrentOrientation = ORIENTATION_PORTRAIT;
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            FullScreenUtils.toggleHideyBar(activity);
            mController.setScreenBtnImg(R.mipmap.com_video_screen_full);
        } else if (screenOrientation == ORIENTATION_LANDSCAPE) {
            // 横屏
            mCurrentOrientation = ORIENTATION_LANDSCAPE;
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            setLayoutParams(layoutParams);
            
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            FullScreenUtils.toggleHideyBar(activity);
            mController.setScreenBtnImg(R.mipmap.com_video_screen_nromal);
        }
        requestLayout();
    }
    
    /**
     * 设置视频 url
     *
     * @param videoUrl
     * @param autoPlay 是否自动播放
     * @return
     */
    public JCVideoView withVideoUrl(@NonNull String videoUrl, boolean autoPlay) {
        mVideoUrl = videoUrl;
        mAutoPlay = autoPlay;
        if (mPlayer != null) {
            mPlayer.setVideoPath(videoUrl);
        }
        mLifeCircleFragment.bindLifeCircle(this, videoUrl);
        return this;
    }
    
    public JCVideoView withVideoUrl(@NonNull String videoUrl) {
        return withVideoUrl(videoUrl, true);
    }
    
    public JCVideoView bindListener(JCVideoViewListener videoViewListener) {
        mListener = videoViewListener;
        return this;
    }
    
    /**
     * 处理物理返回按钮
     *
     * @return true：Activity 不需要处理，false：交给 Activity 处理
     */
    public boolean onBackPressed() {
        if (playAlone) {
            // 当前是独立播放模式
            if (mCurrentOrientation == ORIENTATION_LANDSCAPE) {
                // 横屏 -> 竖屏
                mCurrentOrientation = ORIENTATION_PORTRAIT;
                setRequestedOrientation((Activity) getContext(), mCurrentOrientation);
            } else {
                if (mUseInnerTitle) {
                    // 竖屏 -> 关闭独立播放模式
                    mAloneView.close();
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    /**
     * 设置覆盖视图，常用于设置视频封面。或者在 xml 中设置：xml 中 JCVideoView 只支持包含一个直接子 View
     * @param coverView
     * @param playWhenClicked 点击coverView后，是否播放视频，true：自动播放
     * @return
     */
    public JCVideoView setCoverView(@NonNull View coverView, boolean playWhenClicked) {
        mCoverView = coverView;
        
        if (coverView.getParent() == null) {
            ViewGroup.LayoutParams holderViewParams = coverView.getLayoutParams();
            if (holderViewParams == null) {
                holderViewParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            } else {
                holderViewParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                holderViewParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            addView(coverView, holderViewParams);
        }
        
        if (playWhenClicked) {
            // 点击后播放
            coverView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    start();
                }
            });
        }
        
        return this;
    }
    
    /**
     * 播放
     */
    public void start() {
        if (mPlayer != null) {
            // 隐藏占位视图
            if (mCoverView != null && mCoverView.getVisibility() == View.VISIBLE) {
                mCoverView.setVisibility(View.GONE);
            }
            
            startPlay();
        }
    }
    
    /**
     * 暂停播放
     */
    public void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }
    
    /**
     * 设置宽高比，只有竖屏模式有效果
     *
     * @param heightRatio
     */
    public void setHeightRatio(float heightRatio) {
        mHeightRatio = heightRatio;
        requestLayout();
    }
    
    /**
     * 只用使用内部标题，才会有独立播放模式；否则，是一个假象
     * @param useInnerTitle true：使用内部标题 false：使用自定义标题
     * @return
     */
    public JCVideoView useInnerTitle(boolean useInnerTitle) {
        mUseInnerTitle = useInnerTitle;
        if (useInnerTitle) {
            playAlone = false;
        } else {
            playAlone = true;
        }
        return this;
    }
    
    JCVideoView setPlayAlone(boolean playAlone) {
        this.playAlone = playAlone;
        return this;
    }
    
    boolean isAutoPlay() {
        return mAutoPlay;
    }
    
    void enterForeground() {
        if (mPlayer != null) {
            mPlayer.enterForeground();
        }
    }
    
    /**
     * enterBackground should be invoke before super.onStop()
     */
    void enterBackground() {
        if (mPlayer != null) {
            mPlayer.enterBackground();
        }
    }
    
    /**
     * 根据网络情况和用户的播放设置，播放视频
     */
    void startPlay() {
        if (!NetUtils.isNetAvailable(getContext())) {
            mVideoPlayNoticeText.setText(getContext().getString(R.string.com_video_loading_fault_without_wifi));
            mVideoPlayAgainBtn.setText(getContext().getString(R.string.com_video_refresh));
            mPlayAgainLayout.setVisibility(View.VISIBLE);
            mController.stopPlayer();
        } else {
            if (!NetUtils.isWifi(getContext()) && !isWithoutWifiCouldPlay) {
                mPlayAgainLayout.setVisibility(View.VISIBLE);
                mVideoPlayNoticeText.setText(getContext().getString(R.string.com_video_play_without_wifi_notice));
                mVideoPlayAgainBtn.setText(getContext().getString(R.string.com_video_play_continue));
                mController.stopPlayer();
            } else {
                mPlayAgainLayout.setVisibility(View.GONE);
                mController.startPlayer();
            }
        }
    }
    
    /**
     * 释放实例
     */
    void release() {
        unregisterReceiver();
        if (mPlayer != null) {
            mPlayer.stopPlayback(); // 释放播放器资源
            mPlayer.release(); // 释放播放器资源和显示资源
        }
        if (mController != null) {
            mController.release();
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mCurrentOrientation == ORIENTATION_PORTRAIT) {
            // 竖屏
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (width * mHeightRatio);
            setMeasuredDimension(width, height);
            measureChildren(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
    }
    
    void init() {
        initUI();
        initPlayer();
        initPlayAloneView();
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getChildCount() == 2) {
            mCoverView = getChildAt(1);
            // 默认点击会开始播放
            mCoverView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    start();
                }
            });
        }
    }
    
    void initUI() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.com_video_layout_video_view, null);
        addView(v, 0);
        
        FragmentTransaction transaction = ((Activity) getContext()).getFragmentManager().beginTransaction();
        mLifeCircleFragment = new LifeCircleFragment();
        transaction.add(R.id.lifeCircleFragment, mLifeCircleFragment);
        transaction.commit();
        
        mController = (PlayerController) findViewById(R.id.media_controller_bar);
        mSufferViewLayout = (RelativeLayout) findViewById(R.id.rl_suffer_view);
        mVideoPlayAgainBtn = (TextView) findViewById(R.id.tv_video_play_again);
        mVideoPlayNoticeText = (TextView) findViewById(R.id.tv_play_notice);
        mPlayAgainLayout = (RelativeLayout) findViewById(R.id.rl_play_again);
        mProgressLayout = (LinearLayout) findViewById(R.id.progress_layout);
        mProgressImg = (ImageView) findViewById(R.id.iv_fast_forward);
        mTimeNowText = (TextView) findViewById(R.id.tv_time_now);
        mTimeTotalText = (TextView) findViewById(R.id.tv_time_total);
        
        findViewById(R.id.rl_suffer_view).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEmptyArea();
            }
        });
        findViewById(R.id.tv_video_play_again).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayAgainClick();
            }
        });
        
        initGestureView();
    }
    
    void initPlayer() {
        BDCloudVideoView.setAK(AK);
        mPlayer = new BDCloudVideoView(getContext());
        
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnInfoListener(this);
        mPlayer.setOnBufferingUpdateListener(this);
        mPlayer.setOnPlayerStateListener(this);
        mPlayer.setVideoScalingMode(BDCloudVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        
        RelativeLayout viewHolder = (RelativeLayout) findViewById(R.id.view_holder);
        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(-1, -1);
        rllp.addRule(RelativeLayout.CENTER_IN_PARENT);
        viewHolder.addView(mPlayer, rllp);
        
        mController.setMediaPlayerControl(mPlayer);
        
        mPlayer.setLogEnabled(false);
        mPlayer.setBufferTimeInMs(5000);
        mPlayer.setInitPlayPosition(0);
        mPlayer.setMaxProbeTime(3000);
        
        mController.setListener(new PlayerController.OnMyListener() {
            @Override
            public void onStop() {
            }
            
            @Override
            public void onPlay() {
                mPlayAgainLayout.setVisibility(View.GONE);
            }
            
            @Override
            public void onScreenChanged() {
                if (playAlone) {
                    // 当前是独立播放模式
                    if (mCurrentOrientation == ORIENTATION_LANDSCAPE) {
                        // 横屏 -> 竖屏
                        mCurrentOrientation = ORIENTATION_PORTRAIT;
                    } else {
                        // 竖屏 -> 横屏
                        mCurrentOrientation = ORIENTATION_LANDSCAPE;
                    }
                    setRequestedOrientation((Activity) getContext(), mCurrentOrientation);
                } else {
                    mAloneView.open(JCVideoView.this);
                }
                hideOuterAfterFiveSeconds();
            }
            
            @Override
            public void onRePlayVideo() {
                mPlayAgainLayout.setVisibility(View.GONE);
                onRePlay(0);
            }
            
            @Override
            public void onSeekTouch() {
                if (barTimer != null) {
                    barTimer.cancel();
                    barTimer = null;
                }
            }
            
            @Override
            public void onSeekTouchStop() {
                hideOuterAfterFiveSeconds();
            }
        });
    }
    
    void initGestureView() {
        mProgressLayout.setVisibility(View.GONE);
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                firstScroll = true;// 设定是触摸屏幕后第一次scroll的标志
                playingTime = mPlayer.getCurrentPosition();
                videoTotalTime = mPlayer.getDuration();
                
                return false;
            }
            
            @Override
            public void onShowPress(MotionEvent e) {
                
            }
            
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }
            
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float mOldX = e1.getX(), mOldY = e1.getY();
                int y = (int) e2.getRawY();
                
                if (firstScroll) {// 以触摸屏幕后第一次滑动为标准，避免在屏幕上操作切换混乱
                    // 横向的距离变化大则调整进度，纵向的变化大则调整音量
                    if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                        mProgressLayout.setVisibility(View.VISIBLE);
                        mController.setIsDragging(true);
                    }
                }
                
                // 如果每次触摸屏幕后第一次scroll是调节进度，那之后的scroll事件都处理音量进度，直到离开屏幕执行下一次操作
                // distanceX=lastScrollPositionX-currentScrollPositionX，因此为正时是快进
                if (Math.abs(distanceX) > Math.abs(distanceY)) {// 横向移动大于纵向移动
                    if (distanceX >= STEP_PROGRESS) {// 快退，用步长控制改变速度，可微调
                        mProgressImg.setImageResource(R.mipmap.com_video_ic_fast_back);
                        if (playingTime > 1000) {// 避免为负
                            playingTime -= 1000;// scroll方法执行一次快退3秒
                        } else {
                            playingTime = 0;
                        }
                    } else if (distanceX <= -STEP_PROGRESS) {// 快进
                        mProgressImg.setImageResource(R.mipmap.com_video_ic_fast_forward);
                        if (playingTime < videoTotalTime) {// 避免超过总时长
                            playingTime += 1000;// scroll执行一次快进3秒
                        } else {
                            playingTime = videoTotalTime;
                        }
                    }
                    if (playingTime < 0) {
                        playingTime = 0;
                    }
                    
                    mController.setProgress(playingTime);
                    mTimeNowText.setText(mController.formatMilliSecond2(playingTime / 1000));
                    mTimeTotalText.setText("/" + mController.formatMilliSecond2(videoTotalTime / 1000));
                }
                
                
                firstScroll = false;// 第一次scroll执行完成，修改标志
                return false;
            }
            
            @Override
            public void onLongPress(MotionEvent e) {
                
            }
            
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        mSufferViewLayout.setLongClickable(true);
        gestureDetector.setIsLongpressEnabled(true);
        mSufferViewLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mProgressLayout.setVisibility(View.GONE);
                    if (mController.getIsDragging()) {        //进度条位拖动时，不需要该操作
                        mController.setIsDragging(false);
                        mController.seekToTime(playingTime);
                    }
                }
                return gestureDetector.onTouchEvent(event);//如果想要监听到双击、滑动、长按等复杂的手势操作，这个时候就必须得用到OnGestureListener了
            }
        });
        ViewTreeObserver viewObserver = mSufferViewLayout.getViewTreeObserver();
        viewObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSufferViewLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }
    
    void initPlayAloneView() {
        mAloneView = new PlayAloneView(getContext());
        ((Activity) getContext()).getWindow().addContentView(mAloneView,
                new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mAloneView.setVisibility(View.GONE);
    }
    
    /**
     * 5 秒后进入全屏沉浸模式
     */
    void hideOuterAfterFiveSeconds() {
        if (barTimer != null) {
            barTimer.cancel();
            barTimer = null;
        }
        barTimer = new Timer();
        barTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mController.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mController.hide();
                        if (mListener != null) {
                            mListener.onScreenChanged(true);
                        }
                        mAloneView.setTitleViewVisibility(View.GONE);
                    }
                });
                
            }
        }, 5 * 1000);
    }
    
    /**
     * 点击空白区域，显示或隐藏其他组件
     */
    void onClickEmptyArea() {
        if (barTimer != null) {
            barTimer.cancel();
            barTimer = null;
        }
        if (mController.getVisibility() == View.VISIBLE) {
            mController.hide();
            if (mListener != null) {
                mListener.onScreenChanged(true);
            }
            mAloneView.setTitleViewVisibility(View.GONE);
        } else {
            mController.show();
            if (mListener != null) {
                mListener.onScreenChanged(false);
            }
            mAloneView.setTitleViewVisibility(View.VISIBLE);
            hideOuterAfterFiveSeconds();
        }
    }
    
    /**
     * 再次播放
     */
    void onPlayAgainClick() {
        if (!NetUtils.isNetAvailable(getContext())) {
            mVideoPlayNoticeText.setText(getContext().getString(R.string.com_video_loading_fault_without_wifi));
            mVideoPlayAgainBtn.setText(getContext().getString(R.string.com_video_refresh));
            mPlayAgainLayout.setVisibility(View.VISIBLE);
            mController.stopPlayer();
        } else {
            if (!NetUtils.isWifi(getContext()) && !isWithoutWifiCouldPlay) {
                if (mPlayAgainLayout.getVisibility() == View.GONE) {
                    mPlayAgainLayout.setVisibility(View.VISIBLE);
                    mVideoPlayNoticeText.setText(getContext().getString(R.string.com_video_play_without_wifi_notice));
                    mVideoPlayAgainBtn.setText(getContext().getString(R.string.com_video_play_continue));
                    mController.stopPlayer();
                } else {
                    if (mVideoPlayAgainBtn.getText().toString().trim().equals(getContext().getString(R.string.com_video_play_continue))) {
                        isWithoutWifiCouldPlay = true;
                    }
                    mPlayAgainLayout.setVisibility(View.GONE);
                    mController.startPlayer();
                }
            } else {
                if (mVideoPlayAgainBtn.getText().toString().trim().equals(getContext().getString(R.string.com_video_play_continue))) {
                    isWithoutWifiCouldPlay = true;
                }
                mPlayAgainLayout.setVisibility(View.GONE);
                onRePlay(0);
            }
        }
    }
    
    /**
     * 重播
     *
     * @param progress
     */
    void onRePlay(int progress) {
        mPlayer.stopPlayback();
        mPlayer.setInitPlayPosition(progress);
        mPlayer.setVideoPath(mVideoUrl);
        mPlayer.start();
    }
    
    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        
    }
    
    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        if (mPlayer.getCurrentPosition() >= (mPlayer.getDuration() - 5000)) {
            mVideoPlayNoticeText.setText(getContext().getString(R.string.com_video_play_completed));
            mVideoPlayAgainBtn.setText(getContext().getString(R.string.com_video_play_again));
            mPlayAgainLayout.setVisibility(View.VISIBLE);
        } else {
            if (barTimer != null) {
                barTimer.cancel();
                barTimer = null;
            }
            if (mController.getVisibility() == View.VISIBLE) {
                mController.hide();
                if (mListener != null) {
                    mListener.onScreenChanged(true);
                }
                mAloneView.setTitleViewVisibility(View.GONE);
            }
            onRePlay(0);
        }
    }
    
    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }
    
    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }
    
    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        if (mController != null && mPlayer != null) {
            mController.onTotalCacheUpdate(percent * mPlayer.getDuration() / 100);
        }
    }
    
    @Override
    public void onPlayerStateChanged(BDCloudVideoView.PlayerState nowState) {
        if (mController != null) {
            mController.changeState();
        }
    }
    
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerReceiver();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterReceiver();
    }
    
    /**
     * 是否正在播放
     * @return
     */
    boolean isPlaying() {
        BDCloudVideoView.PlayerState playerState = mPlayer.getCurrentPlayerState();
        return playerState != null && playerState == BDCloudVideoView.PlayerState.STATE_PLAYING;
    }
    
    public class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 正在播放，才处理网络的变化
            if (isPlaying()) {
                start();
            }
        }
    }
    
    private ConnectionChangeReceiver myReceiver;
    
    void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver = new ConnectionChangeReceiver();
        getContext().registerReceiver(myReceiver, filter);
    }
    
    void unregisterReceiver() {
        if (myReceiver != null) {
            getContext().unregisterReceiver(myReceiver);
            myReceiver = null;
        }
    }
}
