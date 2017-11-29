package com.example.panpan.panpan_android.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.example.panpan.panpan_android.R;


public class MainActivity extends BaseActivity {
    TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.textView);
        initLoadViewHelper(findViewById(R.id.textView));

        showLoadView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);//在子线程有一段耗时操作,比如请求网络
                    mHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, GoodDetailActivity.class);
                startActivity(intent);

            }
        });
    }


    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 判断消息码是否为1
            if (msg.what == 0) {
                hideLoadView();
                mTextView.setText("点击加载");
            }
        }
    };

}
