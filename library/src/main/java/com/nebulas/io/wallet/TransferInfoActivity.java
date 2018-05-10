package com.nebulas.io.wallet;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nebulas.io.R;
import com.nebulas.io.ui.BaseActivity;

/**
 * Created by nebulas on 2018/5/10.
 */

public class TransferInfoActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        final Bundle bundle = new Bundle();
        bundle.putString("txhash", getIntent().getStringExtra("txhash"));
        bundle.putString("contract_address", getIntent().getStringExtra("contract_address"));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, TransferInfoFragment.instance(bundle)).commitAllowingStateLoss();
        setTitle("帐号管理");
    }
}
