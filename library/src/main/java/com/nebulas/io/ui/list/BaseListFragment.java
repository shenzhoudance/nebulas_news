package com.nebulas.io.ui.list;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nebulas.io.ui.BaseFragment;

import com.nebulas.io.R;


public abstract class BaseListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    static final int INTERNAL_PROGRESS_CONTAINER_ID = 0x00ff0002;
    static final int INTERNAL_DEFAULT_EMPTY_ID = 0x00ff0004;

    final private AdapterView.OnItemClickListener mOnClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            final int headerViewsCount = getListView().getHeaderViewsCount();
            int realPosition = position - headerViewsCount;
            if (getListAdapter() != null && realPosition < getListAdapter().getCount()) {
                onListItemClick((ListView) parent, v, position - headerViewsCount, id);
            }
        }
    };
    ListAdapter mAdapter;
    ListView mList;
    View mEmptyView;
    View mProgressContainer;
    boolean mListShown;

    protected SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = getActivity();
        FrameLayout root= (FrameLayout) inflater.inflate(R.layout.swip_pull_to_refresh, null);
        swipeRefreshLayout = root.findViewById(R.id.swipelayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        LinearLayout pframe = createProgressContainer(context);
        pframe.setId(INTERNAL_PROGRESS_CONTAINER_ID);
        root.addView(pframe, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return root;
    }


    /**
     * Attach to list view once the view hierarchy has been created.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureList();
    }

    /**
     * Detach from list view.
     */
    @Override
    public void onDestroyView() {
        mList = null;
        mListShown = false;
        mEmptyView = mProgressContainer  = null;
        super.onDestroyView();
    }


    /**
     * This method will be called when an item in the list is selected.
     * Subclasses should override. Subclasses can call
     * getListView().getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param l        The ListView where the click happened
     * @param v        The view that was clicked within the ListView
     * @param position The position of the view in the list
     * @param id       The row id of the item that was clicked
     */
    public void onListItemClick(ListView l, View v, int position, long id) {
    }

    /**
     * Get the activity's list view widget.
     */
    public ListView getListView() {
        ensureList();
        return mList;
    }

    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     *
     * @param shown   If true, the list view is shown; if false, the progress
     *                indicator.  The initial value is true.
     */
    public void setListShown(boolean shown) {
        ensureList();
        if (mProgressContainer == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (mListShown == shown) {
            return;
        }
        mListShown = shown;
        if (shown) {
            mProgressContainer.setVisibility(View.GONE);
            mList.setVisibility(View.VISIBLE);
            if (mList.getEmptyView() != null) {
                mList.getEmptyView().setVisibility(View.VISIBLE);
            }
        } else {
            mProgressContainer.setVisibility(View.VISIBLE);
            mList.setVisibility(View.GONE);
            if (mList.getEmptyView() != null) {
                mList.getEmptyView().setVisibility(View.GONE);
            }
        }
    }

    /**
     * Get the ListAdapter associated with this activity's ListView.
     */
    public ListAdapter getListAdapter() {
        return mAdapter;
    }

    /**
     * Provide the cursor for the list view.
     */
    public void setListAdapter(ListAdapter adapter) {
        boolean hadAdapter = mAdapter != null;
        mAdapter = adapter;
        if (mList != null) {
            mList.setAdapter(adapter);
            if (!mListShown && !hadAdapter) {
                // The list was hidden, and previously didn't have an
                // adapter.  It is now time to show it.
                setListShown(true);
            }
        }
    }

    protected void ensureList() {
        if (mList != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        if (root instanceof ListView) {
            mList = (ListView) root;
        } else {
            mEmptyView = createEmptyView();
            mProgressContainer = root.findViewById(INTERNAL_PROGRESS_CONTAINER_ID);
            View rawListView = root.findViewById(R.id.listview);
            if (!(rawListView instanceof ListView)) {
                if (rawListView == null) {
                    throw new RuntimeException(
                            "Your content must have a ListView whose id attribute is " +
                                    "'android.R.id.list'");
                }
                throw new RuntimeException(
                        "Content has view with id attribute 'android.R.id.list' "
                                + "that is not a ListView class");
            }
            mList = (ListView) rawListView;
            if (mEmptyView != null) {
                mList.setEmptyView(mEmptyView);
            }
        }
        mListShown = true;
        mList.setOnItemClickListener(mOnClickListener);
        if (mAdapter != null) {
            ListAdapter adapter = mAdapter;
            mAdapter = null;
            setListAdapter(adapter);
        } else {
            // We are starting without an adapter, so assume we won't
            // have our data right away and start with the progress indicator.
            if (mProgressContainer != null) {
                setListShown(false);
            }
        }
    }

    protected View createEmptyView() {
        FrameLayout eFrame = new FrameLayout(getActivity());
        View defaultEmptyView = createDefaultEmptyView();
        defaultEmptyView.setId(INTERNAL_DEFAULT_EMPTY_ID);
        eFrame.addView(defaultEmptyView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        return eFrame;
    }

    protected View createDefaultEmptyView() {
        TextView view = new TextView(getActivity());
        view.setText(getEmptyText());
        return view;
    }

    protected CharSequence getEmptyText() {
        return "暂无数据";
    }

    protected void refresh() {
    }

    protected LinearLayout createProgressContainer(Context context) {
        LinearLayout pframe = new LinearLayout(context);
        pframe.setOrientation(LinearLayout.VERTICAL);
        pframe.setVisibility(View.GONE);
        pframe.setGravity(Gravity.CENTER);
        ProgressBar progressBar = new ProgressBar(context);
        pframe.addView(progressBar, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return pframe;
    }
}
