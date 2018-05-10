package com.nebulas.io.wallet;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nebulas.io.R;
import com.nebulas.io.account.AccountManager;
import com.nebulas.io.core.Address;
import com.nebulas.io.ui.BaseActivity;

/**
 * Created by legend on 2018/5/6.
 */

public class ImportAccountActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_account);
        setUpViews();
        setTitle("导入钱包");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.btn_importaccount) {
            importAccount();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void importAccount() {

        final String keystore = ((EditText) findViewById(R.id.edit_keystore)).getText().toString();
        if (TextUtils.isEmpty(keystore)) {
            ((EditText) findViewById(R.id.edit_keystore)).setError("请输入密钥");
            return ;
        }

        final String pwd = ((EditText) findViewById(R.id.edit_pwd)).getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            ((EditText) findViewById(R.id.edit_pwd)).setError("请输入密码");
            return ;
        }
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        new AsyncTask<Void, Void, Address>() {
            @Override
            protected Address doInBackground(Void... voids) {
                Address address = AccountManager.instance().load(keystore.getBytes(), pwd.getBytes());
                return address;

            }

            @Override
            protected void onPostExecute(Address address) {
                findViewById(R.id.progress).setVisibility(View.GONE);
                if (address != null) {
                    Toast.makeText(ImportAccountActivity.this,"导入钱包 " + address.string(),Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(ImportAccountActivity.this, "导入失败", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();


    }

    private void setUpViews() {
        findViewById(R.id.btn_importaccount).setOnClickListener(this);

    }
}
