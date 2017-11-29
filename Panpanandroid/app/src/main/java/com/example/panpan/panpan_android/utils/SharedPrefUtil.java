package com.example.panpan.panpan_android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences存取工具类
 */
public class SharedPrefUtil {
    
    private final static String XML_NAME = "Panpan";
    
    
    public static void saveState(Context context, String key, Object value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        editor.commit();
    }
    
    public static <T> T getState(Context context, Class<T> type, String key) {
        Object value = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        if (type.getSimpleName().equals(Boolean.class.getSimpleName())) {
            value = sharedPreferences.getBoolean(key, false);
        } else if (type.getSimpleName().equals(Integer.class.getSimpleName())) {
            value = sharedPreferences.getInt(key, -1);
        } else if (type.getSimpleName().equals(String.class.getSimpleName())) {
            value = sharedPreferences.getString(key, "");
        } else if (type.getSimpleName().equals(Long.class.getSimpleName())) {
            value = sharedPreferences.getLong(key, -1);
        } else if (type.getSimpleName().equals(Boolean.class.getSimpleName())) {
            value = sharedPreferences.getBoolean(key, false);
        }
        return (T) value;
    }
    
    public static void remove(Context context, String key) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
