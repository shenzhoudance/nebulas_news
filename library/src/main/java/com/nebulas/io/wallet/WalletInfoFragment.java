package com.nebulas.io.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nebulas.io.R;
import com.nebulas.io.account.Account;
import com.nebulas.io.account.AccountManager;
import com.nebulas.io.util.Utils;
import com.nebulas.io.ui.BaseFragment;
import com.nebulas.io.net.model.AccountState;
import com.nebulas.io.net.retrofit.CallLoaderCallbacks;
import com.nebulas.io.net.retrofit.LoaderIdManager;
import com.nebulas.io.net.retrofit.NebulasRetrofitService;
import com.nebulas.io.qrcode.QRCodeUtil;

import retrofit2.Call;

/**
 * Created by legend on 2018/5/4.
 */

public class WalletInfoFragment extends BaseFragment implements View.OnClickListener {

    private Account account ;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        account = AccountManager.instance().getCurrentAccount();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wallet_layout, null);
        view.findViewById(R.id.btn_importaccount).setOnClickListener(this);
        view.findViewById(R.id.btn_createaccount).setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        account = AccountManager.instance().getCurrentAccount();
        if (account != null) {
            getView().findViewById(R.id.account_info).setVisibility(View.VISIBLE);
            ((ImageView) getView().findViewById(R.id.img_qrcode)).setImageBitmap(QRCodeUtil.createQRCodeBitmap(account.getAddress().string(), 300));
            ((TextView) getView().findViewById(R.id.tv_address)).setText(account.getAddress().string());
        }else{
            getView().findViewById(R.id.account_info).setVisibility(View.GONE);
        }
        if (account != null) {
            CallLoaderCallbacks callLoaderCallbacks = new CallLoaderCallbacks<AccountState>(getContext()) {
                @Override
                public void onLoadFinished(Loader<AccountState> loader, AccountState data) {
                    if (data != null) {
                        ((TextView) WalletInfoFragment.this.getView().findViewById(R.id.tv_balance)).setText(Utils.getNormalNebulas(data.balance) + " NAS");
                    }
                }

                @Override
                public Call<AccountState> onCreateCall(int id, Bundle args) {
                    return NebulasRetrofitService.getInstance().accountState(AccountManager.instance().getCurrentAccount().getAddress().string());
                }
            };
            getLoaderManager().restartLoader(LoaderIdManager.getLoaderId(callLoaderCallbacks.getClass()), null, callLoaderCallbacks);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("钱包");

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_createaccount) {
            startActivity(new Intent(getActivity(), NewAccountActivity.class));
        } else if (i == R.id.btn_importaccount) {
            startActivity(new Intent(getActivity(), ImportAccountActivity.class));
        }
    }

    public static BaseFragment instance(Bundle bundle) {
        BaseFragment fragment = new WalletInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
