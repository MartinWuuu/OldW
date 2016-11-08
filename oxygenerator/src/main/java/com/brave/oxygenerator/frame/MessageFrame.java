package com.brave.oxygenerator.frame;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showMoreAlertDialog(position);
            }
        });
    }

    private void showMoreAlertDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("系统消息");
        builder.setMessage("您的机器已经连续工作3小时\n您的机器已经连续工作3小时\n您的机器已经连续工作3小时\n您的机器已经连续工作3小时\n您的机器已经连续工作3小时");
        builder.setPositiveButton("确定",null);
        builder.create().show();
    }

    private void initView() {
        mListView = (ListView)mView.findViewById(R.id.frame_message_list);
    }
}
