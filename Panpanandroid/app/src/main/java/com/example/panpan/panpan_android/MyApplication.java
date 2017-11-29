package com.example.panpan.panpan_android;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Administrator on 2017/11/28.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //initActivityRecycleListener();
    }

    /**
     * activity生命周期初始化
     */
    private void initActivityRecycleListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                Log.i("LIFECYCLE", "onActivityCreated" + activity.toString());
            }

            @Override
            public void onActivityStarted(Activity activity) {

                    Log.i("LIFECYCLE", "onActivityStarted");

            }

            @Override
            public void onActivityStopped(Activity activity) {

                    Log.i("LIFECYCLE", "onActivityStopped");

            }

            @Override
            public void onActivityResumed(Activity activity) {

                Log.i("LIFECYCLE", "onActivityResumed");

            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.i("LIFECYCLE", "onActivityPaused");
            }


            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

}
