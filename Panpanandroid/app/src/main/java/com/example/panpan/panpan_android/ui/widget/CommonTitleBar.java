package com.example.panpan.panpan_android.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.panpan.panpan_android.R;
import com.example.panpan.panpan_android.utils.DeviceUtils;


public class CommonTitleBar extends RelativeLayout implements View.OnClickListener {
    private Context mContext;

    private TextView mTitleTV, mTitleIcon;
    private TextView mLeftTV, mRightTV;
    private ImageView mLeftIV, mRightIV;
    private View mLeftMark, mRightMark;
    private OnTitleBarClickListener mListener;

    private String mTitleStr, mLeftStr, mRightStr;
    private int mLeftResId, mRightResId, mTitleIconId;
    private int mMenuMaxWidth;
    private int mBarHeight;

    public CommonTitleBar(Context context) {
        this(context, null);
    }

    public CommonTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initView(context);
    }

    public void setOnTitleBarClickListener(OnTitleBarClickListener listener) {
        mListener = listener;
    }

    public interface OnTitleBarClickListener {
        void onLeftClick(View v);

        void onRightClick(View v);
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) return;
        int viewId = v.getId();
        if (R.id.com_title_left_text == viewId || R.id.com_title_left_image == viewId) {
            mListener.onLeftClick(v);
        } else if (R.id.com_title_right_text == viewId || R.id.com_title_right_image == viewId) {
            mListener.onRightClick(v);
        }
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mMenuMaxWidth = getResources().getDimensionPixelSize(R.dimen.dp60);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.comTitleBar);
        mBarHeight = array.getDimensionPixelSize(R.styleable.comTitleBar_title_bar_height, getResources().getDimensionPixelSize(R.dimen.com_title_bar_height));
        mTitleStr = array.getString(R.styleable.comTitleBar_title_text);
        mLeftStr = array.getString(R.styleable.comTitleBar_title_left_text);
        mRightStr = array.getString(R.styleable.comTitleBar_title_right_text);
        mLeftResId = array.getResourceId(R.styleable.comTitleBar_title_left_image, 0);
        mRightResId = array.getResourceId(R.styleable.comTitleBar_title_right_image, 0);
        mTitleIconId = array.getResourceId(R.styleable.comTitleBar_title_icon, 0);

        array.recycle();
    }

    private void initView(Context context) {
        mContext = context;
        if (!TextUtils.isEmpty(mTitleStr)) {
            setTitleText(mTitleStr);
        }
        if (!TextUtils.isEmpty(mLeftStr)) {
            setLeftText(mLeftStr);
        }
        if (!TextUtils.isEmpty(mRightStr)) {
            setRightText(mRightStr);
        }
        if (mLeftResId > 0) {
            setLeftImage(mLeftResId);
        }
        if (mRightResId > 0) {
            setRightImage(mRightResId);
        }
        if (mTitleIconId > 0) {
            setTitleDrawable(mTitleIconId);
        }
    }

    public void setTitleText(String text) {
        if (mTitleTV == null) {
            mTitleTV = createTitleTV();
        }
        mTitleTV.setText(text);
    }

    public void setLeftText(String text) {
        if (mLeftTV == null) {
            mLeftTV = createTV(true);
        }
        mLeftTV.setText(text);
    }

    public void setRightText(String text) {
        if (mRightTV == null) {
            mRightTV = createTV(false);
        }
        mRightTV.setText(text);
    }

    public void setLeftImage(int imgResId) {
        if (mLeftIV == null) {
            mLeftIV = createIV(true);
        }
        mLeftIV.setImageResource(imgResId);
    }

    public void setRightImage(int imgResId) {
        if (mRightIV == null) {
            mRightIV = createIV(false);
        }
        mRightIV.setImageResource(imgResId);
    }

    public void setLeftMark() {

    }

    public void setRightMark() {

    }

    private TextView createTitleTV() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView tv = new TextView(getContext());
        params.addRule(CENTER_IN_PARENT);
        tv.setLayoutParams(params);
        tv.setMaxWidth(DeviceUtils.getScreenWidth(mContext) - getResources().getDimensionPixelSize(R.dimen.dp60) * 2);
        tv.setSingleLine();
        tv.setGravity(Gravity.CENTER);
        tv.setMaxLines(1);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.sp16));
        tv.setTextColor(getResources().getColor(R.color.com_colorTitleText));
        addView(tv);
        return tv;
    }

    private TextView createTV(boolean isLeft) {
        TextView tv = new TextView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mBarHeight);
        if (isLeft) {
            tv.setId(R.id.com_title_left_text);
            params.addRule(ALIGN_PARENT_LEFT);
        } else {
            tv.setId(R.id.com_title_right_text);
            params.addRule(ALIGN_PARENT_RIGHT);
        }
        params.addRule(CENTER_VERTICAL);
        tv.setLayoutParams(params);
        tv.setPadding(0, 0, getResources().getDimensionPixelSize(R.dimen.dp5), 0);
        tv.setMaxWidth(mMenuMaxWidth);
        tv.setMinWidth(mBarHeight);
        tv.setSingleLine();
        tv.setMaxLines(1);
        tv.setGravity(Gravity.CENTER);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.sp12));
        tv.setTextColor(getResources().getColor(R.color.com_colorMenuText));
        addView(tv);
        tv.setOnClickListener(this);
        return tv;
    }

    private ImageView createIV(boolean isLeft) {
        ImageView iv = new ImageView(getContext());
        LayoutParams params = new LayoutParams(mBarHeight, mBarHeight);
        if (isLeft) {
            iv.setId(R.id.com_title_left_image);
            params.addRule(ALIGN_PARENT_LEFT);
        } else {
            iv.setId(R.id.com_title_right_image);
            params.addRule(ALIGN_PARENT_RIGHT);
        }
        params.addRule(CENTER_VERTICAL);
        iv.setMaxWidth(mMenuMaxWidth);
        iv.setLayoutParams(params);
        iv.setScaleType(ImageView.ScaleType.CENTER);
        addView(iv);
        iv.setOnClickListener(this);
        return iv;
    }

    public void setTitleDrawable(@DrawableRes int id) {
        if (mTitleTV == null)
            mTitleTV = createTitleTV();
        mTitleTV.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.dp2));
        mTitleTV.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(id), null);
    }

    public void setOnTitleTextClickListener(OnClickListener listener) {
        if (mTitleTV == null)
            return;
        mTitleTV.setOnClickListener(listener);
    }

    /**
     * 设置左边ImageView可见性
     *
     * @param visible
     */
    public void setLeftIvVisible(int visible) {
        if (mLeftIV == null) {
            return;
        }
        mLeftIV.setVisibility(visible);
    }

    /**
     * 设置右边TextView 可见性
     *
     * @param visible
     */
    public void setRightTvVisible(int visible) {
        if (mRightTV == null) {
            return;
        }
        mRightTV.setVisibility(visible);
    }
}
