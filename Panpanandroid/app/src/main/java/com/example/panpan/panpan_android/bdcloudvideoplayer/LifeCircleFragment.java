package com.example.panpan.panpan_android.bdcloudvideoplayer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.panpan.panpan_android.R;



public class LifeCircleFragment extends Fragment {
    JCVideoView mVideoView;
    String mVideoUrl;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_video_fragment_lifecircle, container, false);
    }
    
    public void bindLifeCircle(JCVideoView jcVideoView, String videoUrl) {
        mVideoView = jcVideoView;
        mVideoUrl = videoUrl;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (mVideoView != null) {
            mVideoView.enterForeground();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (mVideoView != null && mVideoView.isAutoPlay()) {
            mVideoView.start();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }
    
    @Override
    public void onStop() {
        // enterBackground should be invoke before super.onStop()
        if (mVideoView != null) {
            mVideoView.enterBackground();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.release();
        }
    }
}
