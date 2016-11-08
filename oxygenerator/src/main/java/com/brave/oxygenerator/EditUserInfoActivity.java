package com.brave.oxygenerator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;

public class EditUserInfoActivity extends AppCompatActivity {

    private EditText mEditText;
    private String mHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        setTitle("更改"+getIntent().getStringExtra("title"));
        mHint = getIntent().getStringExtra("value");
        initView();
    }

    private void initView() {
        mEditText = (EditText) findViewById(R.id.activity_edit_user_info_edit);
        mEditText.setText(mHint);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm, menu);
        return true;
    }
}
