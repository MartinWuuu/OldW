package com.brave.oldwatch.frame;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.brave.oldwatch.ChatActivity;
import com.brave.oldwatch.R;
import com.brave.oldwatch.adapter.DevicesListAdapter;
import com.brave.oldwatch.utils.AppInfo;
import com.brave.oldwatch.view.ChatListItem;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class MessageFragment extends Fragment {

    private View mView;
    private ListView mListView;

    private String[] ids,names;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_message, container, false);
        initView();
        initData();
        return mView;
    }


    private void initView() {
        mListView = (ListView)mView.findViewById(R.id.frame_message_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                i.setClass(getActivity(), ChatActivity.class);
                i.putExtra("imei",ids[position]);
                i.putExtra("name",names[position]);
                getActivity().startActivity(i);
            }
        });
    }

    private void initData() {
        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "updateChat")
                .addParams("username", AppInfo.getString(getActivity(),"username"))
                .addParams("password", AppInfo.getString(getActivity(),"password"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("getAlert:","异常");
                        Toast.makeText(getActivity(), "获取数据出现异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("response:",response);

                        JSONObject jo;
                        try {
                            jo = new JSONObject(response);
                            String msg = jo.getString("msg");
                            if(jo.getInt("msgcode") == 0){
                                JSONArray data = jo.getJSONArray("data");
                                ids = new String[data.length()];
                                names = new String[data.length()];
                                List<LinearLayout> list = new ArrayList<>();
                                for (int i = 0 ; i < data.length(); i++){

                                    final String imei = data.getJSONObject(i).getString("imei");
                                    final String image = data.getJSONObject(i).getString("image");
                                    final String name  = data.getJSONObject(i).getString("name");
                                    final String time = data.getJSONObject(i).getString("datetime");

                                    ChatListItem item = new ChatListItem(getActivity());
                                    item.setParams(time,image,name);
                                    list.add(item);

                                    ids[i] = imei;
                                    names[i] = name;
                                }
                                mListView.setAdapter(new DevicesListAdapter(list));
                            }else{
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
