package com.androstock.wx.model;

public class TokenModel extends NetModel {


    /**
     * access_token : accesstoken000001
     * expires_in : 7200
     */

    public String access_token;
    public long expires_in = 7200;


    @Override
    public String toString() {
        return "TokenModel{" +
                "access_token='" + access_token + '\'' +
                ", expires_in=" + expires_in +
                '}';
    }
}
