package com.androstock.retrofit2;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.Call;

/**
 * 包装下RX的适配器 做了个切面的
 * Created by hanbo on 2018/3/12.
 */

public class FatherObservable<T> extends Observable<T> {

    private Observable<T> upstream;
    private Call call;

    public FatherObservable(Observable<T> upstream, Call call) {
        this.upstream = upstream;
        this.call = call;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        this.upstream.subscribe(new BodyObserver(observer, call));
    }

    private static class BodyObserver<R> implements Observer<R> {
        private final Observer<? super R> observer;
        private boolean terminated; //标记是在onNext里出错处理
        private Call call;

        BodyObserver(Observer<? super R> observer, Call call) {
            this.observer = observer;
            this.call = call;
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            this.observer.onSubscribe(disposable);
        }

        @Override
        public void onNext(R r) {
            try {
                SpecialParser.interceptorSucc(call, r);
                this.observer.onNext(r);
            } catch (Throwable e) {
                this.terminated = true;
                try {
                    this.observer.onError(new NetRuntimeError("SpecialParser.interceptorSucc cause error", e));
                } catch (Throwable var4) {
                    Exceptions.throwIfFatal(var4);
                    RxJavaPlugins.onError(var4);
                }
            }
        }


        @Override
        public void onComplete() {
            if (!this.terminated) {
                this.observer.onComplete();
            }

        }

        @Override
        public void onError(Throwable e) {
            if (!this.terminated) {
                try {//处理拦截
                    SpecialParser.interceptorError(call, e);
                } catch (Throwable es) {
                    es.printStackTrace();
                }

                this.observer.onError(e);
            } else {
                //正常不应该发生
                Throwable broken = new AssertionError("This should never happen! Report as a bug with the full stacktrace.");
                broken.initCause(e);
                RxJavaPlugins.onError(broken);
            }

        }

    }
}
