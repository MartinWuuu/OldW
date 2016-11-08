package com.brave.oxygenerator;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.brave.oxygenerator.util.PreferenceUtil;

public class WelcomeActivity extends AppCompatActivity {

    int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

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
