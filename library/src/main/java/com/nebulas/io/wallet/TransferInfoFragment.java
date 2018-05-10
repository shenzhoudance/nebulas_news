package com.nebulas.io.wallet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nebulas.io.R;
import com.nebulas.io.ui.BaseFragment;
import com.nebulas.io.net.model.Transaction;
import com.nebulas.io.net.retrofit.CallLoaderCallbacks;
import com.nebulas.io.net.retrofit.LoaderIdManager;
import com.nebulas.io.net.retrofit.NebulasRetrofitService;

import retrofit2.Call;

/**
 * Created by legend on 2018/5/4.
 */

public class TransferInfoFragment extends BaseFragment implements View.OnClickListener{

    public static BaseFragment instance(Bundle bundle) {
        BaseFragment fragment = new TransferInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfer_info_layout, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews();
    }

    private String txHash;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            txHash = getArguments().getString("txhash");
        }
        getActivity().setTitle("交易查询");
    }

    private EditText searchEditText;

    private void setUpViews() {
        getView().findViewById(R.id.btn_search_transaction).setOnClickListener(this);
        searchEditText = getView().findViewById(R.id.edit_hash);
        searchEditText.setText(txHash);
    }

    @Override
    public void updateData(Bundle bundle) {
        super.updateData(bundle);
        if (bundle != null) {
            String txhash = bundle.getString("txhash");
            if (!TextUtils.isEmpty(txhash)) {
                searchEditText.setText(txhash);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("转账");
        if (searchEditText.getText().toString() != null) {
            searchTans();
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_search_transaction) {
            searchTans();

        }
    }

    private void searchTans() {

        getView().findViewById(R.id.progress).setVisibility(View.VISIBLE);
        final String hash = searchEditText.getText().toString();
        final String contract_address = getArguments() != null ? getArguments().getString("contract_address") : null;
        if (TextUtils.isEmpty(hash)) {
            searchEditText.setError("请输入有效hash");
            return;
        }

        CallLoaderCallbacks<Transaction> transactionLoaderCallbacks = new CallLoaderCallbacks<Transaction>(getContext()) {
            @Override
            public void onLoadFinished(Loader<Transaction> loader, Transaction data) {
                getView().findViewById(R.id.progress).setVisibility(View.GONE);
                if (data != null) {
                    TransferInfoFragment.this.getView().findViewById(R.id.search_result).setVisibility(View.VISIBLE);
                    ((TextView) TransferInfoFragment.this.getView().findViewById(R.id.tv_hash)).setText(data.hash);
                    ((TextView) TransferInfoFragment.this.getView().findViewById(R.id.tv_contract)).setText(data.contract_address);
                    switch (data.status) {
                        case 0:
                            ((TextView) TransferInfoFragment.this.getView().findViewById(R.id.tv_status)).setText("失败");
                            break;
                        case 1:
                            ((TextView) TransferInfoFragment.this.getView().findViewById(R.id.tv_status)).setText("成功");
                            break;
                        case 2:
                            ((TextView) TransferInfoFragment.this.getView().findViewById(R.id.tv_status)).setText("确认中");
                            break;
                    }
                    ((TextView) TransferInfoFragment.this.getView().findViewById(R.id.tv_from)).setText(data.from);
                    ((TextView) TransferInfoFragment.this.getView().findViewById(R.id.tv_to)).setText(data.to);
                    ((TextView) TransferInfoFragment.this.getView().findViewById(R.id.tv_value)).setText(data.value);
                    ((TextView) TransferInfoFragment.this.getView().findViewById(R.id.tv_nonce)).setText(data.nonce);
                    ((TextView) TransferInfoFragment.this.getView().findViewById(R.id.tv_gas_price)).setText(data.gas_price);
                    ((TextView) TransferInfoFragment.this.getView().findViewById(R.id.tv_gas_limit)).setText(data.gas_limit);
                    ((TextView) TransferInfoFragment.this.getView().findViewById(R.id.tv_gas_spend)).setText(data.gas_used);
                    if (!TextUtils.isEmpty(data.data)) {
                        ((TextView) TransferInfoFragment.this.getView().findViewById(R.id.tv_data)).setText(data.data);
                    }
                }else{
                    TransferInfoFragment.this.getView().findViewById(R.id.search_result).setVisibility(View.GONE);
                    Toast.makeText(getContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public Call<Transaction> onCreateCall(int id, Bundle args) {
                return NebulasRetrofitService.getInstance().getTransResult(hash,contract_address);
            }
        };
        getLoaderManager().restartLoader(LoaderIdManager.getLoaderId(transactionLoaderCallbacks.getClass()), null, transactionLoaderCallbacks);
    }
}
