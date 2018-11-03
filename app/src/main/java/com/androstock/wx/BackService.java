package com.androstock.wx;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.androstock.smsapp.SmsModel;
import com.androstock.wx.phone.PhoneProcesser;
import com.androstock.wx.sms.SmsProcesser;

/**
 * @Author hanbo
 * @Since 2018/11/3
 */
public class BackService extends android.app.IntentService {

    public BackService() {
        super("smsservice");
    }

    public BackService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        switch (intent.getAction()) {
            case "handle_sms":
                SmsModel data = (SmsModel) intent.getSerializableExtra("data");
                SmsProcesser.processSms(data);
                break;
            case "handle_phone":
                String phone = intent.getStringExtra("data");
                PhoneProcesser.prosess(phone);
                break;
        }
    }


}
