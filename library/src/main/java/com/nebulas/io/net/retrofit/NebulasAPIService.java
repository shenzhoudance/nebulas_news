package com.nebulas.io.net.retrofit;

import com.nebulas.io.net.model.Account;
import com.nebulas.io.net.model.AccountState;
import com.nebulas.io.net.model.AddressList;
import com.nebulas.io.net.model.Block;
import com.nebulas.io.net.model.TestContractResult;
import com.nebulas.io.net.model.Config;
import com.nebulas.io.net.model.Data;
import com.nebulas.io.net.model.EstimateGas;
import com.nebulas.io.net.model.Events;
import com.nebulas.io.net.model.GasPrice;
import com.nebulas.io.net.model.Miners;
import com.nebulas.io.net.model.NebState;
import com.nebulas.io.net.model.RawResult;
import com.nebulas.io.net.model.Result;
import com.nebulas.io.net.model.SubscribeResult;
import com.nebulas.io.net.model.TestContractRequest;
import com.nebulas.io.net.model.TransResult;
import com.nebulas.io.net.model.Transaction;
import com.nebulas.io.update.UpdateInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by legend on 2018/5/5.
 * RPC API:
 * User  : https://github.com/nebulasio/wiki/blob/master/rpc.md
 * Admin : https://github.com/nebulasio/wiki/blob/master/rpc_admin.md
 *
 */

class NebulasAPIService {

    public interface AccountService {
        @GET("/v1/user/nebstate")
        Call<NebState> getNetState();

        @GET("/v1/admin/getConfig")
        Call<Config> getConfig();

        @GET("/v1/admin/accounts")
        Call<AddressList> getAccountList();

        
        @POST("/v1/admin/account/new")
        Call<Account> createNewAccount(@Field("passphrase") String passphrase);

        
        @POST("/v1/admin/account/unlock")
        Call<Result> unlockAccount(@Field("address") String address,
                                   @Field("passphrase") String passphrase,
                                   @Field("duration") String duration);

        
        @POST("/v1/admin/account/lock")
        Call<Result> lockAccount(@Field("address") String address);

        
        @POST("/v1/admin/sign")
        Call<Result> signTransaction(@Field("transaction") String address,
                                     @Field("passphrase") String passphrase);

        
        @POST("/v1/admin/transactionWithPassphrase")
        Call<TransResult> transactionWithPassphrase(@Field("transaction") String address,
                                                    @Field("passphrase") String passphrase);

        
        @POST("/v1/user/transaction")
        Call<TransResult> normalTransaction(@Field("from") String from,
                                            @Field("to") String to,
                                            @Field("value") String value,
                                            @Field("nonce") String nonce,
                                            @Field("gas_price") String gas_price,
                                            @Field("gas_limit") String gas_limit,
                                            @Field("binary") byte[] binary);

        
        @POST("/v1/user/transaction")
        Call<TransResult> contractTransaction(@Field("from") String from,
                                              @Field("to") String to,
                                              @Field("value") String value,
                                              @Field("nonce") String nonce,
                                              @Field("gas_price") String gas_price,
                                              @Field("gas_limit") String gas_limit,
                                              @Field("contract") String contract,
                                              @Field("binary") byte[] binary);

        
        @POST("/v1/admin/sign/hash")
        Call<TransResult> signHash(@Field("address") String address,
                                   @Field("hash") String hash,
                                   @Field("alg") int alg);


        @POST("/v1/user/accountstate")
        Call<AccountState> accountState(@Body Account address);

        
        @POST("/v1/user/call")
        Call<TestContractResult> testContract(@Body TestContractRequest contractRequest);

        
        @POST("/v1/user/rawtransaction")
        Call<RawResult> sendRawtransaction(@Body Data data);


        
        @POST("/v1/user/getBlockByHash")
        Call<Block> getBlockByHash(@Field("hash") String hash,
                                   @Field("full_fill_transaction") boolean  full_fill_transaction);

        
        @POST("/v1/user/getTransactionReceipt")
        Call<Transaction> getTransactionReceipt(@Body TransResult hash);

        @POST("/v1/user/subscribe")
        Call<SubscribeResult> subscribe(@Field("topics") String topics);

        @GET("/v1/user/getGasPrice")
        Call<GasPrice> subscribe();


        
        @POST("/v1/user/estimateGas")
        Call<EstimateGas> estimateGas(@Field("from") String from,
                                      @Field("to") String to,
                                      @Field("value") String value,
                                      @Field("nonce") String nonce,
                                      @Field("gas_price") String gas_price,
                                      @Field("gas_limit") String gas_limit,
                                      @Field("binary") byte[] binary);

        
        @POST("/v1/user/getEventsByHash")
        Call<Events> getEventsByHash(@Field("hash") String hash);

        
        @POST("/v1/user/dynasty")
        Call<Miners> dynasty(@Field("height") int height);

        @GET("/tools/mockapi/5618/nebulas")
        Call<UpdateInfo> checkUpdate();



    }
}
