package com.nebulas.io.wallet;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.nebulas.io.R;
import com.nebulas.io.account.AccountManager;
import com.nebulas.io.core.Address;
import com.nebulas.io.ui.BaseFragment;
import com.nebulas.io.util.LogUtils;

/**
 * Created by legend on 2018/5/4.
 */

public class CreateAccountFragment extends BaseFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_account, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getView().findViewById(R.id.btn_createaccount).setOnClickListener(this);
        pwdInput = getView().findViewById(R.id.edit_pwd);

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private EditText pwdInput;

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_createaccount) {
            final String pwd = pwdInput.getText().toString();
            if (TextUtils.isEmpty(pwd) || pwd.length() < 9) {
                pwdInput.setError("请输入合法密码");
            } else {
                CreateAccountFragment.this.getView().findViewById(R.id.progress).setVisibility(View.VISIBLE);

                new AsyncTask<Void, Void, Address>(){

                    @Override
                    protected Address doInBackground(Void... voids) {
                        try {
                            Address address = AccountManager.instance().newAccount(pwd.getBytes());
                            return address;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Address address) {
                        super.onPostExecute(address);
                        CreateAccountFragment.this.getView().findViewById(R.id.progress).setVisibility(View.GONE);
                        if (address != null) {
                            String jsonStr = AccountManager.instance().export(address, pwd.getBytes());
                            CreateAccountFragment.this.getView().findViewById(R.id.key_layout).setVisibility(View.VISIBLE);
                            ((TextView) CreateAccountFragment.this.getView().findViewById(R.id.textview_keyjson)).setText(jsonStr);
                            LogUtils.d("密钥" + jsonStr);
                        }
                    }
                }.execute();

            }

        }
    }
}
