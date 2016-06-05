package com.brave.oldwatch.frame;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.brave.oldwatch.ChatActivity;
import com.brave.oldwatch.DeviceActivity;
import com.brave.oldwatch.R;
import com.brave.oldwatch.adapter.DevicesListAdapter;
import com.brave.oldwatch.view.ChatListItem;

import java.util.ArrayList;
import java.util.List;


public class MessageFragment extends Fragment {

    private View mView;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_message, container, false);
        initView();
        return mView;
    }


    private void initView() {
        mListView = (ListView)mView.findViewById(R.id.frame_message_list);
        List<LinearLayout> list = new ArrayList<>();
        for (int i = 0; i < 4; i++){
            ChatListItem item = new ChatListItem(getActivity());
            list.add(item);
        }
        mListView.setAdapter(new DevicesListAdapter(list));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                i.setClass(getActivity(), ChatActivity.class);
                getActivity().startActivity(i);
            }
        });
    }
}
