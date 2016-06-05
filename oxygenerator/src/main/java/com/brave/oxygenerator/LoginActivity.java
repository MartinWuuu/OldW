package com.brave.oxygenerator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private EditText mUserNameView,mPasswordView;
    private ImageView mQQBtn,mWeCahtBtn,mWeiboBtn;
    private TextView mLoginBtn,mTipsView,mForgetView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        mUserNameView = (EditText)findViewById(R.id.login_username);
        mPasswordView = (EditText)findViewById(R.id.login_password);
        mLoginBtn = (TextView)findViewById(R.id.login_btn);
        mTipsView = (TextView)findViewById(R.id.login_tips);
        mForgetView = (TextView)findViewById(R.id.login_forget);
        mQQBtn = (ImageView)findViewById(R.id.login_qq);
        mWeCahtBtn = (ImageView)findViewById(R.id.login_wechat);
        mWeiboBtn = (ImageView)findViewById(R.id.login_weibo);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(LoginActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_register:
                Intent i = new Intent();
                i.setClass(this,RegisterActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
