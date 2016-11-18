package com.brave.oldwatch.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.brave.oldwatch.MainActivity;
import com.brave.oldwatch.R;
import com.brave.oldwatch.utils.AppInfo;
import com.brave.oldwatch.utils.Player;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Brave on 2016/11/18.
 */

public class NetService extends Service {

    private static final String TAG = "NetService";
    private static Date mLastTimeOfChat = new Date();
    private static Date mLastTimeOfNotice = new Date();

    private Player player;
    private String music;

    @Override
    public void onCreate() {
        super.onCreate();
        player = new Player(NetService.this, R.raw.sond);
        checkChatList();
        checkNoticeList();
        Log.d(TAG, "onCreate");
    }

    private void checkNoticeList() {
        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "getAlert")
                .addParams("username", AppInfo.getString(NetService.this,"username"))
                .addParams("password", AppInfo.getString(NetService.this,"password"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("getAlert:","异常");
                    }

                    @Override
                    public void onResponse(String response, int id) {
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

                                    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                                    try {
                                        Date date = sdf.parse(time);
                                        if (date.after(mLastTimeOfNotice)){
                                            Log.d(TAG, "onResponse: 系统消息:"+title+":"+time);
                                            mLastTimeOfNotice = date;
                                            final Intent intent = new Intent();
                                            intent.setAction(MainActivity.ACTION_UPDATEUI);
                                            intent.putExtra("notice", true);
                                            intent.putExtra("chat", false);
                                            sendBroadcast(intent);
                                            Toast.makeText(NetService.this,  name+"触发了"+title+"的警报", Toast.LENGTH_SHORT).show();
                                            player.play();
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }else{
                                Toast.makeText(NetService.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkNoticeList();
            }
        },2000);
    }

    private void checkChatList() {
        OkHttpUtils
                .get()
                .url(AppInfo.HttpUrl + "updateChat")
                .addParams("username", AppInfo.getString(getApplicationContext(),"username"))
                .addParams("password", AppInfo.getString(getApplicationContext(),"password"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("getAlert:","异常");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jo;
                        try {
                            jo = new JSONObject(response);
                            String msg = jo.getString("msg");
                            if(jo.getInt("msgcode") == 0){
                                JSONArray data = jo.getJSONArray("data");
                                for (int i = 0 ; i < data.length(); i++){
                                    final String name  = data.getJSONObject(i).getString("name");
                                    final String time = data.getJSONObject(i).getString("datetime");
                                    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                                    try {
                                        Date date = sdf.parse(time);
                                        if (date.after(mLastTimeOfChat)){
                                            Log.d(TAG, "onResponse: 聊天消息:"+name+":"+time);
                                            mLastTimeOfChat = date;
                                            final Intent intent = new Intent();
                                            intent.setAction(MainActivity.ACTION_UPDATEUI);
                                            intent.putExtra("notice", false);
                                            intent.putExtra("chat", true);
                                            sendBroadcast(intent);
                                            Toast.makeText(NetService.this, "有一条来自"+name+"新的聊天消息", Toast.LENGTH_SHORT).show();
                                            player.play();
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkChatList();
            }
        },2000);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
