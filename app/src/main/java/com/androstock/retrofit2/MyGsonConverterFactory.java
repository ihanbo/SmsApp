package com.androstock.retrofit2;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by hanbo on 2018/3/12.
 */

public class MyGsonConverterFactory extends Converter.Factory {
    public static MyGsonConverterFactory create() {
        return new MyGsonConverterFactory(new Gson());
    }


    private final Gson gson;

    private MyGsonConverterFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new YCGsonResponseBodyConverter<>(gson, adapter, type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new YCGsonRequestBodyConverter<>(gson, adapter);
    }


    //请求的转换器
    static final class YCGsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
        private static final Charset UTF_8 = Charset.forName("UTF-8");

        private final Gson gson;
        private final TypeAdapter<T> adapter;

        YCGsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, value);
            jsonWriter.close();
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }


    //响应的转换器
    static final class YCGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final TypeAdapter<T> adapter;
        Type mType;

        YCGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter, Type type) {
            this.gson = gson;
            this.adapter = adapter;
            mType = type;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            return getResponseBody(mType, value);
        }

        //解析
        private T getResponseBody(Type type, ResponseBody value) throws IOException {
            Class<?> rawType = getRawType(type);
            String jsonString = null;   //原始string，可能有可能没有，尽量获取了
            try {
                //基础的
                if (rawType == String.class) {
                    jsonString = value.string();
                    return (T) jsonString;
                } else if (rawType == byte[].class) {   //字节流
                    return (T) value.bytes();
                } else if (rawType == JSONObject.class) {
                    jsonString = value.string();
                    return (T) new JSONObject(jsonString);
                } else if (rawType == JSONArray.class) {
                    jsonString = value.string();
                    return (T) new JSONArray(jsonString);
                }

                T t = (T) SpecialParser.parse(rawType, type, jsonString);
                if (t != null) {
                    return t;
                } else {    //默认的解析
                    JsonReader jsonReader = gson.newJsonReader(value.charStream());
                    try {
                        return adapter.read(jsonReader);
                    } finally {
                        value.close();
                    }
                }
            } catch (JSONException e) {
                throw new NetIOError(e).setJsonString(jsonString);
            } catch (IOException e) {
                throw new NetIOError(e).setJsonString(jsonString);
            } catch (Exception e) {
                throw new NetIOError(e).setJsonString(jsonString);
            }
        }
    }


}
