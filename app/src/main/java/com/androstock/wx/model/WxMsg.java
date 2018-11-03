package com.androstock.wx.model;

/**
 * @author hanbo
 * @date 2018/11/2
 */
public class WxMsg {


    /**
     * touser : UserID1|UserID2|UserID3
     * toparty : PartyID1|PartyID2
     * totag : TagID1 | TagID2
     * msgtype : text
     * agentid : 1
     * text : {"content":"你的快递已到，请携带工卡前往邮件中心领取。\n出发前可查看<a href=\"http://work.weixin.qq.com\">邮件中心视频实况<\/a>，聪明避开排队。"}
     * safe : 0
     */

    public String touser = "ihanbo";
    public String toparty;
    public String totag;
    public String msgtype = "text";
    public int agentid = 0;
    public TextBean text;
    public int safe = 0;


    public WxMsg() {
    }

    public WxMsg(String text) {
        this.text = new TextBean(text);
    }


    public static class TextBean {
        /**
         * content : 你的快递已到，请携带工卡前往邮件中心领取。
         * 出发前可查看<a href="http://work.weixin.qq.com">邮件中心视频实况</a>，聪明避开排队。
         */

        private String content;

        public TextBean(String content) {
            this.content = content;
        }

        public TextBean() {
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
