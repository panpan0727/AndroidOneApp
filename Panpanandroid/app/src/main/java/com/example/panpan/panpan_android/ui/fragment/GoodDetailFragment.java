package com.example.panpan.panpan_android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.panpan.panpan_android.R;
import com.example.panpan.panpan_android.entity.EduHomeViewEntity;
import com.example.panpan.panpan_android.ui.adapter.BaseSmartRecycleAdapter;
import com.example.panpan.panpan_android.ui.adapter.GoodDetailListAdapter;
import com.example.panpan.panpan_android.utils.PagerUtils;
import com.example.panpan.panpan_android.webapi.PanpanWebApi;

import java.util.List;

/**
 * Created by Administrator on 2017/11/21.
 */

public class GoodDetailFragment extends BaseSmartRefreshListFragment {


    private TextView mTitle;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

  /*  @Override
    protected void initViews() {
        super.initViews();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.detail_fragment, null);
        mTitle = (TextView)view.findViewById(R.id.textView);
        mContentView.addView(view);
    }*/


 /*   @Override
    public void onSuccess(int requestCode,int requestId, Object response) {

        super.onSuccess(requestCode, requestId, response);
        EduHomeViewEntity mEduBrandInforEntity = (EduHomeViewEntity) response;
        if (mEduBrandInforEntity != null) {
            mTitle.setText(mEduBrandInforEntity.com);
            Log.i("dddddd",mEduBrandInforEntity.com);
        }
    }*/

    @Override
    protected void invokeListWebAPI(int pageNum) {
        PanpanWebApi.getInstance().getListData(getContext(), GoodDetailFragment.this);
    }

    @Override
    protected BaseSmartRecycleAdapter getListAdapter() {
        return new GoodDetailListAdapter(getContext());
    }

    @Override
    protected List<?> parseListData(Object result, PagerUtils.PageInfo pageInfo) {
        if(result != null){
            EduHomeViewEntity mEduBrandInforEntity = (EduHomeViewEntity) result;
            List<EduHomeViewEntity.DataBean> data = mEduBrandInforEntity.data;
            return data;
        }
        return null;
    }

    @Override
    protected void showEmptyView() {

    }

    public void refreshData(){
        loadServerData(true);
    }

    @Override
    protected boolean enableLoadMoreData() {
        return false;
    }
}
