package com.nebulas.io.net.model;

import com.nebulas.io.net.retrofit.NoProguard;

import retrofit2.Converter;

/**
 * Created by legend on 2018/5/5.
 */

@NoProguard
public class TestContractResult{
    public String result;
    public String execute_err;
    public String estimate_gas;
}
