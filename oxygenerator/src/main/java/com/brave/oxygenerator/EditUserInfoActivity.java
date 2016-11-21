package com.brave.oxygenerator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.brave.oxygenerator.util.PreferenceUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class EditUserInfoActivity extends AppCompatActivity {

    private EditText mEditText;
    private String mHint,mParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        setTitle("更改"+getIntent().getStringExtra("title"));
        mHint = getIntent().getStringExtra("value");
        mParams = getIntent().getStringExtra("params");
        initView();
    }

    private void initView() {
        mEditText = (EditText) findViewById(R.id.activity_edit_user_info_edit);
        mEditText.setText(mHint);
    }

    private void update(){


        String info = mEditText.getText().toString();

        if(info.equals("")){
            Toast.makeText(this, "修改信息不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mParams.equals("male")){
            if(info.equals("男")){
                info = "0";
            }else if(info.equals("女")){
                info = "1";
            }else{
                info = "2";
            }
        }

        OkHttpUtils
                .post()
                .url("http://airsep.routeyo.net/airsep/lutsoft/sso/updateUser.action")
                .addParams(mParams,info)
                .addParams("userLogoKey", PreferenceUtil.getUserInfo(EditUserInfoActivity.this,"img"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(EditUserInfoActivity.this, "修改信息失败：请检查网络", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        try {
                            JSONObject jo = new JSONObject(response);
                            Toast.makeText(EditUserInfoActivity.this, jo.getString("errorMsg"), Toast.LENGTH_LONG).show();
                            if(jo.getInt("errorCode") == 1000){

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_confirm){
            update();
        }
        return super.onOptionsItemSelected(item);
    }
}
