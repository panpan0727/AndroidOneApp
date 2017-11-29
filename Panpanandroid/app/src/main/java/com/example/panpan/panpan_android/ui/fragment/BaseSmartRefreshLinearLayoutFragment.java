package com.example.panpan.panpan_android.ui.fragment;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.panpan.panpan_android.R;
import com.example.panpan.panpan_android.ui.widget.SmartRecycleHeader;
import com.example.panpan.panpan_android.utils.LogUtils;
import com.example.panpan.panpan_android.utils.RunUiThread;
import com.example.panpan.panpan_android.utils.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;



public abstract class BaseSmartRefreshLinearLayoutFragment extends BaseFragment {
    
    protected SmartRefreshLayout mRefreshLayout;
    protected LinearLayout mContentView;
    
    protected boolean mIsLoadingList = false;
    protected boolean mHasLoadedData = false;
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView;
        fragmentView = inflater.inflate(getInflateLayoutResourceId(), container, false);
        return fragmentView;
    }
    
    protected int getInflateLayoutResourceId() {
        return R.layout.com_base_smart_refresh_linearlayout_fragment;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadDataOnActivityCreated();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadDataOnResume();
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            loadDataWhenVisibleToUser();
        }
    }
    
    protected boolean enableLoadDataAfterActivityCreated() {
        return true;
    }
    
    protected void loadDataOnActivityCreated() {
        if (!isInViewPager()
                || (isInViewPager() && !mHasLoadedData && getUserVisibleHint())) {
            LogUtils.i(getLogTag(), "loadDataOnActivityCreated");
            if (enableLoadDataAfterActivityCreated()) {
                loadServerData(true);
            }
        }
    }
    
    protected void loadDataOnResume() {
        if (needRefreshDataWhenVisibleToUserEveryTime() &&
                (!isInViewPager()
                        || isInViewPager() && getUserVisibleHint())) {
            LogUtils.i(getLogTag(), "refresh list data when onResume");
            loadServerData(true);
        }
    }
    
    protected void loadDataWhenVisibleToUser() {
        boolean isViewCreated = (isResumed() || getView() != null);
        if (isInViewPager() && isViewCreated) {
            if (needRefreshDataWhenVisibleToUserEveryTime()) {
                LogUtils.i(getLogTag(), "refresh data when visible to user every time");
                loadServerData(true);
            } else if (!mHasLoadedData) {
                LogUtils.i(getLogTag(), "loadDataWhenVisibleToUser");
                loadServerData(true);
            }
        }
    }
    
    protected boolean isInViewPager() {
        return false;
    }
    
    protected boolean needRefreshDataWhenVisibleToUserEveryTime() {
        return false;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews();
        initViews();
    }
    
    protected void findViews() {
        View view = getView();
        mRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
        initLoadViewHelper(mRefreshLayout);
        mContentView = (LinearLayout) view.findViewById(R.id.content_view);
    }
    
    protected void initViews() {
        initSmartRefresh();
    }
    
    @Override
    protected void hideLoadView() {
        super.hideLoadView();
        RunUiThread.run(new Runnable() {
            @Override
            public void run() {
                if (!(mRefreshLayout.getRefreshHeader() instanceof SmartRecycleHeader)) {
                    if (!isAdded() || isDetached() || isRemoving())
                        return;
                    mRefreshLayout.setRefreshHeader(new SmartRecycleHeader(getActivity()));
                    mRefreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
                }
            }
        }, 1000);
    }
    
    protected void initSmartRefresh() {
        mRefreshLayout.setEnableRefresh(enableRefreshData());
        mRefreshLayout.setEnableLoadmore(false);
        mRefreshLayout.setRefreshHeader(new SmartRecycleHeader(getActivity()));
        mRefreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                loadServerData(true);
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadServerData(false);
            }
        });
        mRefreshLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });
    }
    
    protected boolean enableRefreshData() {
        return true;
    }
    
    protected boolean pageHasLoadData = false;
    
    protected void loadServerData(boolean refresh) {
        if (mIsLoadingList) {
            LogUtils.i(getLogTag(), "is loading data now");
            return;
        }
        mIsLoadingList = true;
        mHasLoadedData = true;
        if (!pageHasLoadData) {
            showLoadingView();
        }
        invokeListWebAPI();
    }

    @Override
    public void onSuccess(int requestCode, int requestId, Object response) {
        super.onSuccess(requestCode, requestId, response);
        hideLoadView();
        mIsLoadingList = false;
        pageHasLoadData = true;
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.finishRefresh();
        }
        if (isDetached()) {
            LogUtils.i(getLogTag(), getLogTag() + " has detached to activity, do nothing");
            return;
        }
    }

    @Override
    public void onFailure(int responseCode, int requestCode, int requestId, String errMsg) {
        super.onFailure(responseCode, requestCode, requestId, errMsg);
        hideLoadView();
        mIsLoadingList = false;
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.finishRefresh();
        }
        if (mRefreshLayout.isLoading()) {
            mRefreshLayout.finishLoadmore();
        }
        if (pageHasLoadData) {
            ToastUtils.show(R.string.com_prompt_load_failed);
        } else {
            showLoadError(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadServerData(true);
                }
            });
        }
    }
    
    protected abstract void invokeListWebAPI();
    
}
