package com.example.panpan.panpan_android.net;




public class SerializerFactory {
    
    private static ISerializer mInstance;
    
    public static ISerializer getInstance() {
        synchronized (SerializerFactory.class) {
            if (mInstance == null) {
                synchronized (SerializerFactory.class) {
                    mInstance = new GsonSerializer();
                }
            }
        }
        return mInstance;
    }
}
