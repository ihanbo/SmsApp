package com.androstock.smsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * @author hanbo
 * @date 2018/11/1
 */
public class MmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "收到消息2", Toast.LENGTH_SHORT).show();
    }
}
