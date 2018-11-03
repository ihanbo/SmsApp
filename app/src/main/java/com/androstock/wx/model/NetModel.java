package com.androstock.wx.model;

public class NetModel {


    /**
     * errcode : 40091
     * errmsg : secret is invalid
     */

    public int errcode;
    public String errmsg;


    @Override
    public String toString() {
        return "NetModel{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }
}