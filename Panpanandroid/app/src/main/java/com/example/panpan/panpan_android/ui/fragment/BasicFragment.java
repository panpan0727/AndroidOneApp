package com.example.panpan.panpan_android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.panpan.panpan_android.utils.EventBusHelper;



public class BasicFragment extends BaseFragment {
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBusHelper.register(this);
    }
    
    @Override
    public void onDestroyView() {
        EventBusHelper.unregister(this);
        super.onDestroyView();
    }
}
