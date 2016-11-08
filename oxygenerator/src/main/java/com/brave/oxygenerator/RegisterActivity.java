package com.brave.oxygenerator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brave.oxygenerator.util.PreferenceUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mPhoneView,mPasswordView,mPasswordConfirmView,mVerificationView;
    private TextView mRegisterBtn,mGetVerificationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resigter);
        initView();
    }


    private void initView() {
        mPhoneView = (EditText)findViewById(R.id.register_phone);
        mPasswordView = (EditText)findViewById(R.id.register_password);
        mPasswordConfirmView = (EditText)findViewById(R.id.register_password_confirm);
        mVerificationView = (EditText)findViewById(R.id.register_verification);
        mGetVerificationBtn = (TextView)findViewById(R.id.register_get_verification);
        mRegisterBtn = (TextView)findViewById(R.id.register_btn);
        mGetVerificationBtn.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
    }


    private void sendVerification() {
        final String email = mPhoneView.getText().toString();
        if(email.equals("")){
            Toast.makeText(this, "请输入要注册的手机号或E-mail帐号", Toast.LENGTH_SHORT).show();
            return;
        }
        mGetVerificationBtn.setEnabled(false);

        OkHttpUtils
                .post()
                .url("http://airsep.routeyo.net/airsep/lutsoft/sso/sendSeCode.action")
                .addParams("email", email)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(RegisterActivity.this, "获取验证码失败：请检查网络", Toast.LENGTH_LONG).show();
                        mGetVerificationBtn.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mGetVerificationBtn.setEnabled(true);
                        try {
                            JSONObject jo = new JSONObject(response);
                            Toast.makeText(RegisterActivity.this, jo.getString("errorMsg"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_btn:
                register();
                break;
            case R.id.register_get_verification:
                sendVerification();
                break;
        }
    }

    private void register() {
        final String email = mPhoneView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final String password_confirm = mPasswordConfirmView.getText().toString();
        final String verification = mVerificationView.getText().toString();

        if(email.equals("")){
            Toast.makeText(this, "手机号或E-mail帐号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.equals("")){
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password_confirm.equals("")){
            Toast.makeText(this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!(password.equals(password_confirm))){
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return;
        }

//        if(verification.equals("")){
//            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if(password.length() < 6 || password.length() > 16){
            Toast.makeText(this, "密码长度应在6-16位", Toast.LENGTH_SHORT).show();
            return;
        }

        mRegisterBtn.setEnabled(false);

        OkHttpUtils
                .post()
                .url("http://airsep.routeyo.net/airsep/lutsoft/sso/newUser.action")
                .addParams("loginName", email)
                .addParams("loginPass", password)
                .addParams("loginWithSDK", "true")
//                .addParams("seCode",verification)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(RegisterActivity.this, "获取数据失败：请检查网络", Toast.LENGTH_LONG).show();
                        mRegisterBtn.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mRegisterBtn.setEnabled(true);
                        try {
                            JSONObject jo = new JSONObject(response);
                            Toast.makeText(RegisterActivity.this, jo.getString("errorMsg"), Toast.LENGTH_LONG).show();
                            if(jo.getInt("errorCode") == 1000){
                                PreferenceUtil.setUserName(RegisterActivity.this,email);
                                PreferenceUtil.setUserPassword(RegisterActivity.this,password);
                                PreferenceUtil.setIsLogin(RegisterActivity.this,true);

                                Intent i = new Intent();
                                i.setClass(RegisterActivity.this,MainActivity.class);
                                startActivity(i);
                                RegisterActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });

    }
}
