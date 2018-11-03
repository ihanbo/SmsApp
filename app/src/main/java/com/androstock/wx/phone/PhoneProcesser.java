package com.androstock.wx.phone;

import android.util.Log;

import com.androstock.wx.WxStore;

/**
 * @Author hanbo
 * @Since 2018/11/3
 */
public class PhoneProcesser {
    public static void prosess(String phone) {
        Log.i("hh", "PhoneProcesser  : prosess: " + phone);

        if (phone == null) {
            return;
        }
        WxStore.sendMsg("未接来电：" + phone);
    }
}
