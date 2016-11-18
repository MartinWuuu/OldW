package com.brave.oldwatch;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.brave.oldwatch.utils.AppInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class DeviceActivity extends AppCompatActivity implements View.OnTouchListener {

    private ImageView mHeaderView;
    private View mLocationBtn,mChatBtn,mCalledBtn,mHeartBtn,mLineBtn,mListenerBtn;
    private static String info[];

    private double latitude,longitude;
    private double[] lats,lngs;

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
        if (msgCode == 0){
            if(jo.isNull("data")){
                Toast.makeText(this, "该设备处于离线状态", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject data = jo.getJSONObject("data");
            latitude = data.getDouble("latitude");
            longitude = data.getDouble("longitude");

            if(data.isNull("dzwl")){
                lats = null;
                lngs = null;
            }else{
                String list = data.getString("dzwl");

                JSONArray histoy = new JSONArray(list);
                lats = new double[histoy.length()];
                lngs = new double[histoy.length()];
                for (int i = 0; i < histoy.length();i++){
                    lats[i] = histoy.getJSONObject(i).getDouble("lat");
                    lngs[i] = histoy.getJSONObject(i).getDouble("lng");
                }
            }
            isGetInfo = true;
        }else{
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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

    private void showAlert(String[] s){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(s[0]);
        builder.setMessage(s[1] + s[2] + "\n\n数据更新时间：" + s[3]);
        builder.setPositiveButton("确认",null);
        builder.create().show();
    }

    private void getHeartData(){
        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "getInfo_xl")
                .addParams("username", AppInfo.getString(this,"username"))
                .addParams("password", AppInfo.getString(this,"password"))
                .addParams("imei", info[1])
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("getInfo_xl:","异常");
                        Toast.makeText(DeviceActivity.this, "获取数据出现异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("response:",response);
                        JSONObject jo;
                        try {
                            jo = new JSONObject(response);
                            String msg = jo.getString("msg");

                            if(jo.getInt("msgcode") == 0){
                                if(jo.isNull("data")){
                                    Toast.makeText(DeviceActivity.this, "为获取到任何心率记录", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                JSONArray data = jo.getJSONArray("data");
                                final String content = data.getJSONObject(0).getString("content");
                                final String time = data.getJSONObject(0).getString("time");
                                showAlert(new String[]{"心率信息","心率：",content,time});
                            }else{
                                Toast.makeText(DeviceActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getXYData(){
        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "getInfo_xy")
                .addParams("username", AppInfo.getString(this,"username"))
                .addParams("password", AppInfo.getString(this,"password"))
                .addParams("imei", info[1])
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("getInfo_xy:","异常");
                        Toast.makeText(DeviceActivity.this, "获取数据出现异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("response:",response);
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(response);
                            String msg = jo.getString("msg");

                            if(jo.getInt("msgcode") == 0){
                                if(jo.isNull("data")){
                                    Toast.makeText(DeviceActivity.this, "为获取到任何血压记录", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                JSONArray data = jo.getJSONArray("data");
                                final String content = data.getJSONObject(0).getString("content");
                                final String time = data.getJSONObject(0).getString("time");
                                showAlert(new String[]{"血压信息","血压：",content,time});
                            }else{
                                Toast.makeText(DeviceActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void call(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("拨打电话");
        builder.setMessage("是否拨打电话:"+info[2]);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + info[2]));
                startActivity(intentPhone);
            }
        });
        builder.setNegativeButton("取消",null);
        builder.create().show();
    }

    private void toChat(){
        Intent i1 = new Intent();
        i1.setClass(DeviceActivity.this,ChatActivity.class);
        i1.putExtra("imei", info[1]);
        i1.putExtra("name", info[0]);
        startActivity(i1);
    }

    private void toLocation(){
        if(isGetInfo){
            Intent i = new Intent();
            i.setClass(DeviceActivity.this,LocationActivity.class);
            i.putExtra("latlng",new double[]{latitude,longitude});
            i.putExtra("head",info[3]);
            i.putExtra("imei",info[1]);
            i.putExtra("lats",lats);
            i.putExtra("lngs",lngs);
            startActivity(i);
        }else{
            Toast.makeText(this, "未获取到当前设备的定位信息，请检查设备是否在线？", Toast.LENGTH_SHORT).show();
        }
    }

    private void doAction(int id) {
        switch (id){
            case R.id.activity_device_location:
                toLocation();
                break;
            case R.id.activity_device_chat:
               toChat();
                break;
            case R.id.activity_device_call:
                call();
                break;
            case R.id.activity_device_heart:
                getHeartData();
                break;
            case R.id.activity_device_line:
                getXYData();
                break;
            case R.id.activity_device_listener:
                Toast.makeText(this, "敬请期待", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
