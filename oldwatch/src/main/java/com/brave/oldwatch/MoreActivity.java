package com.brave.oldwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.brave.oldwatch.utils.AppInfo;

public class MoreActivity extends AppCompatActivity implements View.OnClickListener {


    private View mPasswordBtn,mLogoutBtn,mDevListBtn;
    private ImageView mUserLogo;
    private TextView mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        init();
    }

    private void init() {
        mUserLogo = (ImageView)findViewById(R.id.frame_more_user_logo);
        mPasswordBtn = findViewById(R.id.frame_more_password);
        mDevListBtn = findViewById(R.id.frame_more_dev_list);
        mLogoutBtn = findViewById(R.id.frame_more_logout);
        mUsername = (TextView)findViewById(R.id.frame_more_user_username);
        mUsername.setText(AppInfo.getString(MoreActivity.this,"username"));

        mPasswordBtn.setOnClickListener(this);
        mLogoutBtn.setOnClickListener(this);
        mDevListBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.frame_more_dev_list:
                Intent i = new Intent();
                i.setClass(MoreActivity.this, DevicesListActivity.class);
                startActivity(i);
                break;
            case R.id.frame_more_password:

                break;
            case R.id.frame_more_logout:
                logout();
                break;
        }
    }

    private void logout() {
        AppInfo.save(MoreActivity.this,"isLogin",false);
        Intent view = new Intent();
        view.setClass(MoreActivity.this, LoginActivity.class);
        startActivity(view);
        MoreActivity.this.finish();
    }

}
