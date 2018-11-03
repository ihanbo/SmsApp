package com.androstock.smsapp;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class DisponsablePresenter {
    private CompositeDisposable mDisposables = new CompositeDisposable();

    public void addDisponsable(Disposable... disposables) {
        mDisposables.addAll(disposables);
    }

    public void clearDisponsables() {
        mDisposables.clear();
    }
}

