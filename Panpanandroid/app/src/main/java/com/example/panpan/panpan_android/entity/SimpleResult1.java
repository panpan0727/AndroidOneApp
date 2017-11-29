package com.example.panpan.panpan_android.entity;

import java.io.Serializable;

public class SimpleResult1 implements Serializable {
    private static final int STATUS_OK = 200;
    public static final int STATUS_AUTH_FAIL = 80001;

    private String status;
    private String message;

    public SimpleResult1() {
    }



    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "SimpleResult1{" +
                        "status=" + status +
                        ", message='" + message + '\'' +
                        '}';
    }

    public void copy(SimpleResult1 result1) {
        if (result1 == null) {
            return;
        }

        status = result1.status;
        message = result1.message;
    }
}
