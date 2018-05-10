package com.nebulas.io.net.model;

import com.nebulas.io.net.retrofit.NoProguard;


@NoProguard
public class Transaction {
    public String hash;
    public int chainId;
    public String from;
    public String to;
    public String value;
    public String nonce;
    public String timestamp;
    public String type;
    public String data;
    public String gas_price;
    public String gas_limit;
    public String contract_address;
    public int status;
    public String gas_used;
    public String execute_error;
    public String execute_result;
}
