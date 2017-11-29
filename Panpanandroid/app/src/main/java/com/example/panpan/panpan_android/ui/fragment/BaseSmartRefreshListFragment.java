package com.example.panpan.panpan_android.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.panpan.panpan_android.R;
import com.example.panpan.panpan_android.constant.ExceptionCode;
import com.example.panpan.panpan_android.ui.adapter.BaseSmartRecycleAdapter;
import com.example.panpan.panpan_android.ui.adapter.RecyclerAdapter;
import com.example.panpan.panpan_android.ui.widget.RecycleDividerDecoration;
import com.example.panpan.panpan_android.ui.widget.SmartRecycleHeader;
import com.example.panpan.panpan_android.utils.PagerUtils;
import com.example.panpan.panpan_android.utils.RunUiThread;
import com.example.panpan.panpan_android.utils.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;



public abstract class BaseSmartRefreshListFragment extends BaseFragment {
    
    protected boolean mIsLoadingList = false;
    protected boolean mIsRefreshListOfLatestLoading = true;
    protected boolean mHasLoadedData = false;
    protected PagerUtils.PageInfo mPageInfo = new PagerUtils.PageInfo(0, 0, 0);
    
    protected SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerAdapter recyclerAdapter;
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView;
        fragmentView = inflater.inflate(getInflateLayoutResourceId(), container, false);
        return fragmentView;
    }
    
    protected int getInflateLayoutResourceId() {
        return R.layout.com_base_smart_refresh_listfragment;
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
    public void onDestroy() {
        super.onDestroy();
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
            Log.i(getLogTag(), "loadDataOnActivityCreated");
            if (enableLoadDataAfterActivityCreated()) {
                loadServerData(true);
            }
        }
    }
    
    protected void loadDataOnResume() {
        if (needRefreshDataWhenVisibleToUserEveryTime() &&
                (!isInViewPager()
                        || isInViewPager() && getUserVisibleHint())) {
            Log.i(getLogTag(), "refresh list data when onResume");
            loadServerData(true);
        }
    }
    
    protected void loadDataWhenVisibleToUser() {
        boolean isViewCreated = (isResumed() || getView() != null);
        if (isInViewPager() && isViewCreated) {
            if (needRefreshDataWhenVisibleToUserEveryTime()) {
                Log.i(getLogTag(), "refresh data when visible to user every time");
                loadServerData(true);
            } else if (!mHasLoadedData) {
                Log.i(getLogTag(), "loadDataWhenVisibleToUser");
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
        initLoadViewHelper(getViewHelp());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_listview);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //当前RecyclerView显示出来的最后一个的item的position
                int lastPosition = -1;
                
                //当前状态为停止滑动状态SCROLL_STATE_IDLE时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof GridLayoutManager) {
                        //通过LayoutManager找到当前显示的最后的item的position
                        lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                    } else if (layoutManager instanceof LinearLayoutManager) {
                        lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
                        //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
                        int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                        ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
                        lastPosition = findMax(lastPositions);
                    }
                    
                    //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
                    //如果相等则说明已经滑动到最后了
                    if (lastPosition == recyclerView.getLayoutManager().getItemCount() - 1) {
                        mRefreshLayout.autoLoadmore();
                    }
                    
                }
            }
        });
    }

    public View getViewHelp(){
        return mRefreshLayout;
    }
    
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
    
    protected void initViews() {
        initSmartRefresh();
        initRecycleView();
    }
    
    @Override
    protected void hideLoadView() {
        super.hideLoadView();
        RunUiThread.run(new Runnable() {
            @Override
            public void run() {
                if (!(mRefreshLayout.getRefreshHeader() instanceof SmartRecycleHeader)) {
                    if (!isAdded() || isDetached())
                        return;
                    mRefreshLayout.setRefreshHeader(new SmartRecycleHeader(getActivity()));
                    mRefreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
                }
            }
        }, 1000);
    }
    
    protected void initSmartRefresh() {
        mRefreshLayout.setDisableContentWhenLoading(true);
        mRefreshLayout.setDisableContentWhenRefresh(true);
        mRefreshLayout.setEnableRefresh(enableRefreshData());
        mRefreshLayout.setEnableLoadmore(enableLoadMoreData());
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
    
    protected boolean enableLoadMoreData() {
        return true;
    }
    
    private void initRecycleView() {
        mLayoutManager = getLayoutManager();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = getListAdapter();
        if (getDividerDrawableResourceId() > 0) {
            mRecyclerView.addItemDecoration(new RecycleDividerDecoration(getActivity(), DividerItemDecoration.VERTICAL, getResources().getDrawable(getDividerDrawableResourceId())));
        }
        mRecyclerView.setAdapter(mAdapter);
        recyclerAdapter = (RecyclerAdapter) mAdapter;
//        mAdapter.notifyDataSetChanged();
    }
    
    protected int getDividerDrawableResourceId() {
        return R.drawable.com_divider_order_list;
    }
    
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
    }
    
    protected void loadServerData(boolean refresh) {
        if (mIsLoadingList) {
            Log.i(getLogTag(), "is loading data now");
            return;
        }
        mIsLoadingList = true;
        mHasLoadedData = true;
        mIsRefreshListOfLatestLoading = refresh;
        if (recyclerAdapter.getData() != null && recyclerAdapter.getData().size() == 0) {
            showLoadingView();
        }
        if (refresh) {
            mPageInfo.pageNo = 1;
        } else {
            mPageInfo.pageNo += 1;
        }
        invokeListWebAPI(mPageInfo.pageNo);
    }
    
    @Override
    public void onSuccess(int requestCode, int requestId, Object response) {
        super.onSuccess(requestCode, requestId, response);
        hideLoadView();
        mIsLoadingList = false;
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.finishRefresh();
        }
        if (isDetached()) {
            Log.i(getLogTag(), getLogTag() + " has detached to activity, do nothing");
            return;
        }
        
        List<?> tmpData = parseListData(response, mPageInfo);
        
        // 如果是第一页，清除原有数据
        if (PagerUtils.isFirstPage(mPageInfo)) {
            recyclerAdapter.clearData();
        }
        recyclerAdapter.appendData(tmpData);
        
        if (mRefreshLayout.isLoading()) {
            mRefreshLayout.finishLoadmore();
        }
        
        // mRecyclerView.canScrollVertically(1);//是否能向上滚动
        
        if (PagerUtils.isLastPage(mPageInfo)) {
            Log.i(getLogTag(),"-------isLastPage------" + mPageInfo.pageSize + " / "+mPageInfo.pageNo);
            mRefreshLayout.setLoadmoreFinished(true);
        } else {
            mRefreshLayout.setLoadmoreFinished(false);
        }
        
        if (recyclerAdapter.getData().size() == 0) {
            showEmptyView();
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
        if (mAdapter != null && recyclerAdapter.getData().size() > 0) {
            ToastUtils.show(R.string.com_prompt_load_failed);
        } else {
            if (requestCode == ExceptionCode.NO_INTERNET) {
                showLoadError(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadServerData(true);
                    }
                });
            } else {
                showLoadError(new View.OnClickListener() { // TODO: 2017/9/20 网络错误
                    @Override
                    public void onClick(View v) {
                        loadServerData(true);
                    }
                });
            }
        }
    }
    
    protected abstract void invokeListWebAPI(int pageNum);
    
    protected abstract BaseSmartRecycleAdapter getListAdapter();
    
    protected abstract List<?> parseListData(Object result, PagerUtils.PageInfo pageInfo);
    
    protected abstract void showEmptyView();
    
}
