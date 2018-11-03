package com.androstock.retrofit2;


import com.androstock.retrofit2.error.NetError;

/**
 * @author hanbo
 * @date 2018/11/2
 */
class NetRuntimeError extends RuntimeException implements NetError {

    private int httpStatusCode = DEFAULT_HTTP_STATUS_CODE;


    public NetRuntimeError(String msg) {
        super(msg);
    }

    public NetRuntimeError(Throwable cause) {
        super(cause);
    }

    public NetRuntimeError(String msg, Throwable cause) {
        super(msg, cause);
    }

    @Override
    public String getJsonString() {
        if (getCause() != null && getCause() instanceof NetError) {
            return ((NetError) getCause()).getJsonString();
        }
        return "not found jsonstring";
    }

    @Override
    public int httpStatusCode() {
        return httpStatusCode;
    }

    public NetRuntimeError setHttpStatusCode(int code) {
        httpStatusCode = code;
        return this;
    }
}
