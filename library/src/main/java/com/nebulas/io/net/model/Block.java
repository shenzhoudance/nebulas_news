package com.nebulas.io.net.model;

import com.nebulas.io.net.retrofit.NoProguard;

import java.util.List;

/**
 */

@NoProguard
public class Block {
    public String hash;
    public String parent_hash;
    public String height;
    public String nonce;
    public String coinbase;
    public String timestamp;
    private int chain_id;
    public String state_root;
    public String txs_root;
    public String events_root;
    public String consensus_root;
    public String miner;
    private boolean is_finality;
    private List<Transaction> transactions;


}
