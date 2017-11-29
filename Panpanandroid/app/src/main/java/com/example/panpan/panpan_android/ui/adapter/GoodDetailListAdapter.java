package com.example.panpan.panpan_android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.panpan.panpan_android.R;
import com.example.panpan.panpan_android.entity.EduHomeViewEntity;

/**
 * Created by Administrator on 2017/11/24.
 */

public class GoodDetailListAdapter extends BaseSmartRecycleAdapter<EduHomeViewEntity.DataBean,GoodDetailListAdapter.ListViewHolder> {


    public GoodDetailListAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.com_good_list_item, parent, false);
        return new ListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GoodDetailListAdapter.ListViewHolder holder, int position) {

        EduHomeViewEntity.DataBean entity = getItem(position);
        holder.mTimeText.setText(entity.time);
        holder.mAddressText.setText(entity.context);
    }

    class ListViewHolder extends RecyclerView.ViewHolder {

        TextView mTimeText;
        TextView mAddressText;
        public ListViewHolder(View itemView) {
            super(itemView);
            mTimeText = (TextView) itemView.findViewById(R.id.time_text);
            mAddressText = (TextView) itemView.findViewById(R.id.address_text);

        }
    }

}
