package com.nebulas.io.wallet;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nebulas.io.R;
import com.nebulas.io.ui.BaseActivity;

/**
 * Created by nebulas on 2018/5/9.
 */

public class NewAccountActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new CreateAccountFragment()).commitAllowingStateLoss();
        setTitle("新建钱包");
    }
}
