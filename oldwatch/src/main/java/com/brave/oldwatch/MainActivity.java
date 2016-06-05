package com.brave.oldwatch;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.SDKInitializer;
import com.brave.oldwatch.frame.HomeFragment;
import com.brave.oldwatch.frame.MessageFragment;
import com.brave.oldwatch.frame.MoreFragment;
import com.brave.oldwatch.frame.NoticeFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView mHomeBtn,mNoticeBtn,mMessageBtn,mMoreBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SDKInitializer.initialize(getApplicationContext());
        initView();
        onSelected(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_notice){
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
                setTitle("智能腕带");
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


}
