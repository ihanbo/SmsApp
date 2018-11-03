package com.androstock.retrofit2.error;

/**
 * @author hanbo
 * @date 2018/11/2
 */
public interface NetError {
    int DEFAULT_HTTP_STATUS_CODE = -1000;

    String getJsonString();

    int httpStatusCode();


}
