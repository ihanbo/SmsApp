package com.androstock.wx.sms;

import android.util.Log;

import com.androstock.smsapp.Function;
import com.androstock.smsapp.SPUtils;
import com.androstock.smsapp.SmsModel;
import com.androstock.wx.WxStore;

/**
 * @Author hanbo
 * @Since 2018/11/3
 */
public class SmsProcesser {


    public static void processSms(SmsModel data) {
        if (data == null) {
            return;
        }
        if (data.msg.startsWith("hb")) {
            try {
                handCommond(data.msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (SPUtils.getInstance().getBoolean("opensms", false)) {
            sendDefaultSms(data);
        }
        sendWxMsg(data);
    }

    private static void sendWxMsg(SmsModel data) {
        WxStore.sendMsg("消息：" + data.fromPhone + ": " + data.msg);
    }

    private static void sendDefaultSms(SmsModel data) {
        try {
            Log.i("hh", "SmsProcesser  : sendDefaultSms: 发消息了");
            Function.sendSMS("17600360026", "消息：" + data.fromPhone + ":" + data.msg);
        } catch (Exception e) {
            Log.i("hh", "SmsProcesser  : sendDefaultSms: error" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handCommond(String msg) {
        String[] split = msg.split(",");
        String cmd = split[1];
        Log.i("hh", "SmsProcesser  : handCommond: " + msg);
        switch (cmd) {
            case "opensms":
                Log.i("hh", "SmsProcesser  : handCommond: 开启短信");
                SPUtils.getInstance().put("opensms", true);
                break;
            case "closesms":
                Log.i("hh", "SmsProcesser  : handCommond: 关闭短信");
                SPUtils.getInstance().put("opensms", false);
                break;
        }
    }
}
