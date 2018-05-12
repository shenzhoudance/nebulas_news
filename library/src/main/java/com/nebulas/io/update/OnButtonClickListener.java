package com.nebulas.io.update;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public interface OnButtonClickListener {

    int UPDATE = 0;
    int CANCEL = 1;

    @IntDef({UPDATE, CANCEL})
    @Retention(RetentionPolicy.SOURCE)
    @interface ID {

    }
    void onButtonClick(@ID int id);
}
