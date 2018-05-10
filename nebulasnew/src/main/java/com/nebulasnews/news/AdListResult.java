package com.nebulasnews.news;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.nebulas.io.net.retrofit.GsonConverter;
import com.nebulas.io.net.retrofit.NoProguard;
import com.nebulas.io.util.Base64;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@NoProguard
public class AdListResult  implements GsonConverter<List<Ad>>{

    public String result;
    public String execute_err;
    public String estimate_gas;

    @Override
    public List<Ad> convertData(JsonElement jsonElement) throws IOException {
        String result = jsonElement.getAsJsonObject().get("result").getAsJsonObject().get("result").getAsString();
        List<Ad> ads = new ArrayList<>();
        if (result != null) {
            for (String item : result.split("----------------------")) {
                Ad ad = null;
                AdContainer container = null;
                try {
                    try {
                        container = new Gson().fromJson(item.replace("\\", "").substring(1), AdContainer.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        container = new Gson().fromJson(item.replace("\\", ""), AdContainer.class);
                    }
                    ad = new Gson().fromJson(new String(Base64.getDecoder().decode(container.data),"utf-8"), Ad.class);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ad != null) {
                    ads.add(ad);
                }
            }
        }
        return ads;
    }
}
