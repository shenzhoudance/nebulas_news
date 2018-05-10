package com.nebulasnews.net;

import com.nebulas.io.net.model.ContractRequest;
import com.nebulas.io.net.model.TestContractRequest;
import com.nebulas.io.net.retrofit.NebulasRetrofitService;
import com.nebulasnews.news.AdListResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by carpediem on 2017/1/3.
 */

public class WalletRetrofitService {

    private static WalletRetrofitService walletRetrofitService;
    private Retrofit retrofit;

    private WalletRetrofitService() {
        retrofit = NebulasRetrofitService.getInstance().getRetrofit();
    }

    public static synchronized WalletRetrofitService instance() {
        if (walletRetrofitService == null) {
            walletRetrofitService = new WalletRetrofitService();
        }
        return walletRetrofitService;
    }


    public Call<AdListResult> getAdList(com.nebulas.io.core.Transaction data, String finalContract, String args, String js, String function) {
        TestContractRequest testContractRequest = new TestContractRequest();
        ContractRequest contractRequest = new ContractRequest();
        contractRequest.source = finalContract;
        contractRequest.args = args;
        contractRequest.sourceType = js;
        contractRequest.function = function;


        testContractRequest.contract = contractRequest;
        testContractRequest.from = data.getFrom().string();
        testContractRequest.to = data.getTo().string();
        testContractRequest.gasLimit = String.valueOf(data.getGasLimit());
        testContractRequest.gasPrice = String.valueOf(data.getGasPrice());
        testContractRequest.nonce = data.getNonce();
        testContractRequest.value = String.valueOf(data.getValue());


        Call call = retrofit.create(WalletApiService.class).getAdList(testContractRequest);
        return call;
    }


}
