package com.nebulas.io.ui.list.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.nebulas.io.util.CollectionUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * Time: 下午3:35
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mData;
    protected LayoutInflater mInflater;

    public BaseListAdapter(Context context) {
        this(context, null);
    }

    public BaseListAdapter(Context context, List<T> data) {
        mContext = context;
        if (data != null) {
            mData = new ArrayList<T>(data);
        }

        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public List<T> getData() {
        return mData;
    }

    /**
     * 设置数据
     *
     * @param data
     */
    public void setData(List<T> data) {
        if (data == null) {
            mData = null;
        } else {
            mData = new ArrayList<T>(data);
        }
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     *
     * @param data
     */
    public void appendData(List<T> data) {
        if (CollectionUtils.isEmpty(mData)) {
            setData(data);
        } else {
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 替换某条数据
     *
     * @param data
     */
    public void replaceData(T data,int position) {
        if (!CollectionUtils.isEmpty(mData)&&mData.size()>position) {
            mData.set(position,data);
            notifyDataSetChanged();
        }
    }

    /**
     * 清除数据
     */
    public void clear() {
        if (mData != null) {
            mData.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected String getText(int textRes) {
        return mContext.getString(textRes);
    }

    protected String getText(int res,Object ...args) {
        return mContext.getString(res, args);
    }

    protected int getColor(int colorRes) {
        return mContext.getResources().getColor(colorRes);
    }


    public void appendDataToHeader(List<T> data) {
        if (CollectionUtils.isEmpty(mData)) {
            setData(data);
        } else {
            for (int i = 0; i < data.size(); i++) {
                mData.add(i, data.get(i));
            }
            notifyDataSetChanged();
        }
    }
}
