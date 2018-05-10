package com.nebulas.io.net.model;

import com.nebulas.io.net.retrofit.NoProguard;

/**
 * Created by legend on 2018/5/8.
 */

@NoProguard
public class TestContractRequest {

    public ContractRequest contract;
    public String from;
    public String to;
    public String gasLimit;
    public String gasPrice;
    public long nonce;
    public String value;

    
}
