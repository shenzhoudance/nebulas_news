package com.nebulas.io.net.retrofit;

import com.google.gson.JsonElement;

import java.io.IOException;

/**
 * 在定义Bean的时候实现该接口，从而完成自定义的Gson解析流程，jsonElement包含完成的json结构.
 * <p>
 * 该类已被ProGuard,不可随意改名与移动.
 */
@NoProguard
public interface GsonConverter<T> {
    T convertData(JsonElement jsonElement) throws IOException;

}

