package com.androstock.smsapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * @author hanbo
 * @date 2018/11/1
 */
public class HeadlessSmsSendService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
