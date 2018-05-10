package com.nebulas.io.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by legend on 2018/5/4.
 */

public abstract class BaseFragment extends Fragment implements UpdateListener{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void updateData(Bundle bundle) {

    }
}
