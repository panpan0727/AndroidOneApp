package com.example.panpan.panpan_android.webapi.response;



import android.util.Log;

import com.example.panpan.panpan_android.entity.SimpleResult1;
import com.example.panpan.panpan_android.utils.Utility;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class JsonParser {

    public static JsonParser jsonParser;

    public static JsonParser getInstance() {
        if (jsonParser == null) {
            jsonParser = new JsonParser();
        }

        return jsonParser;
    }

    public <T extends SimpleResult1> T fromJson(byte[] response, Class<T> tClass) {
        return fromJson(Utility.getStrFromByte(response), tClass);
    }

    public <T extends SimpleResult1> T fromJson(String response, Class<T> tClass) {
        String jsonStr = response.replace("\"data\":[]", "\"data\":null");
        T obj = null;
        try {
            obj = Utility.getGson().fromJson(jsonStr, tClass);
        } catch (NumberFormatException e) {
           Log.i("JsonParser", e.toString() + " json = " + jsonStr);
        } catch (JsonSyntaxException e) {
            Log.i("JsonParser", e.toString() + " json = " + jsonStr);
        } catch (IllegalStateException e) {
            Log.i("JsonParser", e.toString() + " json = " + jsonStr);
        }

        // try to parse as SimpleResult
        if (obj == null) {

            SimpleResult1 result1 = null;
            try {
                result1 = Utility.getGson().fromJson(jsonStr, SimpleResult1.class);
            } catch (NumberFormatException e) {
                Log.i("JsonParser", e.toString() + " json = " + jsonStr);
            } catch (JsonSyntaxException e) {
                Log.i("JsonParser", e.toString() + " json = " + jsonStr);
            } catch (IllegalStateException e) {
                Log.i("JsonParser", e.toString() + " json = " + jsonStr);
            }
            Log.i("simpleresult = " ,result1.toString());
            if (result1 == null) {
                return null;
            }

            try {
                Constructor<T> constructor = tClass.getConstructor(new Class[0]);
                obj = constructor.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (obj != null) {
                obj.copy(result1);
            }
        }

        return obj;
    }

}
