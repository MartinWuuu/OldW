package com.brave.oldwatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.brave.oldwatch.adapter.DevicesListAdapter;
import com.brave.oldwatch.utils.AppInfo;
import com.brave.oldwatch.view.NoticeListItem;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class NoticeActivity extends AppCompatActivity {

    private static final String TAG = "NoticeActivity";

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        initView();
        initData();
    }

    private void initView() {
        mListView = (ListView)findViewById(R.id.activity_notice_list);

    }

    private void initData() {
        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "getAlert")
                .addParams("username", AppInfo.getString(this,"username"))
                .addParams("password", AppInfo.getString(this,"password"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("getAlert:","异常");
                        Toast.makeText(NoticeActivity.this, "获取数据出现异常", Toast.LENGTH_SHORT).show();
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
                                List<LinearLayout> list = new ArrayList<>();
                                for (int i = 0 ; i < data.length(); i++){
                                    final String title = data.getJSONObject(i).getString("title");
                                    final String name = data.getJSONObject(i).getString("name");
                                    final String time = data.getJSONObject(i).getString("time");
                                    Log.d(TAG, "\n\n\nonResponse:\n "+name+"\n"+title+"\n"+time);
                                    NoticeListItem item = new NoticeListItem(NoticeActivity.this);
                                    item.setParams(name,title,time);
                                    list.add(item);
                                }
                                mListView.setAdapter(new DevicesListAdapter(list));
                            }else{
                                Toast.makeText(NoticeActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
