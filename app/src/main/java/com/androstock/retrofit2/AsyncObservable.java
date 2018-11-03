package com.androstock.retrofit2;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

/**
 * 哈哈哈
 * Created by hanbo on 2018/3/18.
 */

public class AsyncObservable<T> extends Observable<T> {
    private static final int DEFAULT_TIME_OUT = 40;
    private final Async2Rx<T> originalCall;

    AsyncObservable(Async2Rx<T> originalCall) {
        this.originalCall = originalCall;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        CallCallback<T> callback = new CallCallback(originalCall, observer);
        observer.onSubscribe(callback);
        originalCall.start(callback);
    }

    private static final class CallCallback<T> implements Disposable, Async2Rx.AsyncCallBack<T> {
        private final Observer<? super T> observer;
        private Async2Rx<T> originalCall;
        boolean terminated = false;
        private boolean mIsDisposed = false;

        CallCallback(Async2Rx<T> originalCall, Observer<? super T> observer) {
            this.originalCall = originalCall;
            this.observer = observer;
        }

        @Override
        public void dispose() {
            mIsDisposed = true;
            this.originalCall.cancel();
        }

        @Override
        public boolean isDisposed() {
            return mIsDisposed;
        }

        @Override
        public void onSucc(T t) {
            if (!isDisposed()) {
                try {
                    this.observer.onNext(t);
                    if (!isDisposed()) {
                        this.terminated = true;
                        this.observer.onComplete();
                    }
                } catch (Throwable var6) {
                    if (this.terminated) {
                        //onNext结束后onComplete()方法出错
                        RxJavaPlugins.onError(var6);
                    } else if (!isDisposed()) {
                        try {
                            this.observer.onError(var6);
                        } catch (Throwable var5) {
                            Exceptions.throwIfFatal(var5);
                            RxJavaPlugins.onError(new CompositeException(new Throwable[]{var6, var5}));
                        }
                    }
                }

            }
        }

        @Override
        public void onError(Throwable throwable) {
            if (!isDisposed()) {
                try {
                    this.observer.onError(throwable);
                } catch (Throwable var4) {
                    Exceptions.throwIfFatal(var4);
                    RxJavaPlugins.onError(new CompositeException(new Throwable[]{throwable, var4}));
                }

            }
        }
    }

    public static Observable create(Async2Rx originalCall){
        return new AsyncObservable(originalCall).observeOn(Schedulers.newThread());
    }
    public static Observable create(Async2Rx originalCall,int timeout){
        return new AsyncObservable(originalCall).timeout(timeout, TimeUnit.SECONDS).observeOn(Schedulers.newThread());

    }
}
