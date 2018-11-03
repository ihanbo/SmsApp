package com.androstock.wx;


import com.androstock.wx.model.TokenModel;
import com.androstock.wx.model.WxMsgModel;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

interface WxRemoteService {


    @GET("cgi-bin/gettoken")
    TokenModel getToken(@Query("corpid") String corpid, @Query("corpsecret") String secret);

    @GET("cgi-bin/gettoken")
    Call<TokenModel> getToken2(@Query("corpid") String corpid, @Query("corpsecret") String secret);

    @GET("cgi-bin/gettoken")
    Observable<TokenModel> getToken3(@Query("corpid") String corpid, @Query("corpsecret") String secret);


    @POST
    WxMsgModel sendMsg(@Url String url, @Body RequestBody body);

    @POST
    Observable<WxMsgModel> sendMsg2(@Url String url, @Body RequestBody body);

}
