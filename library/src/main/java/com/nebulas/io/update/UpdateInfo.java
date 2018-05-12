package com.nebulas.io.update;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.nebulas.io.net.retrofit.GsonConverter;
import com.nebulas.io.net.retrofit.NoProguard;

import java.io.IOException;

/**
 * Created by wuzhi on 14-3-15.
 */
@NoProguard
public class UpdateInfo  implements GsonConverter<UpdateInfo>{

    @SerializedName("url")
    private String apkUrl;
    @SerializedName("text")
    private String updateText;
    private int versionCode;

    public int getVersionCode() {
        return versionCode;
    }

    public UpdateInfo setVersionCode(int versionCode) {
        this.versionCode = versionCode;
        return this;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public UpdateInfo setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
        return this;
    }

    public String getUpdateText() {
        return updateText;
    }

    public UpdateInfo setUpdateText(String updateText) {
        this.updateText = updateText;
        return this;
    }

    @Override
    public UpdateInfo convertData(JsonElement jsonElement) throws IOException {
        return new Gson().fromJson(jsonElement, UpdateInfo.class);
    }
}
