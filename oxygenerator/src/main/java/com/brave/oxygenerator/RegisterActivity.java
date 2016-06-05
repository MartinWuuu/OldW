package com.brave.oxygenerator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private EditText mPhoneView,mPasswordView,getmPasswordConfirmView,mVerificationView;
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
        getmPasswordConfirmView = (EditText)findViewById(R.id.register_password_confirm);
        mVerificationView = (EditText)findViewById(R.id.register_verification);
        mGetVerificationBtn = (TextView)findViewById(R.id.register_get_verification);
        mRegisterBtn = (TextView)findViewById(R.id.register_btn);
    }
}
