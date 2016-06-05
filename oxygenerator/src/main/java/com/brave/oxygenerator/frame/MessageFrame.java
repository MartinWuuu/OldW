package com.brave.oxygenerator.frame;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.brave.oxygenerator.R;
import com.brave.oxygenerator.adapter.MessageListAdapter;
import com.brave.oxygenerator.view.MessageListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brave on 2016/10/7.
 */

public class MessageFrame extends Fragment {

    private ListView mListView;
    private View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frame_message,container,false);
        initView();
        initData();
        return mView;
    }

    private void initData() {
        List<MessageListItem> list = new ArrayList<>();
        for (int i = 0; i < 9; i++){
            MessageListItem item = new MessageListItem(getActivity());
            list.add(item);
        }
        mListView.setAdapter(new MessageListAdapter(list));
    }

    private void initView() {
        mListView = (ListView)mView.findViewById(R.id.frame_message_list);
    }
}
