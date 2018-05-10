package com.nebulas.io.net.model;

import com.google.gson.annotations.SerializedName;
import com.nebulas.io.net.retrofit.NoProguard;

/**
 * Created by legend on 2018/5/5.
 */

@NoProguard
public class NebState {
    private int chain_id;
    public String tail;
    public String lib;
    public String height;
    public String protocol_version;
    @SerializedName("synchronized")
    private boolean synchronizedBool;
    public String version;


}
