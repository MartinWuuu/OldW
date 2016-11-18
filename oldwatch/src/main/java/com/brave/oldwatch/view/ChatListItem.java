package com.brave.oldwatch.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brave.oldwatch.R;
import com.brave.oldwatch.utils.AppInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.internal.http.HttpDate;

/**
 * Created by Brave on 2016/10/29.
 */

public class ChatListItem extends LinearLayout {

    private ImageView mLogoView;
    private TextView mNameView,mTimeView;

    private Context mContext;


    public ChatListItem(Context context){
        super(context);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_chat_list__item,this);
        mLogoView = (ImageView)view.findViewById(R.id.view_chat_list_item_logo);
        mNameView = (TextView)view.findViewById(R.id.view_chat_list_item_name);
        mTimeView = (TextView)view.findViewById(R.id.view_chat_list_item_time);
    }

    public void setParams(String time,String image,String name){
        mTimeView.setText(time);
        mNameView.setText(name);
        setLogo(image);
    }

    private void setLogo(String path){
        OkHttpUtils
                .get()
                .url(AppInfo.HttpImgUrl + path)
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        mLogoView.setImageBitmap(response);
                    }
                });
    }



}
