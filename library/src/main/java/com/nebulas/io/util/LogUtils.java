package com.nebulas.io.util;

import android.util.Log;

/**
 * Created by legend on 2018/5/7.
 */

public class LogUtils {
    public static final String TAG = "nebulas_wallet";

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

}
