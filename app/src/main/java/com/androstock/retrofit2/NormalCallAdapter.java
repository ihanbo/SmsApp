package com.androstock.retrofit2;

import com.androstock.wx.model.NetModel;
import com.androstock.wx.model.TokenModel;
import com.androstock.wx.model.WxMsgModel;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 用于解析通用model
 * Created by hanbo on 2018/3/12.
 */

public class NormalCallAdapter extends CallAdapter.Factory {


    public static NormalCallAdapter create() {
        return new NormalCallAdapter();
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);
        if (rawType == TokenModel.class || rawType == WxMsgModel.class || rawType == NetModel.class) {
            return new CA<>(returnType, retrofit);
        }
        return null;
    }


    public static class CA<T> implements CallAdapter<T, T> {

        Type responseType;
        Retrofit retrofit;

        public CA(Type responseType, Retrofit retrofit) {
            this.retrofit = retrofit;
            this.responseType = responseType;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public T adapt(Call<T> call) {
            Response<T> response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                try {
                    SpecialParser.interceptorError(call, e);
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
                throw new NetRuntimeError(e);
            }

            if (!response.isSuccessful()) {
                NetRuntimeError netRuntimeError = new NetRuntimeError("!response.isSuccessful()")
                        .setHttpStatusCode(response.code());
                try {
                    SpecialParser.interceptorError(call, netRuntimeError);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                throw netRuntimeError;
            }

            T body = response.body();
            try {
                SpecialParser.interceptorSucc(call, body);
            } catch (Throwable e) {
                NetRuntimeError netRuntimeError = new NetRuntimeError("SpecialParser.interceptorSucc cause error", e)
                        .setHttpStatusCode(response.code());
                throw netRuntimeError;
            }
            return response.body();
        }


    }


}