package com.nebulas.io.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by legend on 2018/5/7.
 */

public class Utils {
    public static final BigDecimal NAS = new BigDecimal(1000000000000000000.f);

    public static  BigDecimal getNormalNebulas(String value) {
        BigDecimal balance = new BigDecimal(Float.valueOf(value)).divide(NAS, 10, BigDecimal.ROUND_HALF_EVEN);
        return balance;
    }


    public static  BigInteger convertToTransValue(String transMoney) {
        BigDecimal floatValue = new BigDecimal(Float.valueOf(transMoney));
        BigInteger value = new BigInteger(String.valueOf(floatValue.multiply(Utils.NAS).toBigInteger()));
        return value;

    }

}
