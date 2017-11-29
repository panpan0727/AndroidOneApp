package com.example.panpan.panpan_android.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.StrikethroughSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 通用工具类
 */
public class Utility {

    public static final String THIS_DIR = "DDG";

    private static Gson sGson = new Gson();

    public static String getLogTag() {
        return Utility.class.getSimpleName();
    }

    public static Gson getGson() {
        return sGson;
    }

    public static final int getVersionCode(Context context) {
        int verCode = 0;
        try {
            PackageInfo appInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            verCode = appInfo.versionCode;
        } catch (Exception e) {
        }
        return verCode;
    }

    public static String getRawVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String versionName = "";
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }


    public static boolean isFlyme() {
        if ("Meizu".equalsIgnoreCase(Build.MANUFACTURER)
                || "Meizu".equalsIgnoreCase(Build.BRAND)) {
            return true;
        }
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }


    /**
     * 计算最大的内存缓存大小
     *
     * @param context
     * @return
     */
    public static int calculateMemoryCacheSize(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();
        if (largeHeap && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            memoryClass = am.getLargeMemoryClass();
        }
        // Target ~15% of the available heap.
        return 1024 * 1024 * memoryClass / 7;
    }

    /**
     * 格式化浮点数
     *
     * @param doubleString 浮点数字符串
     * @param accuracy     保留几位小数
     * @return 格式化后的字符串
     */
    public static String formatDouble(String doubleString, int accuracy) {
        double value = 0f;
        try {
            value = Double.parseDouble(doubleString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatDouble(value, accuracy);
    }

    /**
     * 格式化浮点数
     *
     * @param floatValue 浮点数值
     * @param accuracy   保留几位小数
     * @return 格式化后的字符串
     */
    public static float formatFloat(float floatValue, int accuracy) {
        String format = "0";
        if (accuracy > 0) {
            StringBuilder decimalPlaceHolder = new StringBuilder();
            for (int i = 1; i <= accuracy; i++) {
                decimalPlaceHolder.append("0");
            }
            format = "0." + decimalPlaceHolder.toString();
        }
        DecimalFormat decimalFormat = new DecimalFormat(format);
        String formatString = decimalFormat.format(floatValue);
        return Float.parseFloat(formatString);
    }

    /**
     * 格式化浮点数
     *
     * @param doubleValue 浮点数值
     * @param accuracy    保留几位小数
     * @return 格式化后的字符串
     */
    public static String formatDouble(double doubleValue, int accuracy) {
        String format = "0";
        if (accuracy > 0) {
            StringBuilder decimalPlaceHolder = new StringBuilder();
            for (int i = 1; i <= accuracy; i++) {
                decimalPlaceHolder.append("0");
            }
            format = "0." + decimalPlaceHolder.toString();
        }
        DecimalFormat decimalFormat = new DecimalFormat(format);
        String formatString = decimalFormat.format(doubleValue);
        return formatString;
    }

    public static String getStrFromByte(byte[] responseBody) {
        String response = "";
        if (responseBody != null) {
            try {
                response = new String(responseBody, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return response;
    }







    /**
     * 距离数值转换为字符串
     *
     * @param distance 距离，单位米
     * @return 距离字符串
     */
    public static String formatDistance(long distance) {
        String result;
        if (distance < 100) {
            result = "<100m";
        } else if (distance < 500) {
            result = "<500m";
        } else {
            result = String.format("%.1fkm", distance * 1.0 / 1000);
        }
        return result;
    }

    public static String formatDistance(String distanceString) {
        if (!TextUtils.isEmpty(distanceString)) {
            try {
                long distance = Long.parseLong(distanceString);
                return formatDistance(distance);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }




    public static long parseLong(String longString, long defaultValue) {
        if (TextUtils.isEmpty(longString)) {
            return defaultValue;
        }
        long value = defaultValue;
        try {
            value = Long.parseLong(longString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static int parseInt(String intString, int defaultValue) {
        if (TextUtils.isEmpty(intString)) {
            return defaultValue;
        }
        int value = defaultValue;
        try {
            value = Integer.parseInt(intString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static float parseFloat(String floatString, float defaultValue) {
        if (TextUtils.isEmpty(floatString)) {
            return defaultValue;
        }
        float value = defaultValue;
        try {
            value = Float.parseFloat(floatString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static double parseDouble(String doubleString, double defaultValue) {
        if (TextUtils.isEmpty(doubleString)) {
            return defaultValue;
        }
        double value = defaultValue;
        try {
            value = Double.parseDouble(doubleString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }



    public static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String formatInterval(final long intervalInMills) {
        final long hr = TimeUnit.MILLISECONDS.toHours(intervalInMills);
        final long min = TimeUnit.MILLISECONDS.toMinutes(intervalInMills - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(intervalInMills - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(intervalInMills - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
    }

    /**
     * 给url后面追加基本参数
     *
     * @param url
     * @return
     */


    /**
     * 限制价格输入
     *
     * @param s
     * @return true表示对Editable处理过了
     */
    public static boolean restrictPriceInput(Editable s) {
        String inputString = s.toString().trim();
        // 限制第一个数字不能输入0，只能输入0.
        if (inputString.startsWith("0") && inputString.length() >= 2) {
            if ('.' != inputString.charAt(1)) {
                s.delete(0, 1);
                return true;
            }
        }
        // 限制小数点后只能输入两位
        if (inputString.contains(".")
                && inputString.length() - 1 - inputString.indexOf(".") > 2) {
            s.delete(inputString.length() - 1, inputString.length());
            return true;
        }
        // 限制不能输入负数
        if (inputString.startsWith("-")) {
            s.delete(0, 1);
            return true;
        }
        return false;
    }

    /**
     * 限制只能输入正整数
     *
     * @param s
     * @return true表示对Editable处理过了
     */
    public static boolean restrictPositiveIntegerInput(Editable s) {
        String inputString = s.toString().trim();
        // 限制不能输入负数
        if (inputString.startsWith("-")) {
            s.delete(0, 1);
            return true;
        }
        // 限制第一个数字不能输入0
        if (inputString.startsWith("0")) {
            s.delete(0, 1);
            return true;
        }
        return false;
    }

    /**
     * 限制百分比的输入，只能输入xx.xx
     *
     * @param s
     * @return true表示对Editable处理过了
     */
    public static boolean restrictPercentInput(Editable s) {
        String inputString = s.toString().trim();
        // 限制不能输入负数
        if (inputString.startsWith("-")) {
            s.delete(0, 1);
            return true;
        }
        // 限制第一个数字不能输入0，只能输入0.
        if (inputString.startsWith("0") && inputString.length() >= 2) {
            if ('.' != inputString.charAt(1)) {
                s.delete(0, 1);
                return true;
            }
        }
        // 限制小数点前只能输入两位
        if (inputString.length() > 2
                && inputString.charAt(0) >= '1' && inputString.charAt(0) <= '9'
                && inputString.charAt(1) >= '0' && inputString.charAt(1) <= '9'
                && inputString.charAt(2) != '.') {
            s.delete(inputString.length() - 1, inputString.length());
            return true;
        }
        // 限制小数点后只能输入两位
        if (inputString.contains(".")
                && inputString.length() - 1 - inputString.indexOf(".") > 2) {
            s.delete(inputString.length() - 1, inputString.length());
            return true;
        }
        return false;
    }





    public static String getEx(Bitmap.CompressFormat ex) {
        String exd = null;
        if (ex == Bitmap.CompressFormat.WEBP) {
            exd = ".webp";
        } else if (ex == Bitmap.CompressFormat.PNG) {
            exd = ".png";
        } else if (ex == Bitmap.CompressFormat.JPEG) {
            exd = ".jpg";
        }

        return exd;
    }

    /**
     * 获取屏幕尺寸与密度.
     *
     * @param context the context
     * @return mDisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources mResources;
        if (context == null) {
            mResources = Resources.getSystem();

        } else {
            mResources = context.getResources();
        }
        //DisplayMetrics{density=1.5, width=480, height=854, scaledDensity=1.5, xdpi=160.421, ydpi=159.497}
        //DisplayMetrics{density=2.0, width=720, height=1280, scaledDensity=2.0, xdpi=160.42105, ydpi=160.15764}
        DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
        return mDisplayMetrics;
    }




    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] arr = baos.toByteArray();
        String result = Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }

    public static View getItemViewByPosition(ListView listView, int position) {
        position += listView.getHeaderViewsCount();
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
        if (position >= firstListItemPosition && position <= lastListItemPosition) {
            return listView.getChildAt(position - firstListItemPosition);
        } else {
            return null;
        }
    }

    //0(已取消)10(默认):未付款认;20:已付款(待接单);30:已发货(待发货);35：配送中(配送订单才有)40:已收货/o2o已完成;
    public static String getReadableOrderState(String order_state) {
        HashMap<String, String> map = new HashMap<>();
        map.put("0", "已取消");
        map.put("10", "默认");
        map.put("20", "未付款");
        map.put("30", "已发货");
        map.put("35", "配送中");
        map.put("40", "已收货");
        return map.get(order_state);
    }

    /**
     * ASCII表中可见字符从!开始，偏移位值为33(Decimal)
     */
    static final char DBC_CHAR_START = 33; // 半角!
    /**
     * ASCII表中可见字符到~结束，偏移位值为126(Decimal)
     */
    static final char DBC_CHAR_END = 126; // 半角~
    /**
     * ASCII表中除空格外的可见字符与对应的全角字符的相对偏移
     */
    static final int CONVERT_STEP = 65248; // 全角半角转换间隔
    /**
     * 全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理
     */
    static final char SBC_SPACE = 12288; // 全角空格 12288
    /**
     * 半角空格的值，在ASCII中为32(Decimal)
     */
    static final char DBC_SPACE = ' '; // 半角空格

    /**
     * <PRE>
     * 半角字符->全角字符转换
     * 只处理空格，!到˜之间的字符，忽略其他
     * </PRE>
     */
    public static String half2full(String src) {
        try {
            if (src == null) {
                return src;
            }
            StringBuilder buf = new StringBuilder(src.length());
            char[] ca = src.toCharArray();
            for (int i = 0; i < ca.length; i++) {
                if (ca[i] == DBC_SPACE) { // 如果是半角空格，直接用全角空格替代
                    buf.append(SBC_SPACE);
                } else if ((ca[i] >= DBC_CHAR_START) && (ca[i] <= DBC_CHAR_END)) { // 字符是!到~之间的可见字符
                    buf.append((char) (ca[i] + CONVERT_STEP));
                } else { // 不对空格以及ascii表中其他可见字符之外的字符做任何处理
                    buf.append(ca[i]);
                }
            }
            return ToDBC(buf.toString());
        } catch (Exception e) {
            return src;
        }
    }

    public static String ToSBC(String input)
    {
      /*  //半角转全角：
        char[] c=input.toCharArray();
        for (int i = 0; i < c.length; i++)
        {
            if (c[i]==32)
            {
                c[i]=(char)12288;
                continue;
            }
            if (c[i]<127)
                c[i]=(char)(c[i]+65248);
        }
        return new String(c);*/

        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);


    }


    /**
     * 全角转半角(只转数字)
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String ToDBC(String input) {

        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);

        return returnString;
    }


    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分"）
     *
     * @param time
     * @return
     */
    public static String DatetimeChange(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String times = "";
        if (!TextUtils.isEmpty(time)) {
            times = sdr.format(new Date(Long.valueOf(time) * 1000));
        }
        return times;
    }

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分"）
     *
     * @param time
     * @return
     */
    public static String dateLongToSimpleString(long time, String formart) {
        SimpleDateFormat sdr = new SimpleDateFormat(formart);
        String times = "";
        if (time > 0) {
            times = sdr.format(new Date(Long.valueOf(time) * 1000));
        }
        return times;
    }

    public static String getCurrentDate(String formart) {
        SimpleDateFormat sdr = new SimpleDateFormat(formart);
        String times = "";
        times = sdr.format(new Date(Long.valueOf(System.currentTimeMillis())));
        return times;
    }

    /**
     * @param time    2016.06.06
     * @param formart
     * @return 秒
     */
    public static long getSimpleDateLongValue(String time, String formart) {
        SimpleDateFormat sdr = new SimpleDateFormat(formart);
        long datetime = 0;
        if (!TextUtils.isEmpty(time)) {
            try {
                Date date = sdr.parse(time);
                datetime = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return datetime / 1000;
    }

    /**
     * 获取目标时间是今天 昨天。。
     *
     * @return
     */
    public static String getChineseTime(long time) {
        if (System.currentTimeMillis() - time * 1000 < 24 * 60 * 60 * 1000) {
            Date cur_date = new Date();
            Date tmp_date = new Date(time * 1000);
            if (cur_date.getHours() >= tmp_date.getHours()) {
                return dateLongToSimpleString(time, "HH:mm");
            }
        }
        return dateLongToSimpleString(time, "yyyy-MM-dd HH:mm");
    }

    public static String getExactTime(long time) {
        long intervalTime = System.currentTimeMillis() - time * 1000;
        if (intervalTime < 60 * 1000) {
            return "1分钟内";
        } else if (intervalTime < 60 * 60 * 1000) {
            return intervalTime / 60 / 1000 + "分钟之前";
        } else if (intervalTime < 60 * 60 * 24 * 1000) {
            double hourTime = intervalTime / (60 * 60 * 1000);
            return hourTime + "小时之前";
        } else if (intervalTime < (long) (30L * 24L * 60L * 60L * 1000L)) {
            return intervalTime / 60 / 60 / 24 / 1000 + "天前";
        } else if (intervalTime < (long) (30L * 24L * 60L * 60L * 12L * 1000L)) {
            return "1年内";
        } else {
            return getChineseTime(time);
        }

    }




    public static boolean saveImageToGallery(Context context, String bitName, Bitmap bmp) {
        boolean save_success = true;
        File appDir = new File(Environment.getExternalStorageDirectory(), THIS_DIR);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = bitName + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            save_success = false;
        }
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Uri data = Uri.parse("file://" + file.getAbsolutePath());
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
        return save_success;
    }


    /**
     *  文字中间添加横线
     */
    public static SpannableString getSpannableString(String spannableString) {
        SpannableString sp = new SpannableString(spannableString);
        sp.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }

    public  static Boolean stringIsEmpty(String string){

        if(string!=null && !string.isEmpty()){
            return false;
        }
        return true;
    }

    public static void logJson(String responseBody,String url) {
        StringBuilder logSb = new StringBuilder();
        logSb.append("POST: curl \"" + url);
        logSb.append("\n");
       json(getLogTag(), 3, logSb.toString(), responseBody);
    }

    public static void json(String tag, int methodCount, String contentHeader, String json) {
        Log.i(tag,contentHeader+json);
        Logger.t(tag).json(json);
    }


}
