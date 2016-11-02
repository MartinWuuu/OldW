package com.brave.bookshop.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.brave.bookshop.view.BookListItem;

import java.util.List;


public class BookListAdapter extends BaseAdapter {

    private List<BookListItem> list;

    public BookListAdapter(List<BookListItem> l ){
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

