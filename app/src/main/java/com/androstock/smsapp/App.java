package com.androstock.smsapp;

import android.app.Application;

import com.androstock.wx.WxStore;

/**
 * @Author hanbo
 * @Since 2018/11/3
 */
public class App extends Application {

    public static App mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        WxStore.init(this);
        SPUtils.getInstance();
    }
}
