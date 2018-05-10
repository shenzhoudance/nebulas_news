package com.nebulas.io.ui.list;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nebulas.io.R;
import com.nebulas.io.net.retrofit.CallLoader;
import com.nebulas.io.ui.list.adapter.BaseListAdapter;
import com.nebulas.io.util.CollectionUtils;
import com.nebulas.io.util.NetUtils;

import java.util.List;

import retrofit2.Call;

/**
 * Created with IntelliJ IDEA.
 * Date: 13-12-7
 * Time: 上午12:13
 * To change this template use File | Settings | File Templates.
 */
public abstract class PullToRefreshListFragment<D> extends BaseListFragment implements AbsListView.OnScrollListener, LoaderManager.LoaderCallbacks<D> {
    protected static final int LIST_LOADER_ID = 100;


    protected boolean shouldInitLoader = true;


    public static final int LoaderDelayedMission = 100;
    public int LoaderDelayed = 0;
    public static final int defultLoaderDelayed = 1000;

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LoaderDelayedMission:
                    try {
                        refresh = true;
                        getLoaderManager().restartLoader(LIST_LOADER_ID, null, PullToRefreshListFragment.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };



    public AbsListView.OnScrollListener onScrollListener;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (shouldInitLoader) {
            if (LoaderDelayed > 0 && isAdded() && mHandler != null) {
                mHandler.sendEmptyMessageDelayed(LoaderDelayedMission, LoaderDelayed);
            } else {
                getLoaderManager().restartLoader(LIST_LOADER_ID, null, PullToRefreshListFragment.this);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler = null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }



    @Override
    public Loader<D> onCreateLoader(int i, Bundle bundle) {
        isPageLoading = true;
        return new CallLoader<D>(getContext(), getCall());
    }

    public abstract Call<D> getCall();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    private int lastVisibleItem;

    public abstract BaseAdapter createAdapter();

    public void onNULLData() {
    }

    @Override
    public void onLoadFinished(Loader<D> loader, D o) {
        if (!isPageLoading && !refresh) {
            return;
        }

        isPageLoading = false;
        //没拿到数据
        if (getListAdapter() == null) {
            setListAdapter(createAdapter());
        }
        if (refresh) {
            swipeRefreshLayout.setRefreshing(false);
            if (getListAdapter() != null) {
                ((BaseListAdapter) getListAdapter()).clear();
            }
            refresh = false;
        }
        if (o == null || CollectionUtils.isEmpty((List<Object>) o)) {
            hasNextPage = false;
            onNULLData();
            getListView().removeFooterView(footer);
            footerAdded = false;
        } else {
            hasNextPage = true;
            if (getListAdapter() == null) {
                setListAdapter(createAdapter());
            }
            ((BaseListAdapter) getListAdapter()).appendData((List) o);
        }
    }

    protected boolean footerAdded;
    protected boolean isPaged = true;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (onScrollListener != null) {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        if (lastVisibleItem == firstVisibleItem + visibleItemCount || !isPaged) {
            return;
        }
        lastVisibleItem = firstVisibleItem + visibleItemCount;
        if (!hasNextPage) {
            return;
        }
        if (visibleItemCount > 0 && lastVisibleItem >= totalItemCount - 1 && !isPageLoading && firstVisibleItem != 0) {
            getLoaderManager().restartLoader(LIST_LOADER_ID, null, this);
            hasNextPage = true;
            if (!footerAdded && isPaged) {
                footerAdded = true;
                getListView().addFooterView(footer);
            }
        }
    }

    @Override
    protected View createDefaultEmptyView() {
        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.empty_view, null);
        TextView textView = (TextView) emptyView.findViewById(R.id.empty_text);

        if (NetUtils.isNetworkAvailable(getActivity())) {
            textView.setText(getEmptyText());
        } else {
            textView.setText("当前没有可用网络，请检查网络设置");
        }
        return emptyView;
    }

    @Override
    public void onRefresh() {
        refresh = true;
        try {
            getLoaderManager().restartLoader(LIST_LOADER_ID, null, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean refresh = false;
    protected boolean hasNextPage = true;

    public int getOffset() {
        int offset = 0;
        if (getListAdapter() != null) {
            offset = getListAdapter().getCount();
        }
        if (refresh) {
            return 0;
        }
        return offset;
    }

    @Override
    public void refresh() {
        refresh = true;
        try {
            getLoaderManager().restartLoader(LIST_LOADER_ID, null, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean isPageLoading;

    protected RelativeLayout footer;

    @Override
    public void onLoaderReset(Loader loader) {
    }
}
