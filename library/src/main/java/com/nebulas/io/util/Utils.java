package com.nebulas.io.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;

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

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static boolean isAvaiableSpace(int sizeMb) {
        boolean ishasSpace = false;
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            String sdcard = Environment.getExternalStorageDirectory().getPath();
            StatFs statFs = new StatFs(sdcard);
            long blockSize = statFs.getBlockSize();
            long blocks = statFs.getAvailableBlocks();
            long availableSpare = (blocks * blockSize) / (1024 * 1024);
            if (availableSpare > sizeMb) {
                ishasSpace = true;
            }
        }
        return ishasSpace;
    }


    public static int getScreenWith(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

}
