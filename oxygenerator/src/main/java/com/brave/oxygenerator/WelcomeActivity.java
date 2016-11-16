package com.brave.oxygenerator;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.brave.oxygenerator.util.PreferenceUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import okhttp3.OkHttpClient;

public class WelcomeActivity extends AppCompatActivity {

    int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        OkHttpUtils.initClient(okHttpClient);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String passwordStr = PreferenceUtil.getGesturePassword(WelcomeActivity.this);
                boolean isLogin = PreferenceUtil.isLogin(WelcomeActivity.this);
                Intent intent;
                if (passwordStr == "") {
                    if(isLogin){
                        intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    }else{
                        intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    }
                } else {
                    intent = new Intent(WelcomeActivity.this, UnlockActivity.class);
                }
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }, 3000);
    }
}
