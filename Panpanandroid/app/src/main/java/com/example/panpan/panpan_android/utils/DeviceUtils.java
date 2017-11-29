package com.example.panpan.panpan_android.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static android.content.Context.TELEPHONY_SERVICE;

public class DeviceUtils {
    
    public static final String SOURCE_APP = "0";
    public static final String SOURCE = "20";
    public static String sIMEI;
    public static String sResolution;
    public static String sVersionName;
    private static String sCPU;
    private static String sMAC;
    
    private static final int NETWORK_TYPE_2G = 1;
    private static final int NETWORK_TYPE_3G = 2;
    private static final int NETWORK_TYPE_4G = 3;
    private static final int NETWORK_TYPE_WIFI = 4;
    private static final int NETWORK_TYPE_OTHER = 5;
    private static final int NETWORK_TYPE_UNKNOWN = 6;
    
    public static String getImei(Context context) {
       try {
           if (TextUtils.isEmpty(sIMEI)) {
               sIMEI = ((TelephonyManager) context.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
           }
           return sIMEI;
       }catch (Exception e) {
           return "";
       }
    }
    
    /**
     * 获取手机IMSI号 android.permission.READ_PHONE_STATE
     **/
    public static String getIMSI(Context context) {
        if (!PermissionUtils.check(context, Manifest.permission.READ_PHONE_STATE)) {
            return "";
        }else{
            TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imsi = mTelephonyMgr.getSubscriberId();
            return imsi;
        }
    }
    
    public static String getUuid() {
        return sIMEI;
    }
    
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }
    
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }
    
    public static boolean externalStorageAvailable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }
    
    public static String getSdcardApkCachePath() {
        String a = Environment.getExternalStorageDirectory() + "/apk/";
        return a;
    }
    
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
    
    public static int dp2px(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }
    
    public static void gotoSystemAppManager(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.setClassName("com.android.settings", "com.android.settings.ManageApplications");
        context.startActivity(intent);
    }
    
    /**
     * 获取状态栏高度
     */
    public static int getStatusHeight(Activity activity) {
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }
    
    
    /*public static String getVersionName(Context context) {
        if (TextUtils.isEmpty(sVersionName)) {
            // 获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = null;
            try {
                packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if(packInfo != null){
                sVersionName = packInfo.versionName;
            }
        }
        return sVersionName;
    }*/
    
    public static int getNetworkType(Context context) {
        return convertNetworkType(getNetworkTypeStr(context));
    }
    
    public static String getNetworkTypeStr(Context context) {
        String strNetworkType = "";
        
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();
                
                Log.e("Network getSubtypeName", _strSubTypeName);
                
                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }
                        
                        break;
                }
                
                Log.e("Network getSubtype : ", Integer.valueOf(networkType).toString());
            }
        }
        
        Log.e("Network Type : ", strNetworkType);
        
        return strNetworkType;
    }
    
    private static int convertNetworkType(String type) {
        if ("2G".equals(type)) {
            return NETWORK_TYPE_2G;
        } else if ("3G".equals(type)) {
            return NETWORK_TYPE_3G;
        } else if ("4G".equals(type)) {
            return NETWORK_TYPE_4G;
        } else if ("wifi".equalsIgnoreCase(type)) {
            return NETWORK_TYPE_WIFI;
        } else {
            return NETWORK_TYPE_OTHER;
        }
    }
    
    /**
     * 获取CPU名称
     *
     * @return
     */
    public static String getCpuName() {
        if (TextUtils.isEmpty(sCPU)) {
            try {
                FileReader fr = new FileReader("/proc/cpuinfo");
                BufferedReader br = new BufferedReader(fr);
                String text = br.readLine();
                String[] array = text.split(":\\s+", 2);
                for (int i = 0; i < array.length; i++) {
                }
                sCPU = array[1];
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (sCPU == null) sCPU = "";
        return sCPU;
    }
    
    /**
     * 获取语言环境
     *
     * @param context
     * @return
     */
    public static String getLanguate(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }
    
    public static String getDeviceName() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }
    
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }
    
    public static String getPhoneModel() {
        return Build.MODEL;
    }
    
    public static int getSystemVersion() {
        return Build.VERSION.SDK_INT;
    }
    
    /**
     * 获取本地IP
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        
        return "";
    }
    
    /**
     * 根据IP获取本地Mac
     */
    public static String getLocalMacAddressFromIp(Context context) {
        if (TextUtils.isEmpty(sMAC)) {
            try {
                byte[] mac;
                NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
                mac = ne.getHardwareAddress();
                sMAC = byte2hex(mac);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (sMAC == null) sMAC = "";
        return sMAC;
    }
    
    private static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs.append("0").append(stmp);
            else {
                hs = hs.append(stmp);
            }
        }
        return String.valueOf(hs);
    }
    
    public static String getResolution(Context context) {
        if (TextUtils.isEmpty(sResolution)) {
            int num1 = getScreenWidth(context);
            int num2 = getScreenHeight(context);
            if (num1 < num2) {
                sResolution = num1 + "x" + num2;
            } else {
                sResolution = num2 + "x" + num1;
            }
        }
        return sResolution;
    }
}
