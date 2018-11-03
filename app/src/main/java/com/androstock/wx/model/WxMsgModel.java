package com.androstock.wx.model;

/**
 * @author hanbo
 * @date 2018/11/2
 */
public class WxMsgModel extends NetModel {


    /**
     * invaliduser : userid1|userid2
     * invalidparty : partyid1|partyid2
     * invalidtag : tagid1|tagid2
     */

    private String invaliduser;
    private String invalidparty;
    private String invalidtag;

    public String getInvaliduser() {
        return invaliduser;
    }

    public void setInvaliduser(String invaliduser) {
        this.invaliduser = invaliduser;
    }

    public String getInvalidparty() {
        return invalidparty;
    }

    public void setInvalidparty(String invalidparty) {
        this.invalidparty = invalidparty;
    }

    public String getInvalidtag() {
        return invalidtag;
    }

    public void setInvalidtag(String invalidtag) {
        this.invalidtag = invalidtag;
    }


    @Override
    public String toString() {
        return "WxMsgModel{" +
                "invaliduser='" + invaliduser + '\'' +
                ", invalidparty='" + invalidparty + '\'' +
                ", invalidtag='" + invalidtag + '\'' +
                ", errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }
}
