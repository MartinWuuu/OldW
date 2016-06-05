package com.brave.oldwatch.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;


import java.util.List;

/**
 * Created by Brave on 2016/10/28.
 */

public class DevicesListAdapter extends BaseAdapter {

    private List<LinearLayout> list;


    public DevicesListAdapter(List<LinearLayout> l){
       list = l;
    }

    public void setData(List<LinearLayout> l){
        list = l;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return list.get(position);
    }
}
