package com.androstock.smsapp;

import java.io.Serializable;

/**
 * @Author hanbo
 * @Since 2018/11/3
 */
public class SmsModel implements Serializable {
    public String fromPhone;
    public String msg;

    public SmsModel(String fromPhone, String msg) {
        this.fromPhone = fromPhone;
        this.msg = msg;
    }
}
