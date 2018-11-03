package com.androstock.wx;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author hanbo
 * @date 2018/11/2
 */
public class RxUtil {
    /**
     * Description: 获取默认ObservableTransformer
     * author: Lyongwang
     * date: 2017/5/17 下午4:32
     *
     * @return subscribeOn ioThread  observeOn mainThread
     */
    public static <T> ObservableTransformer<T, T> getTransformer() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> getTransformer2() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.newThread())
                        .unsubscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}