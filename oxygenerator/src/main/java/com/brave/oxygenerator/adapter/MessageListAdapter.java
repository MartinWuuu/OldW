package com.brave.oxygenerator.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.brave.oxygenerator.view.MessageListItem;

import java.util.List;

/**
 * Created by Brave on 2016/10/8.
 */

public class MessageListAdapter extends BaseAdapter {

    private List<MessageListItem> list;

    public MessageListAdapter( List<MessageListItem> l){
        list = l;
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
