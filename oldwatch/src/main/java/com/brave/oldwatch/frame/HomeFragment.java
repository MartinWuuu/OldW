package com.brave.oldwatch.frame;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.brave.oldwatch.DeviceActivity;
import com.brave.oldwatch.R;
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

public class HomeFragment extends Fragment {


    private View mView;
    private ListView mListView;

    private List<String[]> mDevicesInfoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        initData();
        return mView;
    }

    private void initData() {
        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "getMachine")
                .addParams("username", AppInfo.getString(getActivity(),"username"))
                .addParams("password", AppInfo.getString(getActivity(),"password"))
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

    private void parseJson(String response) throws JSONException {
        JSONObject jo = new JSONObject(response);
        int msgCode = jo.getInt("msgcode");
        String msg = jo.getString("msg");
        Log.d("msg:",msg);
        if (msgCode == 0){
            JSONArray array = jo.getJSONArray("data");
            List<LinearLayout> list = new ArrayList<>();
            mDevicesInfoList = new ArrayList<>();
            for (int i = 0;i < array.length();i++){
                JSONObject data = array.getJSONObject(i);
                DevicesListItem item = new DevicesListItem(getActivity());
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
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            return;
        }

    }



    private void initView() {
        mListView = (ListView)mView.findViewById(R.id.frame_home_list);
//        List<LinearLayout> list = new ArrayList<>();
//        for (int i = 0; i < 3; i++){
//            DevicesListItem item = new DevicesListItem(getActivity());
//            list.add(item);
//        }
//        mListView.setAdapter(new DevicesListAdapter(list));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                i.setClass(getActivity(), DeviceActivity.class);
                i.putExtra("device_info",mDevicesInfoList.get(position));
                getActivity().startActivity(i);
            }
        });
    }

}
