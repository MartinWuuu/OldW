package com.brave.oxygenerator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brave.oxygenerator.util.PreferenceUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

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
                login();
            }
        });
    }

    private void login() {

        final String username = mUserNameView.getText().toString();
        final String password = mPasswordView.getText().toString();

        if(username.equals("") || password.equals("")){
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        mLoginBtn.setEnabled(false);

        OkHttpUtils
                .post()
                .url("http://airsep.routeyo.net/airsep/lutsoft/sso/login.action")
                .addParams("loginName", username)
                .addParams("loginPass", password)
                .addParams("ajax", "true")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "获取验证码失败：请检查网络", Toast.LENGTH_LONG).show();
                        mTipsView.setText( "登录失败：请检查网络");
                        mLoginBtn.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        Log.d("Login",response);

                        mLoginBtn.setEnabled(true);
                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.getInt("errorCode") == 1000){
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();

                                PreferenceUtil.setUserName(LoginActivity.this,username);
                                PreferenceUtil.setUserPassword(LoginActivity.this,password);
                                PreferenceUtil.setIsLogin(LoginActivity.this,true);

                                Intent i = new Intent();
                                i.setClass(LoginActivity.this,MainActivity.class);
                                startActivity(i);
                                LoginActivity.this.finish();

                            }else{
                                Toast.makeText(LoginActivity.this, jo.getString("errorMsg"), Toast.LENGTH_LONG).show();
                                mTipsView.setText(jo.getString("errorMsg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
