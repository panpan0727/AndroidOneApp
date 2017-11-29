package com.example.panpan.panpan_android.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.panpan.panpan_android.R;


public class ImageGuideAdapter extends PagerAdapter {
    private int[] mImages;
    private String[] mTitles;
    private Context mContext;
    private OnNextPageClickListener mListener;
    
    public ImageGuideAdapter(Context context, int[] images, String [] titles, OnNextPageClickListener listener) {
        mImages = images;
        mTitles = titles;
        mContext = context;
        mListener = listener;
    }
    
    @Override
    public int getCount() {
        return mImages == null ? 0 : mImages.length;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View contentView = (View) object;
        container.removeView(contentView);
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        
        FrameLayout convertView = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.item_guide, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_guide);
        TextView iv_title = (TextView) convertView.findViewById(R.id.iv_guide_text);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_go);
        imageView.setImageResource(mImages[position]);
        iv_title.setText(mTitles[position]);
        if (position == mImages.length - 1) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener == null) {
                        return;
                    }
                    mListener.onNext();
                }
            });
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        container.addView(convertView);
        return convertView;
    }
    
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    
    public interface OnNextPageClickListener {
        void onNext();
    }
}
