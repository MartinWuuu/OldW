package com.brave.oldwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brave.oldwatch.adapter.DevicesListAdapter;
import com.brave.oldwatch.utils.AppInfo;
import com.brave.oldwatch.utils.Player;
import com.brave.oldwatch.utils.Recorder;
import com.brave.oldwatch.view.ChatLeftItem;
import com.brave.oldwatch.view.ChatListItem;
import com.brave.oldwatch.view.ChatRecordingBackbround;
import com.brave.oldwatch.view.ChatRightItem;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private ListView mListView;
    private ImageView mFab;
    private ChatRecordingBackbround mChatBg;
    private View mRecordingView;
    private TextView mRecordingTips;

    private DevicesListAdapter mDevicesListAdapter;

    private List<LinearLayout> mDatalist;

    private String[] paths;
    private String IMEI;
    private int page = 1;
    private boolean isCancel;
    private Player player;
    private Recorder recorder;

    private UpdateUIBroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle(getIntent().getStringExtra("name"));
        IMEI = getIntent().getStringExtra("imei");
        player = new Player();
        recorder = new Recorder();
        initView();

        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.ACTION_UPDATEUI);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);
    }

    private void refreshChatList(){
        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "getChat")
                .addParams("username", AppInfo.getString(ChatActivity.this,"username"))
                .addParams("password", AppInfo.getString(ChatActivity.this,"password"))
                .addParams("imei",IMEI)
                .addParams("page",page+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("getAlert:","异常");
                        Toast.makeText(ChatActivity.this, "获取数据出现异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("response:",response);

                        JSONObject jo;
                        try {
                            jo = new JSONObject(response);
                            String msg = jo.getString("msg");
                            if(jo.getInt("msgcode") == 0){

                                JSONArray data = jo.getJSONArray("data");

                                mDatalist.clear();

                                paths = new String[data.length()];

                                for (int i = data.length() - 1,j = 0; i >= 0 ; i--,j++){

                                    final String text = data.getJSONObject(i).getString("text");
                                    final String datetime = data.getJSONObject(i).getString("datetime");
                                    final String path = AppInfo.HttpAmrUrl+data.getJSONObject(i).getString("imei") + "/" +data.getJSONObject(i).getString("path")+".amr";
                                    final String image = data.getJSONObject(i).getString("image");
                                    final String username = data.getJSONObject(i).getString("username");

                                    paths[j] = path;

                                    if (username.equals("")){
                                        ChatLeftItem item = new ChatLeftItem(ChatActivity.this);
                                        item.setParams(image,text,datetime,path);
                                        mDatalist.add(item);
                                    }else {
                                        ChatRightItem item = new ChatRightItem(ChatActivity.this);
                                        item.setParams(text,datetime,path);
                                        mDatalist.add(item);
                                    }
                                }
                                mDevicesListAdapter.setData(mDatalist);
                                mListView.setSelection(ListView.FOCUS_DOWN);
                            }else{
                                Toast.makeText(ChatActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void sendChat(final File file) {
        OkHttpUtils
                .post()
                .url(AppInfo.HttpUrl + "postRecord")
                .addParams("username", AppInfo.getString(ChatActivity.this,"username"))
                .addParams("password", AppInfo.getString(ChatActivity.this,"password"))
                .addParams("imei",IMEI)
                .addFile("rec", file.getName(), file)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("postRecord:","异常");
                        Toast.makeText(ChatActivity.this, "发送语音消息出现异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("postRecord:",response);
                        JSONObject jo;
                        try {
                            jo = new JSONObject(response);
                            String msg = jo.getString("msg");
                            if(jo.getInt("msgcode") == 0){
                                refreshChatList();
                            }else{
                                Toast.makeText(ChatActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.activity_chat_list);
        mFab = (ImageView)findViewById(R.id.activity_chat_fab);
        mRecordingTips = (TextView)findViewById(R.id.activity_chat_recording_view_tips);
        mChatBg = (ChatRecordingBackbround)findViewById(R.id.activity_chat_recording_bg);
        mRecordingView = findViewById(R.id.activity_chat_recording_view);
        mDatalist = new ArrayList<>();
        mDevicesListAdapter = new DevicesListAdapter(mDatalist);
        mListView.setEnabled(true);
        mListView.setAdapter(mDevicesListAdapter);
        mListView.setSelection(ListView.FOCUS_DOWN);

        refreshChatList();

        mFab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                isCancel = event.getY() < 0;
                if(event.getY() < 0){
                    mRecordingTips.setText("松开手指取消发送");
                }else{
                    mRecordingTips.setText("松开发送，上滑取消");
                }

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mRecordingView.setVisibility(View.VISIBLE);
                    mChatBg.recording();
                    try{
                        recorder.start();
                    }catch (Exception e){
                        Log.d(TAG, "onTouch: start");
                    }
                }else  if(event.getAction() == MotionEvent.ACTION_UP){
                    mRecordingView.setVisibility(View.GONE);
                    mChatBg.recoeded();
                    File file;
                    try{
                        file = recorder.stop();
                        if (!isCancel){
                            sendChat(file);
                        }
                    }catch (Exception e){
                        Log.d(TAG, "onTouch: stop");
                    }
                    mRecordingTips.setText("松开发送，上滑取消");
                }
                return true;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("play:",paths[position]);
                player.playUrl(paths[position]);
            }
        });

    }

    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("chat",false)){
                refreshChatList();
            }
        }
    }

    @Override
    protected void onDestroy() {
        System.out.println("onDestroy");
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}
