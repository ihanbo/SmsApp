package com.androstock.wx.phone;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.androstock.wx.BackService;

/**
 * @Author hanbo
 * @Since 2018/11/3
 */
public class PhoneReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("hh", "收到电话广播:{}" + intent == null ? "null" : intent.getAction());

        // 如果是拨打电话
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            //拨打电话会优先,收到此广播. 再收到 android.intent.action.PHONE_STATE 的 TelephonyManager.CALL_STATE_OFFHOOK 状态广播;

            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.i("hh", "拨打电话了：号码：" + phoneNumber);//获取拨打的手机号码
        } else {
            // 如果是来电
            TelephonyManager tManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            //电话的状态
            switch (tManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //等待接听状态
                    String incomingNumber = intent.getStringExtra("incoming_number");
                    Intent intent1 = new Intent(context, BackService.class);
                    intent1.setAction("handle_phone");
                    intent1.putExtra("data", incomingNumber);
                    context.startService(intent1);
                    Log.i("hh", "来电话了 :" + incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //接听状态
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //挂断状态
                    break;
            }
        }
    }

}
