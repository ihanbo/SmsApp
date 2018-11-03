package com.androstock.retrofit2;

/**
 * 将异步任务转为RX的Observable
 * @param <T>
 */
public interface Async2Rx<T>{
    void start(AsyncCallBack<T> callback);
    void cancel();


    interface AsyncCallBack<T> {
        void onSucc(T t);
        void onError(Throwable throwable);
    }
}
