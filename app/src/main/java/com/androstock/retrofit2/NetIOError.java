package com.androstock.retrofit2;


import com.androstock.retrofit2.error.NetError;

import java.io.IOException;

/**
 * @author hanbo
 * @date 2018/11/1
 */
class NetIOError extends IOException implements NetError {


    private String jsonString;

    public NetIOError(Throwable cause) {
        super(cause);
    }

    public NetIOError(String message, Throwable cause) {
        super(message, cause);
    }

    public NetIOError(Throwable cause, String jsonString) {
        super(cause);
        this.jsonString = jsonString;
    }


    public NetIOError setJsonString(String jsonString) {
        this.jsonString = jsonString;
        return this;
    }

    @Override
    public String getJsonString() {
        return jsonString;
    }

    @Override
    public int httpStatusCode() {
        return DEFAULT_HTTP_STATUS_CODE;
    }

}
