package com.androstock.retrofit2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 包装rxjava的适配器.做一下拦截处理
 * Created by hanbo on 2018/3/12.
 */

public class MyRXCallAdapter extends CallAdapter.Factory {

    RxJava2CallAdapterFactory factory;

    MyRXCallAdapter(RxJava2CallAdapterFactory rxJava2CallAdapterFactory) {
        factory = rxJava2CallAdapterFactory;
    }

    public static MyRXCallAdapter create() {
        return new MyRXCallAdapter(RxJava2CallAdapterFactory.create());
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        CallAdapter<?, ?> callAdapter = factory.get(returnType, annotations, retrofit);
        if (callAdapter != null) {
            return new RXC(callAdapter);
        } else {
            return null;
        }

    }


    public static class RXC implements CallAdapter {

        CallAdapter<?, ?> callAdapter;

        public RXC(CallAdapter<?, ?> callAdapter) {
            this.callAdapter = callAdapter;
        }

        @Override
        public Type responseType() {
            return callAdapter.responseType();
        }

        @Override
        public Object adapt(Call call) {
            Object o = callAdapter.adapt(call);

            if (o instanceof Observable) {
                return new FatherObservable((Observable) o, call);
            }
            return o;
        }
    }
}