package com.androstock.smsapp;

/**
 * @Author hanbo
 * @Since 2018/11/3
 */
public class SmsBody {

    public String _id;
    public String thread_id;
    public String name;
    public String phone;
    public String msg;
    public String type;
    public String timestamp;
    public String time;

    public SmsBody(String _id, String thread_id, String name, String phone, String msg, String type, String timestamp, String time) {
        this._id = _id;
        this.thread_id = thread_id;
        this.name = name;
        this.phone = phone;
        this.msg = msg;
        this.type = type;
        this.timestamp = timestamp;
        this.time = time;
    }
}
