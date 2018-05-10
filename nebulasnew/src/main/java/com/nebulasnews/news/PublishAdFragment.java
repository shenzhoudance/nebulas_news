package com.nebulasnews.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nebulas.io.account.AccountManager;
import com.nebulas.io.core.Address;
import com.nebulas.io.core.Transaction;
import com.nebulas.io.core.TransactionCallPayload;
import com.nebulas.io.util.ByteUtils;
import com.nebulas.io.util.TimeUtils;
import com.nebulas.io.util.Utils;
import com.nebulas.io.ui.BaseFragment;
import com.nebulas.io.net.model.AccountState;
import com.nebulas.io.net.model.RawResult;
import com.nebulas.io.net.model.TestContractResult;
import com.nebulas.io.net.retrofit.CallLoaderCallbacks;
import com.nebulas.io.net.retrofit.LoaderIdManager;
import com.nebulas.io.net.retrofit.NebulasRetrofitService;
import com.nebulas.io.net.util.NetConfig;
import com.nebulas.io.wallet.TransferInfoActivity;
import com.nebulasnews.R;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;

import retrofit2.Call;

/**
 * Created by legend on 2018/5/7.
 */

public class PublishAdFragment extends BaseFragment implements View.OnClickListener {
    public static BaseFragment instance(Bundle bundle) {
        BaseFragment fragment = new PublishAdFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_publish_ad, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("发布动态");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews();
        fetchAccountInfo();
    }

