//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.nebulas.io.net.retrofit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import com.nebulas.io.util.LogUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class CallLoader<D> extends Loader<D> implements Callback<D> {

    private Call<D> rawCall;
    private D data;
    private Call<D> executing;

    public CallLoader(@NonNull Context context, @NonNull Call<D> call) {
        super(context);

        this.rawCall = call;
    }

    protected void onStartLoading() {
        if(this.rawCall.isCanceled()) {
            this.deliverCancellation();
        } else if(this.data == null) {
            this.data = null;
            this.executing = this.rawCall.clone();
            this.executing.enqueue(this);
        } else {
            this.deliverResult(this.data);
        }

    }

    public void onResponse(Call<D> call, Response<D> response) {
        if(response != null && response.isSuccessful()) {
            this.data = response.body();
        }else{
            try {
                LogUtils.e(response.errorBody().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.deliverResult(this.data);
    }

    @Override
    public void onFailure(Call<D> call, Throwable t) {
        this.deliverResult(null);
    }

    protected void onAbandon() {
        if(this.executing != null) {
            if(!this.executing.isCanceled()) {
                this.executing.cancel();
            }

            this.executing = null;
        }

        this.data = null;
    }
}
