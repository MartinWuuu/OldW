package com.brave.oldwatch;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.brave.oldwatch.frame.HomeFragment;
import com.brave.oldwatch.frame.MessageFragment;
import com.brave.oldwatch.frame.MoreFragment;
import com.brave.oldwatch.frame.NoticeFragment;
import com.brave.oldwatch.service.NetService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView mHomeBtn,mNoticeBtn,mMessageBtn,mMoreBtn;

    public static final String ACTION_UPDATEUI = "action.updateUI";
    private UpdateUIBroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        onSelected(1);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATEUI);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);

        Intent i = new Intent();
        i.setClass(this, NetService.class);
        startService(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_normal,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_notice){
            Intent i = new Intent();
            i.setClass(this,NoticeActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mHomeBtn = (ImageView)findViewById(R.id.activity_main_home);
        mNoticeBtn = (ImageView)findViewById(R.id.activity_main_notice);
        mMessageBtn = (ImageView)findViewById(R.id.activity_main_msg);
        mMoreBtn  = (ImageView)findViewById(R.id.activity_main_more);
        mHomeBtn.setOnClickListener(this);
        mNoticeBtn.setOnClickListener(this);
        mMessageBtn.setOnClickListener(this);
        mMoreBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
     switch (v.getId()){
         case R.id.activity_main_home:
            onSelected(1);
             break;
         case R.id.activity_main_notice:
             onSelected(2);
             break;
         case R.id.activity_main_msg:
             onSelected(3);
             break;
         case R.id.activity_main_more:
             onSelected(4);
             break;
     }
    }

    private void onSelected(int i) {
        clear();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (i){
            case 1:
                mHomeBtn.setImageResource(R.mipmap.ic_bottomtabbar_feed_enable);
                setTitle("道易成社区矫正管理控制平台");
                transaction.replace(R.id.activity_main_frame, new HomeFragment());
                break;
            case 2:
                mNoticeBtn.setImageResource(R.mipmap.ic_bottomtabbar_notification_enable);
                setTitle("通知");
                transaction.replace(R.id.activity_main_frame, new NoticeFragment());
                break;
            case 3:
                mMessageBtn.setImageResource(R.mipmap.ic_bottomtabbar_message_enable);
                setTitle("聊天");
                transaction.replace(R.id.activity_main_frame, new MessageFragment());
                break;
            case 4:
                mMoreBtn.setImageResource(R.mipmap.ic_bottomtabbar_more_enable);
                setTitle("更多");
                transaction.replace(R.id.activity_main_frame, new MoreFragment());
                break;
        }
        transaction.commit();
    }

    private void clear() {
        mHomeBtn.setImageResource(R.mipmap.ic_bottomtabbar_feed);
        mNoticeBtn.setImageResource(R.mipmap.ic_bottomtabbar_notification);
        mMessageBtn.setImageResource(R.mipmap.ic_bottomtabbar_message);
        mMoreBtn.setImageResource(R.mipmap.ic_bottomtabbar_more);
    }

    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

//            if(intent.getBooleanExtra("notice",false)){
//                Toast.makeText(MainActivity.this, intent.getStringExtra("msg"), Toast.LENGTH_SHORT).show();
//            }

        }

    }

    @Override
    protected void onDestroy() {
        System.out.println("onDestroy");
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}