    private void setUpViews() {
        ((TextView) PublishAdFragment.this.getView().findViewById(R.id.tv_address_from)).setText(AccountManager.instance().getCurrentAccount().getAddress().string());
        getView().findViewById(R.id.btn_send_trans).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.btn_send_trans).setOnClickListener(this);
    }

    private BigDecimal balance;
    private AccountState accountState;

    private void fetchAccountInfo() {
        CallLoaderCallbacks accountInfo = new CallLoaderCallbacks<AccountState>(getContext()) {

            @Override
            public void onLoadFinished(Loader<AccountState> loader, AccountState data) {
                if (data != null) {
                    accountState = data;
                    balance = Utils.getNormalNebulas(data.balance);
                    ((TextView) PublishAdFragment.this.getView().findViewById(R.id.tv_address_from)).setText(AccountManager.instance().getCurrentAccount().getAddress().string());
                    ((TextView) PublishAdFragment.this.getView().findViewById(R.id.tv_balance)).setText(String.valueOf(balance));
                    ((TextView) PublishAdFragment.this.getView().findViewById(R.id.tv_nonce)).setText(String.valueOf(data.nonce + 1));
                }
            }

            @Override
            public Call<AccountState> onCreateCall(int id, Bundle args) {
                return NebulasRetrofitService.getInstance().accountState(AccountManager.instance().getCurrentAccount().getAddress().string());

            }

        };
        getLoaderManager().restartLoader(LoaderIdManager.getLoaderId(accountInfo.getClass()), null, accountInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_trans:
                callContract(null);
                break;

        }
    }

    /**
     * 部署合约
     */
    private void callContract(String gas) {
        try {
            if (accountState == null) {
                Toast.makeText(getActivity(), "请填写相关数据", Toast.LENGTH_SHORT).show();
                return;
            }

            final String title = ((EditText) getView().findViewById(R.id.edit_title)).getText().toString();
            if (TextUtils.isEmpty(title)) {
                Toast.makeText(getActivity(), "请输入广告title", Toast.LENGTH_SHORT).show();
                return;
            }

            final String desc = ((EditText) getView().findViewById(R.id.edit_desc)).getText().toString();
            if (TextUtils.isEmpty(desc)) {
                Toast.makeText(getActivity(), "请输入广告desc", Toast.LENGTH_SHORT).show();
                return;
            }

            final String url = ((EditText) getView().findViewById(R.id.edit_url)).getText().toString();
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(getActivity(), "请输入广告图片url", Toast.LENGTH_SHORT).show();
                return;
            }


            if (balance.floatValue() < 0) {
                Toast.makeText(getActivity(), "可用余额不足", Toast.LENGTH_SHORT).show();
                return;
            }

            //TODO:合约地址
            String toAddress = "n1oPmQbLcTQL3PSWbLZ9KKKwK6Xwd6vgkpt";

            String pwd = ((EditText) getView().findViewById(R.id.edit_pwd)).getText().toString();
            if (TextUtils.isEmpty(pwd)) {
                ((EditText) getView().findViewById(R.id.edit_pwd)).setError("请输入当前帐号密码");
                return;
            }

            int chainID = NetConfig.getCurrentNetType().getType(); //1 mainet,1001 testnet, 100 default private
            Address from = AccountManager.instance().getCurrentAccount().getAddress();
            Address to = Address.ParseFromString(toAddress);

            BigInteger value = Utils.convertToTransValue("0");

            Ad ad = new Ad().setTitle(title)
                    .setDesc(desc)
                    .setUrl(url)
                    .setBalance(value.toString())
                    .setNasPerShare("0")
                    .setFrom(from.string())
                    .setTime((TimeUtils.formatTime(System.currentTimeMillis())));

            byte[] bytes = new Gson().toJson(ad).getBytes(Charset.forName("UTF-8"));

            String ad64Str = Base64.encodeToString(bytes, Base64.DEFAULT).replace("\n","");

            AdContainer adContainer = new AdContainer(ad64Str, "", "");

            String functionArgs = String.format("[\"%s\",\"%s\"]", title, new Gson().toJson(adContainer).replace("\"","\\\""));
            String function = "save";

            Transaction.PayloadType payloadType = Transaction.PayloadType.CALL;
            byte[] payload = new TransactionCallPayload(function, functionArgs).toBytes();

            BigInteger gasLimit;
            if (gas == null) {
                gasLimit = new BigInteger("1000"); // 0 < gasPrice < 10^12
            }else{
                int tmpLimit = ((Integer.valueOf(gas) / 200000) == 0 ? 1 : Integer.valueOf(gas) / 200000) * 100;
                gasLimit = new BigInteger(String.valueOf(tmpLimit));
            }
            gasLimit = new BigInteger("200000");
            ((EditText)getView().findViewById(R.id.edit_gas_limit)).setText(gasLimit.toString());

            BigInteger gasPrice = new BigInteger("1000000"); // 0 < gasPrice < 10^12
            Transaction transaction = new Transaction(chainID, from, to, value, accountState.nonce + 1, payloadType, payload, gasPrice, gasLimit);
            AccountManager.instance().signTransaction(transaction, pwd.getBytes());
            //gas从user接口获取
            if (gas == null) {
                getTestResult(transaction, function, functionArgs);
            }else{
                transRequest(ByteUtils.Base64ToString(transaction.toProto()));
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "交易失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    private void getTestResult(final Transaction transaction, final String function, final String functionArgs) {
        PublishAdFragment.this.getView().findViewById(R.id.progress).setVisibility(View.VISIBLE);
        CallLoaderCallbacks<TestContractResult> testContractResultCallLoaderCallbacks = new CallLoaderCallbacks<TestContractResult>(getContext()) {
            @Override
            public Call<TestContractResult> onCreateCall(int id, Bundle args) {
                return NebulasRetrofitService.getInstance().testContract(transaction,null,functionArgs,null,function);
            }

            @Override
            public void onLoadFinished(Loader<TestContractResult> loader, TestContractResult result) {
                PublishAdFragment.this.getView().findViewById(R.id.progress).setVisibility(View.GONE);
                if (result != null) {
                    if (TextUtils.isEmpty(result.execute_err)) {
                        try {
                            callContract(result.estimate_gas);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(getActivity(), result.execute_err, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        getLoaderManager().restartLoader(LoaderIdManager.getLoaderId(testContractResultCallLoaderCallbacks.getClass()), null, testContractResultCallLoaderCallbacks);
    }

    private void transRequest(final String rawData) {
        CallLoaderCallbacks sendReqeust = new CallLoaderCallbacks<RawResult>(getContext()) {

            @Override
            public void onLoadFinished(Loader<RawResult> loader, RawResult data) {
                PublishAdFragment.this.getView().findViewById(R.id.progress).setVisibility(View.GONE);
                if (data != null) {
                    Toast.makeText(getActivity(), "发布成功 " + data.txhash, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), TransferInfoActivity.class);
                    intent.putExtra("txhash", data.txhash);
                    intent.putExtra("contract_address", data.contract_address);
                    startActivity(intent);
                }
            }

            @Override
            public Call<RawResult> onCreateCall(int id, Bundle args) {
                PublishAdFragment.this.getView().findViewById(R.id.progress).setVisibility(View.VISIBLE);
                return NebulasRetrofitService.getInstance().sendRawData(rawData);
            }
        };
        getLoaderManager().restartLoader(LoaderIdManager.getLoaderId(sendReqeust.getClass()), null, sendReqeust);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((TextView) getView().findViewById(R.id.edit_pwd)).setText("");
    }
}
