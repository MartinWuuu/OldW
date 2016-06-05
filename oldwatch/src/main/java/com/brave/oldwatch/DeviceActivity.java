package com.brave.oldwatch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.brave.oldwatch.adapter.DevicesListAdapter;
import com.brave.oldwatch.utils.AppInfo;
import com.brave.oldwatch.view.DevicesListItem;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class DeviceActivity extends AppCompatActivity implements View.OnTouchListener {

    private ImageView mHeaderView;
    private View mLocationBtn,mChatBtn,mCalledBtn,mHeartBtn,mLineBtn,mListenerBtn;
    private String info[];

    private String heart,blood,time;
    private long latitude,longitude;

    private boolean isGetInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        info = getIntent().getStringArrayExtra("device_info");
        setTitle(info[0]);
        initView();
        initData();
    }

    private void initData() {
        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "getInfo")
                .addParams("username", AppInfo.getString(this,"username"))
                .addParams("password", AppInfo.getString(this,"password"))
                .addParams("imei", info[1])
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("getMachine:","异常");
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

    private void parseJson(String response) throws JSONException{
        JSONObject jo = new JSONObject(response);
        int msgCode = jo.getInt("msgcode");
        String msg = jo.getString("msg");
        Log.d("msg:",msg);
        if (msgCode == 0){
            JSONObject data = jo.getJSONObject("data");
            heart = data.getString("heart_rate");
            blood = data.getString("blood_pressure");
            time = data.getString("updatetime");
            latitude = data.getLong("latitude");
            longitude = data.getLong("longitude");
            isGetInfo = true;
        }else{
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void initView() {
        mHeaderView = (ImageView)findViewById(R.id.activity_device_head);
        mLocationBtn = findViewById(R.id.activity_device_location);
        mChatBtn = findViewById(R.id.activity_device_chat);
        mCalledBtn = findViewById(R.id.activity_device_call);
        mHeartBtn = findViewById(R.id.activity_device_heart);
        mLineBtn = findViewById(R.id.activity_device_line);
        mListenerBtn = findViewById(R.id.activity_device_listener);

        mLocationBtn.setOnTouchListener(this);
        mChatBtn.setOnTouchListener(this);
        mCalledBtn.setOnTouchListener(this);
        mHeartBtn.setOnTouchListener(this);
        mLineBtn.setOnTouchListener(this);
        mListenerBtn.setOnTouchListener(this);
        OkHttpUtils
                .get()
                .url(info[3])
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(Bitmap response, int id) {
                        mHeaderView.setImageBitmap(response);
                    }
                });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                v.setBackgroundColor(getResources().getColor(R.color.pressed));
                break;
            case MotionEvent.ACTION_UP:
                v.setBackgroundColor(Color.WHITE);
                doAction(v.getId());
                break;
            case MotionEvent.ACTION_CANCEL:
                v.setBackgroundColor(Color.WHITE);
                break;
        }
        return true;
    }

    private void showAlert(int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (i == 0){
            builder.setTitle("血压信息");
            builder.setMessage("血压:"+blood+"\n\n时间:"+time);
        }else{
            builder.setTitle("心率信息");
            builder.setMessage("心率:"+heart+"\n\n时间:"+time);
        }
        builder.setPositiveButton("确认",null);
        builder.create().show();
    }

    private void doAction(int id) {

        if(isGetInfo == false){
            Toast.makeText(this, "数据加载中，请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (id){
            case R.id.activity_device_location:
                Intent i = new Intent();
                i.setClass(DeviceActivity.this,LocationActivity.class);
                i.putExtra("latlng",new long[]{latitude,longitude});
                i.putExtra("head",info[3]);
                startActivity(i);
                break;
            case R.id.activity_device_chat:
                Intent i1 = new Intent();
                i1.setClass(DeviceActivity.this,ChatActivity.class);
                startActivity(i1);
                break;
            case R.id.activity_device_call:
                Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + info[2]));
                startActivity(intentPhone);
                break;
            case R.id.activity_device_heart:
                showAlert(1);
                break;
            case R.id.activity_device_line:
                showAlert(0);
                break;
            case R.id.activity_device_listener:
                Toast.makeText(this, "敬请期待", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
