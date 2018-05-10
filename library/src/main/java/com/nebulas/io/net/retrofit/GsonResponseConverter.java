/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nebulas.io.net.retrofit;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.internal.$Gson$Types;
import com.nebulas.io.util.LogUtils;

import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseConverter<T> implements Converter<ResponseBody, T> {
    private static final String TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)";
    private static final String QUOTED = "\"([^\"]*)\"";
    private static final Pattern TYPE_SUBTYPE = Pattern.compile(TOKEN + "/" + TOKEN);
    private static final Pattern PARAMETER = Pattern.compile(
            ";\\s*(?:" + TOKEN + "=(?:" + TOKEN + "|" + QUOTED + "))?");
    /**
     * A cheap and type-safe constant for the UTF-8 Charset.
     */
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final Gson gson;

    private final Type type;

    GsonResponseConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        Class cls = getConvertDataClass(type);
        String result = value.string();
        LogUtils.d(result);
        JsonElement rootElement = new JsonParser().parse(result);
        if (cls != null) {
            try {
                Method convertMethod = cls.getDeclaredMethod("convertData", JsonElement.class);
                return (T) convertMethod.invoke(cls.newInstance(), rootElement);
            } catch (Exception e) {
                e.printStackTrace();
                IOException ioe = new IOException("ConvertData invoke exception");
                ioe.initCause(e);
                throw ioe;
            }
        }
        return convert(rootElement);
    }

    private Class getConvertDataClass(Type type) {
        Class<?> cls = $Gson$Types.getRawType(type);
        //要对list等类型进行判断
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            if (types.length > 0) {
                for (Type tmpType : types) {
                    Class tmpClass = $Gson$Types.getRawType(tmpType);
                    if (GsonConverter.class.isAssignableFrom(tmpClass)) {
                        return tmpClass;
                    }
                }
            }
        }
        if (GsonConverter.class.isAssignableFrom(cls)) {
            return cls;
        }
        return null;
    }

    public T convert(JsonElement rootElement) throws IOException {
        if (!rootElement.isJsonObject()) {
            IOException ioe = new IOException("Parse exception converting JSON to object");
            ioe.initCause(new JsonParseException("Root is not JsonObject"));
            throw ioe;
        } else {
            JsonObject root = rootElement.getAsJsonObject();
            return this.convertDataElement(root);
        }
    }

    protected void convertErrorElement(JsonElement error) throws HttpResponseException {
        if (error.isJsonObject()) {
            JsonObject errorObject = error.getAsJsonObject();
            int code = errorObject.has("code") ? errorObject.get("code").getAsInt() : 400;
            String message = errorObject.has("message") ? errorObject.get("message").getAsString() : "";
            throw new HttpResponseException(code, message);
        }
    }

    protected T convertDataElement(JsonElement data) {
        return gson.fromJson(data.getAsJsonObject().get("result"), type);
    }
}
