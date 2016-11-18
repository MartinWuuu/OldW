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

public class ChatRightItem extends LinearLayout {
    private ImageView mLogoView,mPlayingBtn;
    private TextView mTextView,mTimeView;
    private View mBtnView;

    public ChatRightItem(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_chat_item_right,this);
        mLogoView = (ImageView)view.findViewById(R.id.view_chat_item_right_logo);
        mPlayingBtn = (ImageView)view.findViewById(R.id.view_chat_item_right_playing);
        mBtnView = view.findViewById(R.id.view_chat_item_right_btn);
        mTextView = (TextView)view.findViewById(R.id.view_chat_item_right_text);
        mTimeView = (TextView)view.findViewById(R.id.view_chat_item_right_time);
    }

    public void setParams(String text,String time,String path){
        mTextView.setText(text);
        mTimeView.setText("接收于 "+time);

    }
}
