package com.brave.oldwatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.brave.oldwatch.adapter.DevicesListAdapter;
import com.brave.oldwatch.view.ChatLeftItem;
import com.brave.oldwatch.view.ChatListItem;
import com.brave.oldwatch.view.ChatRecordingBackbround;
import com.brave.oldwatch.view.ChatRightItem;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ListView mListView;
    private ImageView mFab;
    private ChatRecordingBackbround mChatBg;
    private View mRecordingView;

    private DevicesListAdapter mDevicesListAdapter;

    private List<LinearLayout> mDatalist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        setTitle("陈尚");
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.activity_chat_list);
        mFab = (ImageView)findViewById(R.id.activity_chat_fab);
        mChatBg = (ChatRecordingBackbround)findViewById(R.id.activity_chat_recording_bg);
        mRecordingView = findViewById(R.id.activity_chat_recording_view);
        mDatalist = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            if(i % 2 == 1){
                ChatLeftItem item = new ChatLeftItem(this);
                mDatalist.add(item);
            }else{
                ChatRightItem item = new ChatRightItem(this);
                mDatalist.add(item);
            }

        }
        mDevicesListAdapter = new DevicesListAdapter(mDatalist);
        mListView.setAdapter(mDevicesListAdapter);
        mListView.setSelection(ListView.FOCUS_DOWN);


        mFab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mRecordingView.setVisibility(View.VISIBLE);
                    mChatBg.recording();
                }else  if(event.getAction() == MotionEvent.ACTION_UP){

                    mRecordingView.setVisibility(View.GONE);
                    mChatBg.recoeded();
                    ChatRightItem item = new ChatRightItem(ChatActivity.this);
                    mDatalist.add(item);
                    mDevicesListAdapter.setData(mDatalist);
                    mListView.setSelection(ListView.FOCUS_DOWN);
                }
                return true;
            }
        });

    }
}
