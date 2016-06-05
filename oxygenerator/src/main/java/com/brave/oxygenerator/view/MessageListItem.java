package com.brave.oxygenerator.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.brave.oxygenerator.R;

/**
 * Created by Brave on 2016/10/8.
 */


public class MessageListItem extends LinearLayout {

    public MessageListItem(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_msg_list_item,this);
    }
}
