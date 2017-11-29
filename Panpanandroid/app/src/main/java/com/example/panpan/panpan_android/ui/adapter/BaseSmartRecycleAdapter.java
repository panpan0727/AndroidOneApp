package com.example.panpan.panpan_android.ui.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseSmartRecycleAdapter<T, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> implements RecyclerAdapter<T> {
    
    protected List<T> mData = new ArrayList<>();
    
    protected Context mContext;
    
    public BaseSmartRecycleAdapter(Context mContext) {
        this.mContext = mContext;
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }
    
    @Override
    public void appendData(List<T> data) {
        if (data != null && !data.isEmpty()) {
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }
    
    @Override
    public void clearData() {
        mData.clear();
        notifyDataSetChanged();
    }
    
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }
    
    protected OnItemClickLitener mOnItemClickLitener;
    
    
    @Override
    public List<T> getData() {
        return mData;
    }
    
    public T getItem(int pos) {
        return mData.get(pos);
    }
    
    public String getLogTag() {
        return getClass().getSimpleName();
    }
    
    protected <T extends View> T $(View view, @IdRes int id) {
        return (T) view.findViewById(id);
    }
    
    public void deleteItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }
    
    
}
