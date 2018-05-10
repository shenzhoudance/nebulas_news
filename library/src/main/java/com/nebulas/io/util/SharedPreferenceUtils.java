package com.nebulas.io.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Created by legend on 2018/5/6.
 */

public class SharedPreferenceUtils {
    public static final String LOGIN_STORE_PREFERENCE = "users";

    public static SharedPreferences getUserSharedPreferences(Context context) {
        return context.getSharedPreferences(LOGIN_STORE_PREFERENCE,
                Context.MODE_PRIVATE);
    }

    public static final String CURRENT_USER = "currentuser";

    public static SharedPreferences getCurrentUserSP(Context context) {
        return context.getSharedPreferences(CURRENT_USER,
                Context.MODE_PRIVATE);
    }


    public static final String KEY_MAP= "key";

    public static SharedPreferences getKeyMapSP(Context context) {
        return context.getSharedPreferences(KEY_MAP,
                Context.MODE_PRIVATE);
    }


    /**
     * 在2.3以上使用新的接口，减少文件写入操作
     *
     * @param editor
     */
    public static void apply(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
}