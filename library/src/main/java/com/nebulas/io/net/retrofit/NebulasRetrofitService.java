package com.nebulas.io.net.retrofit;

import android.content.Context;

import com.nebulas.io.net.model.Account;
import com.nebulas.io.net.model.AccountState;
import com.nebulas.io.net.model.AddressList;
import com.nebulas.io.net.model.ContractRequest;
import com.nebulas.io.net.model.Data;
import com.nebulas.io.net.model.NebState;
import com.nebulas.io.net.model.RawResult;
import com.nebulas.io.net.model.TestContractRequest;
import com.nebulas.io.net.model.TestContractResult;
import com.nebulas.io.net.model.TransResult;
import com.nebulas.io.net.model.Transaction;
import com.nebulas.io.net.util.NetConfig;
import com.nebulas.io.update.UpdateInfo;

import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by carpediem on 2017/1/3.
 */

public class NebulasRetrofitService {

    private static NebulasRetrofitService nebulasRetrofitService;
    private Retrofit retrofit;
    private Retrofit updateRetrofit;

    private NebulasRetrofitService() {
        retrofit = RetrofitFactory.getInstance("https://mainnet.nebulas.io/");
        updateRetrofit = RetrofitFactory.getInstance("http://www.wanandroid.com");
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void changeNet(NetConfig.NET_TYPE net_type) {
        switch (net_type) {
            case LOCAL:
                retrofit = RetrofitFactory.getInstance("http://192.168.0.103:8685/");
                break;
            case MAINNET:
                retrofit = RetrofitFactory.getInstance("https://mainnet.nebulas.io/");
                break;
            case TESTNET:
                retrofit = RetrofitFactory.getInstance("https://testnet.nebulas.io/");
                break;
        }
    }

    /**
     * 获取nebulas主节点状态
     */
    public Call<NebState> getNebState() {
        return retrofit.create(NebulasAPIService.AccountService.class).getNetState();
    }

    public Call<AccountState> accountState(String address) {
        Account account = new Account();
        account.address = address;
        return retrofit.create(NebulasAPIService.AccountService.class).accountState(account);
    }

    public Call<Transaction> getTransResult(String hash,String contract_address) {
        TransResult result = new TransResult();
        result.hash = hash;
        result.contract_address = contract_address;
        return retrofit.create(NebulasAPIService.AccountService.class).getTransactionReceipt(result);
    }

    public Call<RawResult> sendRawData(String dataStr) {
        Data data = new Data();
        data.data = dataStr;
        return retrofit.create(NebulasAPIService.AccountService.class).sendRawtransaction(data);
    }

    public Call<TestContractResult> testContract(com.nebulas.io.core.Transaction data, String finalContract, String args, String js,String function) {
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

        return retrofit.create(NebulasAPIService.AccountService.class).testContract(testContractRequest);
    }

    /**
     * 获取nebulas主节点状态
     */
    public Call<AddressList> getAccounts() {
        return retrofit.create(NebulasAPIService.AccountService.class).getAccountList();
    }

    public Call<UpdateInfo> checkUpdate() {
        return updateRetrofit.create(NebulasAPIService.AccountService.class).checkUpdate();
    }

    public Call<Account> createNewAccount(String pwd) {
        return retrofit.create(NebulasAPIService.AccountService.class).createNewAccount(pwd);
    }


    public static NebulasRetrofitService getInstance() {
        if (null == nebulasRetrofitService) {
            synchronized (NebulasRetrofitService.class) {
                if (null == nebulasRetrofitService) {
                    nebulasRetrofitService = new NebulasRetrofitService();
                }
            }
        }
        return nebulasRetrofitService;
    }

}
