package com.androstock.retrofit2;

import java.lang.reflect.Type;

import retrofit2.Call;


/**
 * @author hanbo
 * @date 2018/11/1
 */
public class SpecialParser {


    public static <T> T parse(Class<T> rawType, Type type, String jsonString) throws Exception {
        return null;
    }

    public static <R> void interceptorError(Call<R> call, Throwable e) throws Throwable {

    }

    public static <T> void interceptorSucc(Call<T> call, T body) throws Throwable {

    }
}
