package com.brave.oldwatch.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brave.oldwatch.R;
import com.brave.oldwatch.utils.AppInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import okhttp3.Call;

/**
 * Created by Brave on 2016/10/31.
 */

public class ChatLeftItem extends LinearLayout {

    private ImageView mLogoView,mPlayingBtn;
    private TextView mTextView,mTimeView;
    private View mBtnView;

    public ChatLeftItem(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_chat_item_left,this);
        mLogoView = (ImageView)view.findViewById(R.id.view_chat_item_left_logo);
        mPlayingBtn = (ImageView)view.findViewById(R.id.view_chat_item_left_playing);
        mBtnView = view.findViewById(R.id.view_chat_item_left_btn);
        mTextView = (TextView)view.findViewById(R.id.view_chat_item_left_text);
        mTimeView = (TextView)view.findViewById(R.id.view_chat_item_left_time);
    }

    public void setParams(String img, String text,String time,String path){
        mTextView.setText(text);
        mTimeView.setText("接收于 "+time);
        OkHttpUtils
                .get()
                .url(AppInfo.HttpImgUrl + img)
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
