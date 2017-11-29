package com.example.panpan.panpan_android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**

/**
 * 动态获取权限，Android 6.0 新特性，一些保护权限，除了要在AndroidManifest中声明权限，还要使用如下代码动态获取
 */
public class PermissionUtils {
    /**
     * 是否有权限
     *
     * @param context
     * @param permission
     * @return true
     */
    public static boolean check(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        
    }
    
    /**
     * 申请
     *
     * @param context
     * @param permission  Manifest.permission.WRITE_EXTERNAL_STORAGE
     * @param requestCode
     */
    public static void request(Context context, String permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {permission};
            if (context instanceof Activity) {
                ((Activity) context).requestPermissions(permissions, requestCode);
            }
        }
    }
}
