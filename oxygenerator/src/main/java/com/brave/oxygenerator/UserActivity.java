package com.brave.oxygenerator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private View mHeaderView,mNameView,mAgeView,mSexView,mProfessionView,mPhoneView,mEmailView;
    private TextView mNameText,mAgeText,mSexText,mProfessionText,mPhoneText,mEmailText;
    private ImageView mHeaderImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initView();
    }

    private void initView() {
        mHeaderView = findViewById(R.id.activity_user_header_bar);
        mNameView = findViewById(R.id.activity_user_bar_name);
        mAgeView = findViewById(R.id.activity_user_bar_age);
        mSexView = findViewById(R.id.activity_user_bar_sex);
        mProfessionView = findViewById(R.id.activity_user_bar_profession);
        mPhoneView = findViewById(R.id.activity_user_bar_phone);
        mEmailView = findViewById(R.id.activity_user_bar_email);

        mNameText = (TextView)findViewById(R.id.activity_user_text_name);
        mAgeText = (TextView)findViewById(R.id.activity_user_text_age);
        mSexText = (TextView)findViewById(R.id.activity_user_text_sex);
        mProfessionText = (TextView)findViewById(R.id.activity_user_text_profession);
        mPhoneText = (TextView)findViewById(R.id.activity_user_text_phone);
        mEmailText = (TextView)findViewById(R.id.activity_user_text_email);

        mHeaderImg = (ImageView)findViewById(R.id.activity_user_header_img);

        mHeaderView.setOnClickListener(this);
        mNameView.setOnClickListener(this);
        mAgeView.setOnClickListener(this);
        mSexView.setOnClickListener(this);
        mProfessionView.setOnClickListener(this);
        mPhoneView.setOnClickListener(this);
        mEmailView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_user_header_bar:

                break;
            case R.id.activity_user_bar_name:
                editUserInfo("昵称","罗永浩");
                break;
            case R.id.activity_user_bar_age:
                editUserInfo("年龄","45");
                break;
            case R.id.activity_user_bar_sex:
                editUserInfo("性别","男");
                break;
            case R.id.activity_user_bar_profession:
                editUserInfo("职业","锤子科技CEO");
                break;
            case R.id.activity_user_bar_phone:
                editUserInfo("手机号","15222656877");
                break;
            case R.id.activity_user_bar_email:
                editUserInfo("E-mail","775347842@qq.com");
                break;
        }
    }

    private void editUserInfo(String s,String value) {
        Intent i = new Intent();
        i.setClass(this,EditUserInfoActivity.class);
        i.putExtra("title",s);
        i.putExtra("value",value);
        startActivity(i);
    }
}
