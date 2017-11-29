/*
 * Created by DaXiang on  2017 
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 17-5-12 上午9:26
 */

package com.example.panpan.panpan_android.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;


public class SizeUtils {
    private static final int DESIGN_WIDTH = 750;
    private static final int DESIGN_HEIGHT = 1334;
    private static float sScreenWidth, sScreenHeight;
    
    private static DisplayMetrics getMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }
    
    public static void initScreenSize(Context context) {
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        }
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        }
        sScreenWidth = widthPixels;
        sScreenHeight = heightPixels;

        /*DisplayMetrics metrics = getMetrics(context);
        sScreenWidth = metrics.widthPixels;
        sScreenHeight = metrics.heightPixels;
        int navigation = (int) (context.getResources().getDisplayMetrics().density * 48);
        if(sScreenHeight + navigation == 2560){
            sScreenHeight = 2560;
        }else if(sScreenHeight + navigation == 1920){
            sScreenHeight = 1920;
        }else if(sScreenHeight + navigation == 1280){
            sScreenHeight = 1280;
        }*/
    }
    
    /**
     * 获取是否存在底部虚拟按键
     */
    public static boolean isBottomKeyboard(Context context) {
        boolean hasNavigationBar = false;
        try {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id);
            }
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }
    
    /**
     * 获取 虚拟按键的高度
     */
    public static int getBottomKeyboardHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int contentHeight = outMetrics.heightPixels;
        return getScreenHeight(context) - contentHeight;
    }
    
    
    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }
    
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
    
    public static int getRealWidth(Context context, int width) {
        checkScreenSize(context);
        return (int) (width * sScreenWidth / DESIGN_WIDTH);
    }
    
    public static int getRealHeight(Context context, int height) {
        checkScreenSize(context);
        return (int) (height * sScreenHeight / DESIGN_HEIGHT);
    }
    
    public static int getScreenWidth(Context context) {
        checkScreenSize(context);
        return (int) sScreenWidth;
    }
    
    public static int getScreenHeight(Context context) {
        checkScreenSize(context);
        return (int) sScreenHeight;
    }
    
    private static void checkScreenSize(Context context) {
        if (sScreenWidth == 0 || sScreenHeight == 0) {
            Point point = new Point();
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
                manager.getDefaultDisplay().getSize(point);
                sScreenWidth = point.x;
                sScreenHeight = point.y;
            } else if (Build.VERSION.SDK_INT >= 17) {
                manager.getDefaultDisplay().getRealSize(point);
                sScreenWidth = point.x;
                sScreenHeight = point.y;
            }
        }
    }
    
}
