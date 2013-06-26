package com.marker.markcar.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.marker.markcar.R;
import com.marker.markcar.content.MallInfo;

public class MallListAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final ArrayList<MallInfo> mList;

    public MallListAdapter(Context context, ArrayList<MallInfo> list) {
        mInflater = LayoutInflater.from(context);
        mList = list;
    }

    public MallInfo getMallInfoById(long id) {
        for (MallInfo mi : mList) {
            if (mi.getId() == id) {
                return mi;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MallInfo mall = mList.get(position);
        View v = convertView;
        if (v == null) {
            v = mInflater.inflate(R.layout.mall_list_item, null);
        }
        ((TextView) v.findViewById(R.id.mall_name)).setText(mall.getName());
        return v;
    }
}
