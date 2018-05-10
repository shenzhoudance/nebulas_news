package com.nebulas.io.net.retrofit;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import retrofit2.Call;

/**
 * Created by carpediem on 2017/1/3.
 */

public abstract class CallLoaderCallbacks<D> implements LoaderManager.LoaderCallbacks<D> {
    private final Context context;

    public CallLoaderCallbacks(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public final Loader<D> onCreateLoader(int id, Bundle args) {
        return new CallLoader(this.context, this.onCreateCall(id, args));
    }

    public abstract Call<D> onCreateCall(int id, Bundle args);

    public void onLoaderReset(Loader loader) {
    }
}
