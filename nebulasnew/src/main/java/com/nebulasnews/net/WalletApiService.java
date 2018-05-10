package com.nebulasnews.net;

import com.nebulas.io.net.model.TestContractRequest;
import com.nebulasnews.news.AdListResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WalletApiService {

    @POST("/v1/user/call")
    Call<AdListResult> getAdList(@Body TestContractRequest contractRequest);

}
