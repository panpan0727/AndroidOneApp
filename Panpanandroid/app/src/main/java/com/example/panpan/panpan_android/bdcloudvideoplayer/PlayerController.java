package com.example.panpan.panpan_android.bdcloudvideoplayer;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.example.panpan.panpan_android.R;
import com.example.panpan.panpan_android.utils.NetUtils;

import java.util.Timer;
import java.util.TimerTask;



public class PlayerController extends RelativeLayout implements View.OnClickListener {
    
    private ImageView mPlayBtn;
    private SeekBar seekBar;
    private TextView mPositionText;
    private TextView mDurationText;
    private ImageView mScreenView;
    
    private BDCloudVideoView mVideoView = null;
    
    //是否在拖动进度条
    private boolean mbIsDragging = false;
    
    //是否设置过进度条时长
    private boolean isMaxSetted = false;
    
    //视频当前播放到的位置
    private long currentPositionInMilliSeconds = 0L;
    
    private AnimationSet animationSet;
    
    private boolean isFinishPosition = false;
    
    public void setScreenBtnImg(int imgId) {
        mScreenView.setImageDrawable(ActivityCompat.getDrawable(getContext(), imgId));
    }
    private void initAnimationSet() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setFillAfter(false);
        animationSet.setDuration(1200);
        animationSet.setRepeatCount(0);
    }
    
    private String[] availableResolution = null;
    
    public String[] getAvailableResolution() {
        return availableResolution;
    }
    
    public void setAvailableResolution(String[] fetchResolution) {
        if (fetchResolution != null && fetchResolution.length > 1) {
            String[] availableResolutionDesc = new String[fetchResolution.length];
            for (int i = 0; i < fetchResolution.length; ++i) {
                availableResolutionDesc[i] = getDescriptionOfResolution(fetchResolution[i]);
            }
            this.availableResolution = availableResolutionDesc;
        }
        
    }
    
    private Timer mPositionTimer;
    private static final int POSITION_REFRESH_TIME = 500;
    
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    
    public Handler getMainThreadHandler() {
        return mainThreadHandler;
    }
    
    public PlayerController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public PlayerController(Context context) {
        super(context);
        initView();
    }
    
    private void initView() {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.com_video_bar_advanced_media_controller, this);
        
        mPlayBtn = (ImageView) layout.findViewById(R.id.ibtn_play);
        seekBar = (SeekBar) layout.findViewById(R.id.seekbar);
        mPositionText = (TextView) layout.findViewById(R.id.tv_position);
        mDurationText = (TextView) layout.findViewById(R.id.tv_duration);
        mScreenView = (ImageView) layout.findViewById(R.id.ibtn_screen_control);
        
        seekBar.setMax(0);
        
        initListener();
        enableControllerBar(false);
        initAnimationSet();
    }
    
    private void initListener() {
        mPlayBtn.setOnClickListener(this);
        
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //更新播放进度的 时间
                updatePostion(progress);
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mbIsDragging = true;
                if (onMyListener != null) {
                    onMyListener.onSeekTouch();
                }
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mVideoView.getDuration() > 0) {
                    //仅非直播的视频支持拖动
                    if (seekBar.getProgress() == mVideoView.getDuration()) {
                        currentPositionInMilliSeconds = (seekBar.getProgress() > 1000 ? seekBar.getProgress() - 1000 : 0);
                    }
                    if (mVideoView != null) {
                        //视频播放位置跳转到当前位置
                        mVideoView.seekTo(seekBar.getProgress() == mVideoView.getDuration() ? (seekBar.getProgress() > 1000 ? seekBar.getProgress() - 1000 : 0) : seekBar.getProgress());
                    }
                }
                
                mbIsDragging = false;
                if (onMyListener != null) {
                    onMyListener.onSeekTouchStop();
                }
            }
        });
        mScreenView.setOnClickListener(this);
        
    }
    
    /**
     * 设置 mVideoView
     *
     * @param player
     */
    public void setMediaPlayerControl(BDCloudVideoView player) {
        mVideoView = player;
    }
    
    public void changeState() {
        final BDCloudVideoView.PlayerState state = mVideoView.getCurrentPlayerState();
        isMaxSetted = false;
        mainThreadHandler.post(new TimerTask() {
            @Override
            public void run() {
                if (state == BDCloudVideoView.PlayerState.STATE_IDLE || state == BDCloudVideoView.PlayerState.STATE_ERROR) {
                    stopPositionTimer();
                    mPlayBtn.setEnabled(true);
                    mPlayBtn.setImageDrawable(ActivityCompat.getDrawable(getContext(), R.mipmap.com_video_toggle_btn_play));
                    seekBar.setEnabled(false);
                    updatePostion(mVideoView == null ? 0 : mVideoView.getCurrentPosition());
                    updateDuration(mVideoView == null ? 0 : mVideoView.getDuration());
                    if(onMyListener != null) {
                        onMyListener.onStop();
                    }
                } else if (state == BDCloudVideoView.PlayerState.STATE_PREPARING) {
                    mPlayBtn.setEnabled(false);
                    seekBar.setEnabled(false);
                } else if (state == BDCloudVideoView.PlayerState.STATE_PREPARED) {
                    mPlayBtn.setEnabled(true);
                    mPlayBtn.setImageDrawable(ActivityCompat.getDrawable(getContext(), R.mipmap.com_video_toggle_btn_play));
                    seekBar.setEnabled(true);
                    // set width
                    String resolutionStr = mVideoView.getVideoWidth() + "x" + mVideoView.getVideoHeight();
                    setAvailableResolution(mVideoView.getVariantInfo());
                    updateDuration(mVideoView == null ? 0 : mVideoView.getDuration());
                    seekBar.setMax(mVideoView.getDuration());
                    if(onMyListener != null) {
                        onMyListener.onStop();
                    }
                } else if (state == BDCloudVideoView.PlayerState.STATE_PLAYBACK_COMPLETED) {
                    stopPositionTimer();
                    seekBar.setProgress(seekBar.getMax());
                    seekBar.setEnabled(false);
                    mPlayBtn.setEnabled(true);
                    mPlayBtn.setImageDrawable(ActivityCompat.getDrawable(getContext(), R.mipmap.com_video_toggle_btn_play));
                    isFinishPosition = true;
                    if(onMyListener != null) {
                        onMyListener.onStop();
                    }
                } else if (state == BDCloudVideoView.PlayerState.STATE_PLAYING) {
                    startPositionTimer();
                    seekBar.setEnabled(true);
                    mPlayBtn.setEnabled(true);
                    mPlayBtn.setImageDrawable(ActivityCompat.getDrawable(getContext(), R.mipmap.com_video_toggle_btn_pause));
                    if(onMyListener != null) {
                        onMyListener.onPlay();
                    }
                } else if (state == BDCloudVideoView.PlayerState.STATE_PAUSED) {
                    stopPositionTimer();
                    mPlayBtn.setEnabled(true);
                    mPlayBtn.setImageDrawable(ActivityCompat.getDrawable(getContext(), R.mipmap.com_video_toggle_btn_play));
                    if(onMyListener != null) {
                        onMyListener.onStop();
                    }
                }
            }
        });
    }
    
    /**
     * 视频当前播放的时间
     *
     * @param milliSecond
     */
    private void updatePostion(int milliSecond) {
        if (mPositionText != null) {
            mPositionText.setText(formatMilliSecond(milliSecond));
        }
    }
    
    /**
     * 更新视频 总时长
     *
     * @param milliSecond
     */
    private void updateDuration(int milliSecond) {
        if (mDurationText != null) {
            mDurationText.setText(formatMilliSecond(milliSecond));
        }
    }
    
    /**
     * 时间转换为 时:分:秒
     *
     * @param milliSecond
     * @return
     */
    public String formatMilliSecond(int milliSecond) {
        int seconds = milliSecond / 1000;
        int hh = seconds / 3600;
        int mm = seconds % 3600 / 60;
        int ss = seconds % 60;
        String strTemp = null;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
        return strTemp;
    }
    
    private void enableControllerBar(boolean isEnable) {
        seekBar.setEnabled(isEnable);
        mPlayBtn.setEnabled(isEnable);
    }
    
    private void startPositionTimer() {
        if (mPositionTimer != null) {
            mPositionTimer.cancel();
            mPositionTimer = null;
        }
        mPositionTimer = new Timer();
        mPositionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //在主线程 刷新 UI
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPositionUpdate();
                    }
                });
            }
        }, 0, POSITION_REFRESH_TIME);
    }
    
    private void stopPositionTimer() {
        if (mPositionTimer != null) {
            mPositionTimer.cancel();
            mPositionTimer = null;
        }
    }
    
    private boolean onPositionUpdate() {
        if (mVideoView == null) {
            return false;
        }
        //获取当前视频播放时间位置
        long newPositionInMilliSeconds = mVideoView.getCurrentPosition();
        
        long previousPosition = currentPositionInMilliSeconds;
        
        if (newPositionInMilliSeconds > 0 && !getIsDragging()) {
            currentPositionInMilliSeconds = newPositionInMilliSeconds;
        }
        if (getVisibility() != View.VISIBLE) {
            //如果进度条不可见，则不设置进度
            return false;
        }
        
        if (!getIsDragging()) {
            int durationInMilliSeconds = mVideoView.getDuration();
            if (durationInMilliSeconds > 0) {
                // 设置进度条时长为 视频的时长
                this.setMax(durationInMilliSeconds);
                // 直播视频的duration为0，此时不设置进度
                if (previousPosition != newPositionInMilliSeconds) {
                    this.setProgress((int) newPositionInMilliSeconds);
                }
            }
        }
        
        return false;
    }
    
    /**
     * 进度条是否在拖拽
     *
     * @return
     */
    public boolean getIsDragging() {
        return mbIsDragging;
    }
    
    public void setIsDragging(boolean mbIsDragging) {
        this.mbIsDragging = mbIsDragging;
    }
    
    public void setMax(int maxProgress) {
        if (isMaxSetted) {
            return;
        }
        if (seekBar != null) {
            seekBar.setMax(maxProgress);
        }
        //更新视频总时长
        updateDuration(maxProgress);
        if (maxProgress > 0) {
            isMaxSetted = true;
        }
    }
    
    /**
     * 设置 进度条当前所播放的位置
     *
     * @param progress
     */
    public void setProgress(int progress) {
        if (seekBar != null) {
            seekBar.setProgress(progress);
        }
    }
    
    /**
     * 设置视频缓存位置
     *
     * @param cache
     */
    public void setCache(int cache) {
        if (seekBar != null && cache != seekBar.getSecondaryProgress()) {
            seekBar.setSecondaryProgress(cache);
        }
    }
    
    /**
     * 设置当前缓存条位置
     *
     * @param milliSeconds
     */
    public void onTotalCacheUpdate(final long milliSeconds) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setCache((int) milliSeconds);
            }
        });
    }
    
    /**
     * 设置可见
     */
    public void show() {
        if (mVideoView == null) {
            return;
        }
        setProgress((int) currentPositionInMilliSeconds);
        
        this.setVisibility(VISIBLE);
    }
    
    /**
     * 设置不可见
     */
    public void hide() {
        this.setVisibility(GONE);
    }
    
    /**
     * 停止计时
     */
    public void release() {
        stopPositionTimer();
    }
    
    
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ibtn_play) {
            if (mVideoView == null)
                return;
            
            if (mVideoView.isPlaying()) {
                mPlayBtn.setImageDrawable(ActivityCompat.getDrawable(getContext(), R.mipmap.com_video_toggle_btn_play));
                mVideoView.pause();
                if(onMyListener != null) {
                    onMyListener.onStop();
                }
            } else {
                if(NetUtils.isNetAvailable(getContext())) {
                    if (isFinishPosition) {
                        isFinishPosition = false;
                        if (onMyListener != null) {
                            onMyListener.onRePlayVideo();
                        }
        
                    } else {
                        if (onMyListener != null) {
                            onMyListener.onPlay();
                        }
                        mPlayBtn.setImageDrawable(ActivityCompat.getDrawable(getContext(), R.mipmap.com_video_toggle_btn_pause));
                        mVideoView.start();
                    }
                }
            }
            
        } else if (i == R.id.ibtn_screen_control) {
            if (onMyListener != null) {
                onMyListener.onScreenChanged();
            }
            //}else if(i == R.id.ibtn_screen_zan) {
        }
    }
    
    public String getDescriptionOfResolution(String resolutionType) {
        String result = "未知";
        try {
            String[] cuts1 = resolutionType.trim().split(",");
            if (cuts1[0].length() > 0) {
                String[] cuts2 = cuts1[0].trim().split("[xX]");
                if (cuts2.length == 2) {
                    // get the height size
                    int iResult = Integer.parseInt(cuts2[1]);
                    if (iResult <= 0) {
                        result = "未知";
                    } else if (iResult <= 120) {
                        result = "120P";
                    } else if (iResult <= 240) {
                        result = "240P";
                    } else if (iResult <= 360) {
                        result = "360P";
                    } else if (iResult <= 480) {
                        result = "480P";
                    } else if (iResult <= 800) {
                        result = "720P";
                    } else {
                        result = "1080P";
                    }
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        
        return result;
    }
    
    public void stopPlayer() {
        mPlayBtn.setImageDrawable(ActivityCompat.getDrawable(getContext(), R.mipmap.com_video_toggle_btn_play));
        mVideoView.pause();
    }
    
    public void startPlayer() {
        if (onMyListener != null) {
            onMyListener.onPlay();
        }
        mPlayBtn.setImageDrawable(ActivityCompat.getDrawable(getContext(), R.mipmap.com_video_toggle_btn_pause));
        mVideoView.start();
    }
    
    public void seekToTime(int time) {
        mVideoView.seekTo(time);
    }
    
    public String formatMilliSecond2(int seconds) {
        int hh = seconds / 3600;
        int mm = seconds % 3600 / 60;
        int ss = seconds % 60;
        String strTemp = null;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
        return strTemp;
    }
    
    public interface OnMyListener {
        void onStop();
        void onPlay();
        void onScreenChanged();
        void onRePlayVideo();
        void onSeekTouch();
        void onSeekTouchStop();
    }
    
    private OnMyListener onMyListener;
    
    public void setListener(OnMyListener myListener) {
        this.onMyListener = myListener;
    }
}
