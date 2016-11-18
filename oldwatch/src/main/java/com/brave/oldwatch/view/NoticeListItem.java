package com.brave.oldwatch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brave.oldwatch.R;



public class NoticeListItem extends LinearLayout {


    private TextView mNameView,mContentView,mTimeView;

    public NoticeListItem(Context context){
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_notice_list_item,this);
        mNameView = (TextView)view.findViewById(R.id.view_msg_list_item_title);
        mContentView = (TextView)view.findViewById(R.id.view_msg_list_item_tips);
        mTimeView = (TextView)view.findViewById(R.id.view_msg_list_item_time);
    }

    public void setParams(String s1 , String s2 , String s3){
        mNameView.setText(s1);
        mContentView.setText(s2);
        mTimeView.setText(s3);
    }


}
