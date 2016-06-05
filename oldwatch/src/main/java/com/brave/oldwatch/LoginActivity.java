package com.brave.oldwatch;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brave.oldwatch.utils.AppInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Request;

public class LoginActivity extends AppCompatActivity{

    private EditText mPasswordView, mUsernameView;

    private String username,password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUsernameView = (EditText) findViewById(R.id.activity_edit_email);
        mPasswordView = (EditText) findViewById(R.id.activity_edit_password);

        if(AppInfo.getBoolean(this,"isLogin")){
            toMain();
        }


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {

        username = mUsernameView.getText().toString();
        password = mPasswordView.getText().toString();

        if(username.equals("")){
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.equals("")){
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "login")
                .addParams("username", username)
                .addParams("password", password)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("error:","异常");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("response:",response);
                        Log.d("id:",id+"");
                        try {
                            parseJson(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void parseJson(String response) throws JSONException {
        JSONObject jo = new JSONObject(response);
        int msgCode = jo.getInt("msgcode");
        String msg = jo.getString("msg");
        if (msgCode == 0){
            JSONObject data = jo.getJSONObject("data");
            AppInfo.save(this,"username",username);
            AppInfo.save(this,"password",password);
            AppInfo.save(this,"isLogin",true);
            Toast.makeText(this, "欢迎，"+username, Toast.LENGTH_LONG).show();
            toMain();
        }else{
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void toMain(){
        Intent i = new Intent();
        i.setClass(LoginActivity.this,MainActivity.class);
        startActivity(i);
        this.finish();
    }


}

