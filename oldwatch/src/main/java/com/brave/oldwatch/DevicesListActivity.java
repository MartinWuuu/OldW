package com.brave.oldwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.brave.oldwatch.adapter.DevicesListAdapter;
import com.brave.oldwatch.utils.AppInfo;
import com.brave.oldwatch.view.DevicesListItem;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class DevicesListActivity extends AppCompatActivity {

    private ListView mListView;
    private List<String[]> mDevicesInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);
        initView();
        initData();
    }

    private void initData() {
        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "getMachine")
                .addParams("username", AppInfo.getString(DevicesListActivity.this,"username"))
                .addParams("password", AppInfo.getString(DevicesListActivity.this,"password"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("getMachine:","异常");
                    }

                    @Override
                    public void onResponse(String response, int id) {
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
            JSONArray array = jo.getJSONArray("data");
            List<LinearLayout> list = new ArrayList<>();
            mDevicesInfoList = new ArrayList<>();
            for (int i = 0;i < array.length();i++){
                DevicesListItem item = new DevicesListItem(DevicesListActivity.this);

                JSONObject data = array.getJSONObject(i);
                String status = "在线";
                if(data.getString("isOnline").equals("null")){
                    status = "离线";
                }
                String time ="数据更新于"+ data.getString("updatetime");
                if (data.getString("updatetime").equals("null")){
                    time = "未上传过数据";
                }
                item.setParams(new String[]{data.getString("name"),status,time,AppInfo.HttpImgUrl+data.getString("image")});
                list.add(item);
                mDevicesInfoList.add(new String[]{data.getString("name"),data.getString("imei"),data.getString("phone"),AppInfo.HttpImgUrl+data.getString("image")});
            }
            mListView.setAdapter(new DevicesListAdapter(list));
        }else{
            Toast.makeText(DevicesListActivity.this, msg, Toast.LENGTH_SHORT).show();
            return;
        }

    }



    private void initView() {
        mListView = (ListView)findViewById(R.id.frame_home_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                i.setClass(DevicesListActivity.this, DeviceActivity.class);
                i.putExtra("device_info",mDevicesInfoList.get(position));
                startActivity(i);
            }
        });
    }
}
