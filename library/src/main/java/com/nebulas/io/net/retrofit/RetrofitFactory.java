package com.nebulas.io.net.retrofit;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;


/**
 * Created by carpediem on 2017/1/1.
 */

class RetrofitFactory {

    public static Retrofit getInstance(final String baseUrl) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder requestBuilder = chain.request().newBuilder();
                        requestBuilder.header("Content-Type", "application/json");
                        Response response = chain.proceed(requestBuilder.build());
                        return response;
                    }
                })
                .connectTimeout(60 * 5, TimeUnit.SECONDS)
                .build();


        return new Retrofit.Builder().
                baseUrl(baseUrl)
                .callFactory(okHttpClient)
                .addConverterFactory(new Converter.Factory() {
                    @Override
                    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                        Gson gson = new Gson();
                        return new GsonResponseConverter<>(gson, type);
                    }

                    @Override
                    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
                        Gson gson = new Gson();
                        return new GsonRequestBodyConverter(gson, gson.getAdapter(TypeToken.get(type)));
                    }
                })
                .build();
    }


}
