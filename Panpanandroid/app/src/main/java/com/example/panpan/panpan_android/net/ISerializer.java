package com.example.panpan.panpan_android.net;

import java.lang.reflect.Type;
import java.util.Map;

public interface ISerializer {
    
    String toJson(Map object);
    
    String toJson(Object object);
    
    Object fromJson(String json, Class<?> cls);
    
    Object fromJson(String json, Type type);
}
