package com.nebulasnews.news;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.nebulas.io.account.AccountManager;
import com.nebulas.io.core.Address;
import com.nebulas.io.core.Transaction;
import com.nebulas.io.core.TransactionCallPayload;
import com.nebulas.io.net.model.AccountState;
import com.nebulas.io.net.retrofit.CallLoaderCallbacks;
import com.nebulas.io.net.retrofit.LoaderIdManager;
import com.nebulas.io.net.retrofit.NebulasRetrofitService;
import com.nebulas.io.net.util.NetConfig;
import com.nebulas.io.ui.BaseFragment;
import com.nebulas.io.ui.list.PullToRefreshListFragment;
import com.nebulasnews.net.WalletRetrofitService;

import java.math.BigInteger;

import retrofit2.Call;

/**
 * Created by legend on 2018/5/7.
 */

public class NewsFragment extends PullToRefreshListFragment<AdListResult> {
    public static BaseFragment instance(Bundle bundle) {
        BaseFragment fragment = new NewsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shouldInitLoader = false;
        getActivity().setTitle("动态");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public Call<AdListResult> getCall() {
        @SuppressLint("DefaultLocale")
        String functionArgs = String.format("[100,%d]", getOffset());
        String function = "forEach";
        Transaction transaction = null;
        try {
            //TODO:
            String toAddress = "n1oPmQbLcTQL3PSWbLZ9KKKwK6Xwd6vgkpt";
            int chainID = NetConfig.getCurrentNetType().getType(); //1 mainet,1001 testnet, 100 default private
            Address from = AccountManager.instance().getCurrentAccount().getAddress();
            Address to = Address.ParseFromString(toAddress);
            BigInteger value = new BigInteger(String.valueOf(0));

            Transaction.PayloadType payloadType = Transaction.PayloadType.CALL;
            byte[] payload = new TransactionCallPayload(function, functionArgs).toBytes();
            BigInteger gasLimit = new BigInteger("1"); // 0 < gasPrice < 10^12
            BigInteger gasPrice = new BigInteger("1000000"); // 0 < gasPrice < 10^12
            transaction = new Transaction(chainID, from, to, value, accountState.nonce + 1, payloadType, payload, gasPrice, gasLimit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return WalletRetrofitService.instance().getAdList(transaction, null, functionArgs, null, function);
    }

    @Override
    public BaseAdapter createAdapter() {
        return new AdListAdapter(getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchAccountInfo();
    }

    private AccountState accountState;

    private void fetchAccountInfo() {
        CallLoaderCallbacks accountInfo = new CallLoaderCallbacks<AccountState>(getContext()) {
            @Override
            public void onLoadFinished(Loader<AccountState> loader, AccountState data) {
                if (data != null) {
                    accountState = data;
                    refresh();
                }else{
                    NewsFragment.this.setListShown(false);
                }
            }

            @Override
            public Call<AccountState> onCreateCall(int id, Bundle args) {
                return NebulasRetrofitService.getInstance().accountState(AccountManager.instance().getCurrentAccount().getAddress().string());
            }

        };
        getLoaderManager().restartLoader(LoaderIdManager.getLoaderId(accountInfo.getClass()), null, accountInfo);
    }


}
