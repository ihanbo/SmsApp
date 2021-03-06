package com.androstock.wx;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.androstock.retrofit2.Net;
import com.androstock.wx.model.TokenModel;
import com.androstock.wx.model.WxMsg;
import com.androstock.wx.model.WxMsgModel;
import com.google.gson.Gson;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.androstock.wx.WxSecret.*;

public class WxStore {

    private static final int EXPIRE_CODE = 42001;   //TOKEN过期


    private static final String BASEURL = "https://qyapi.weixin.qq.com/";


    private static WxRemoteService service;

    private static SharedPreferences sp;


    public static void init(Application application) {
        Net.init(BASEURL, 30);
        sp = application.getSharedPreferences("wxss", Context.MODE_PRIVATE);
        service = Net.getService(WxRemoteService.class);
        refreshToken();
    }

    //刷新token
    private static void refreshToken() {
        service.getToken3(corpid, secret)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<TokenModel>() {
                    @Override
                    public void accept(TokenModel tokenModel) throws Exception {
                        if (tokenModel.errcode == 0 && tokenModel.access_token != null) {
                            saveToken(tokenModel.access_token, tokenModel.expires_in);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("hh", "WxStore  : accept: " + throwable.getMessage());
                        throwable.printStackTrace();
                    }
                });
    }

    //保存token
    private static void saveToken(String token, long expire) {
        if (TextUtils.isEmpty(token)) {
            return;
        }
        sp.edit().putString(TOKEN, token)
                .putLong(TIME, System.currentTimeMillis()).commit();
    }


    //先获取本地token，为空再获取远程token
    private static Observable<String> getToken() {
        return getLocalToken().onErrorResumeNext(getRemoteToken());
    }


    //获取本地token
    private static Observable<String> getLocalToken() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String string = sp.getString(TOKEN, null);
                long cacheTime = sp.getLong(TIME, -1);
                long now = System.currentTimeMillis();
                long dis = (now - cacheTime) / 1000;
                boolean isvalid = dis > 7200;
                Log.i("hh", "A  : 间隔: dis: " + dis);
                if (TextUtils.isEmpty(string)) {
                    e.onNext(null);
                } else {
                    e.onNext(string);
                }
            }
        });
    }

    //获取远程token
    private static Observable<String> getRemoteToken() {
        return service.getToken3(corpid, secret).doOnNext(new Consumer<TokenModel>() {
            @Override
            public void accept(TokenModel tokenModel) throws Exception {
                saveToken(tokenModel.access_token, tokenModel.expires_in);
            }
        }).map(new Function<TokenModel, String>() {
            @Override
            public String apply(TokenModel tokenModel) throws Exception {
                return tokenModel.access_token;
            }
        });
    }


    //发送消息，不需要处理
    public static void sendMsg(final String msg) {
        sendMsgNeedHandle(msg).subscribe(new Observer<WxMsgModel>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i("hh", "WxStore  : onSubscribe: ");
            }

            @Override
            public void onNext(WxMsgModel wxMsgModel) {
                Log.i("hh", "WxStore  : onNext: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.i("hh", "WxStore  : onError: ");
            }

            @Override
            public void onComplete() {
                Log.i("hh", "WxStore  : onComplete: ");
            }
        });
    }

    //发送消息
    private static Observable<WxMsgModel> sendMsgNeedHandle(final String msg) {
        return getToken().flatMap(new Function<String, ObservableSource<WxMsgModel>>() {
            @Override
            public ObservableSource<WxMsgModel> apply(String s) throws Exception {
                return sendMsg(s, msg);
            }
        }).flatMap(new Function<WxMsgModel, ObservableSource<WxMsgModel>>() {
            @Override
            public ObservableSource<WxMsgModel> apply(WxMsgModel wxMsgModel) throws Exception {
                if (wxMsgModel.errcode == 0) {
                    return Observable.just(wxMsgModel);
                } else if (wxMsgModel.errcode == EXPIRE_CODE) {
                    return getRemoteToken().flatMap(new Function<String, ObservableSource<WxMsgModel>>() {
                        @Override
                        public ObservableSource<WxMsgModel> apply(String s) throws Exception {
                            return sendMsg(s, msg);
                        }
                    });
                } else {
                    return Observable.error(new RuntimeException("Error   errcode: " + wxMsgModel.errcode + "   errormsg: " + wxMsgModel.errmsg));
                }
            }
        }).compose(RxUtil.<WxMsgModel>getTransformer());
    }

    //发送消息
    private static Observable<WxMsgModel> sendMsg(String token, String msg) {
        String url = BASEURL + "cgi-bin/message/send?access_token=" + token;
        String text = new Gson().toJson(new WxMsg(msg));
        RequestBody json = RequestBody.create(MediaType.parse("application/json"), text);
        return service.sendMsg2(url, json);
    }


    //检查网络
    public static void checkNet(final CallBack<String> callBack) {
        String url = WxRemoteService.CHECK_NET;
        service.checkNet(url).compose(RxUtil.<String>getTransformer2()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                if (callBack != null) {
                    callBack.onSucc(s);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (callBack != null) {
                    callBack.onFail(e);
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }


    public interface CallBack<T> {
        void onSucc(T t);

        void onFail(Throwable t);
    }


}
